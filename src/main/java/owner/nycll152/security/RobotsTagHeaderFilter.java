package owner.nycll152.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import owner.nycll152.config.AppProperties;

import java.io.IOException;

@Component
public class RobotsTagHeaderFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;

    public RobotsTagHeaderFilter(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.setHeader("Permissions-Policy", "camera=(), geolocation=(), microphone=(), payment=(), usb=()");

        if (!appProperties.isPublicIndexingEnabled()) {
            response.setHeader("X-Robots-Tag", "noindex, nofollow");
        }

        filterChain.doFilter(request, response);
    }
}
