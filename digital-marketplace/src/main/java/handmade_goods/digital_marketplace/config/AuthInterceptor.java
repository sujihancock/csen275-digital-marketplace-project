package handmade_goods.digital_marketplace.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;

@Component
public class AuthInterceptor  implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Autowired
    public AuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error("not logged in")));
            return false;
        }

        return true;
    }
}
