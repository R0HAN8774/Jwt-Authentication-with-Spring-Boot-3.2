package com.jwt.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private UserDetailsService userDetailsService;
    private String username = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestHeader = request.getHeader("Authorization");
        LOGGER.info(" Header :  {}", requestHeader);

        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            String token = requestHeader.substring(7);
            handleToken(token, request);
        } else {
            LOGGER.info("Invalid Header Value !! ");
        }
        filterChain.doFilter(request, response);
    }

    private void handleToken(String token, HttpServletRequest request) {
        try {
            username = this.jwtHelper.getUsernameFromToken(token);
        } catch (IllegalArgumentException | ExpiredJwtException | MalformedJwtException e) {
            handleTokenException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            validateAndSetAuthentication(token, userDetails, request);
        }
    }

    private void handleTokenException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            LOGGER.info("Illegal Argument while fetching the username !!");
        } else if (e instanceof ExpiredJwtException) {
            LOGGER.info("Given jwt token is expired !!");
        } else if (e instanceof MalformedJwtException) {
            LOGGER.info("Some changes have been made in the token !! Invalid Token");
        }
    }

    private void validateAndSetAuthentication(String token, UserDetails userDetails, HttpServletRequest request) {
        Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
        if (Boolean.TRUE.equals(validateToken)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            LOGGER.info("Validation fails !!");
        }
    }

}