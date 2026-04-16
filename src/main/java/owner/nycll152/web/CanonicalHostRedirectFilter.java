package owner.nycll152.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;
import owner.nycll152.config.AppProperties;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class CanonicalHostRedirectFilter extends OncePerRequestFilter {

    private static final Pattern IPV4_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}$");

    private final AppProperties appProperties;

    public CanonicalHostRedirectFilter(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String configuredHost = normalized(appProperties.getBaseUrl().getHost());
        if (configuredHost.isBlank() || isLocalHost(configuredHost)) {
            return true;
        }

        String requestHost = normalized(request.getServerName());
        if (requestHost.isBlank()) {
            return true;
        }

        String configuredScheme = normalized(appProperties.getBaseUrl().getScheme());
        String requestScheme = normalized(request.getScheme());

        boolean hostMismatch = !requestHost.equals(configuredHost) && !isBypassHost(requestHost);
        boolean schemeMismatch = requestHost.equals(configuredHost)
                && !configuredScheme.isBlank()
                && !configuredScheme.equals(requestScheme);

        return !(hostMismatch || schemeMismatch);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String location = UriComponentsBuilder.fromUri(appProperties.getBaseUrl())
                .replacePath(request.getRequestURI())
                .replaceQuery(request.getQueryString())
                .build(true)
                .toUriString();

        response.setStatus(308);
        response.setHeader(HttpHeaders.LOCATION, location);
    }

    private boolean isBypassHost(String host) {
        return isLocalHost(host) || isIpAddress(host);
    }

    private boolean isLocalHost(String host) {
        return host.equals("localhost")
                || host.equals("127.0.0.1")
                || host.equals("::1")
                || host.equals("[::1]");
    }

    private boolean isIpAddress(String host) {
        return IPV4_PATTERN.matcher(host).matches() || host.contains(":");
    }

    private String normalized(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }
}
