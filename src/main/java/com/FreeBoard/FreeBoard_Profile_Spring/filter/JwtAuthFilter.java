package com.FreeBoard.FreeBoard_Profile_Spring.filter;

import com.FreeBoard.FreeBoard_Profile_Spring.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token;

        // Проверяем наличие и формат заголовка Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Извлекаем токен из заголовка
        token = authHeader.substring(7);

        // Извлекаем email из токена
        String userEmail = jwtService.extractUserEmail(token);

        // Устанавливаем email в SecurityContext
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Создаем токен аутентификации с email в качестве principal
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userEmail,  // principal (в вашем случае - это email)
                    null,       // credentials (отсутствуют)
                    null        // authorities (нет ролей)
            );

            // Устанавливаем объект аутентификации в контекст
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Продолжаем выполнение цепочки фильтров
        filterChain.doFilter(request, response);
    }
}

