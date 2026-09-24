package com.example.eggCount.EggCounter.filter;

import com.example.eggCount.EggCounter.entity.UserEntity;
import com.example.eggCount.EggCounter.service.JwtService;
import com.example.eggCount.EggCounter.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println("========== JWT FILTER ==========");
        System.out.println("REQUEST: " + request.getMethod() + " " + request.getRequestURI());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            System.out.println("NO BEARER TOKEN");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        System.out.println("TOKEN RECEIVED: YES");

        try {

            Long userId = jwtService.getIdFromToken(token);

            System.out.println("USER ID FROM TOKEN: " + userId);

            UserEntity user = userService.getUserById(userId);

            System.out.println("USER FOUND: " + user.getUsername());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            System.out.println("AUTHENTICATION SET SUCCESSFULLY");

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {

            System.out.println("JWT ERROR: ACCESS TOKEN EXPIRED");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write(
                    "{\"message\":\"Access token expired\"}"
            );

        } catch (JwtException e) {

            System.out.println("JWT ERROR: INVALID TOKEN");
            System.out.println("ERROR: " + e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write(
                    "{\"message\":\"Invalid access token\"}"
            );

        } catch (Exception e) {

            System.out.println("JWT ERROR: OTHER EXCEPTION");
            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write(
                    "{\"message\":\"Unauthorized\"}"
            );
        }
    }
}