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

            String user_id = jwtService.extractUserUUID(token);

            if (user_id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Создаем токен аутентификации с user_id в качестве principal
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user_id,
                        null,
                        null
                );

                // Устанавливаем объект аутентификации в контекст
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        // Продолжаем выполнение цепочки фильтров
        filterChain.doFilter(request, response);
    }
}