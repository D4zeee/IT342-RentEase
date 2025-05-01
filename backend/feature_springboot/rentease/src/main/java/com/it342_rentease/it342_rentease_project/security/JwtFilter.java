package com.it342_rentease.it342_rentease_project.security;

import com.it342_rentease.it342_rentease_project.service.OwnerDetailsService;
import com.it342_rentease.it342_rentease_project.service.RenterDetailsService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private OwnerDetailsService ownerDetailsService;

    @Autowired
    private RenterDetailsService renterDetailsService;


  @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {

    final String header = request.getHeader("Authorization");
    String token = null, username = null;

    if (header != null && header.startsWith("Bearer ")) {
        token = header.substring(7);
        username = jwtUtils.extractUsername(token);
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = null;

        try {
            // Try renter first
            userDetails = renterDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            try {
                // Fallback to owner
                userDetails = ownerDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException ex) {
                // Neither found, abort
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (jwtUtils.validateToken(token)) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    filterChain.doFilter(request, response);
}

@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return path.equals("/owners/login") || path.equals("/owners/register")
        || path.equals("/api/renters/login") || path.equals("/api/renters/register");
}


}
