package com.cena.shoestore.shoestore_api.config;

import com.cena.shoestore.shoestore_api.service.DbSessionContextService;
import com.cena.shoestore.shoestore_api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VpdContextFilter extends OncePerRequestFilter {

    DbSessionContextService dbSessionContextService;
    JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {

                String userEmail = authentication.getName();
                Set<String> roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(auth -> auth.replace("ROLE_", ""))
                        .collect(Collectors.toSet());

                Long userId = extractUserIdFromAuthentication(authentication);

                dbSessionContextService.setClientIdentifier(userId, roles);

                log.debug("VPD context set for user: {} with roles: {}", userEmail, roles);
            } else {
                dbSessionContextService.clearClientIdentifier();
            }

        } catch (Exception e) {
            log.error("Error setting VPD context", e);
        }

        filterChain.doFilter(request, response);
    }

    private Long extractUserIdFromAuthentication(Authentication authentication) {
        try {
            final String authHeader = ((org.springframework.web.context.request.ServletRequestAttributes)
                    org.springframework.web.context.request.RequestContextHolder.getRequestAttributes())
                    .getRequest().getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                return jwtService.extractUserId(jwt);
            }
        } catch (Exception e) {
            log.warn("Could not extract userId from JWT", e);
        }
        return null;
    }
}
