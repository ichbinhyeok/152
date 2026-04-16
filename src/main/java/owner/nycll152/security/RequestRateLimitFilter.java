package owner.nycll152.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import owner.nycll152.config.AppProperties;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestRateLimitFilter extends OncePerRequestFilter {

    private final Map<String, CounterWindow> counters = new ConcurrentHashMap<>();
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public RequestRateLimitFilter(AppProperties appProperties, ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!appProperties.getRateLimit().isEnabled()) {
            return true;
        }

        if (!HttpMethod.POST.matches(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        return !"/api/checker/run".equals(path)
                && !"/api/leads/capture".equals(path)
                && !"/api/leads/event".equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        RateLimitWindow limitWindow = limitWindowFor(request.getRequestURI());
        String key = request.getRequestURI() + ":" + clientAddress(request);

        if (!tryConsume(key, limitWindow.limit(), limitWindow.window())) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "error", "rate_limit_exceeded",
                    "message", "Too many requests. Slow down before retrying."
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String clientAddress(HttpServletRequest request) {
        String forwarded = forwardedFor(request.getHeader("Forwarded"));
        if (StringUtils.hasText(forwarded)) {
            return forwarded;
        }

        String xForwardedFor = firstForwardedFor(request.getHeader("X-Forwarded-For"));
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor;
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }

    private String firstForwardedFor(String headerValue) {
        if (!StringUtils.hasText(headerValue)) {
            return null;
        }

        return headerValue.split(",")[0].trim();
    }

    private String forwardedFor(String headerValue) {
        if (!StringUtils.hasText(headerValue)) {
            return null;
        }

        for (String segment : headerValue.split(",")) {
            for (String part : segment.split(";")) {
                String trimmed = part.trim();
                if (!trimmed.regionMatches(true, 0, "for=", 0, 4)) {
                    continue;
                }

                String value = trimmed.substring(4).trim();
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
                    value = value.substring(1, value.length() - 1);
                }
                if (value.startsWith("[") && value.endsWith("]") && value.length() > 1) {
                    value = value.substring(1, value.length() - 1);
                }
                return value;
            }
        }

        return null;
    }

    private synchronized boolean tryConsume(String key, int limit, Duration window) {
        long now = System.currentTimeMillis();
        CounterWindow current = counters.get(key);

        if (current == null || now - current.windowStartedAt() >= window.toMillis()) {
            counters.put(key, new CounterWindow(now, 1));
            return true;
        }

        if (current.count() >= limit) {
            return false;
        }

        counters.put(key, new CounterWindow(current.windowStartedAt(), current.count() + 1));
        return true;
    }

    private RateLimitWindow limitWindowFor(String path) {
        if ("/api/checker/run".equals(path)) {
            return new RateLimitWindow(appProperties.getRateLimit().getCheckerPerMinute(), Duration.ofMinutes(1));
        }

        if ("/api/leads/capture".equals(path)) {
            return new RateLimitWindow(appProperties.getRateLimit().getLeadsPerHour(), Duration.ofHours(1));
        }

        return new RateLimitWindow(appProperties.getRateLimit().getEventsPerMinute(), Duration.ofMinutes(1));
    }

    private record CounterWindow(long windowStartedAt, int count) {
    }

    private record RateLimitWindow(int limit, Duration window) {
    }
}
