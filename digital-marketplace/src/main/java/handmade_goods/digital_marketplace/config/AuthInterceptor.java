package handmade_goods.digital_marketplace.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;

@Component
public class AuthInterceptor  implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // Skip login & signup endpoints
        String path = request.getRequestURI();
        if (path.contains("/login") || path.contains("/signup")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\",\"message\":\"not logged in\",\"timestamp\":\"" + Instant.now().toString() + "\"}}");
            return false;
        }

        return true;
    }
}
