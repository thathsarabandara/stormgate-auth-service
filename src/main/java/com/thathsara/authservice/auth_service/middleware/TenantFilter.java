package com.thathsara.authservice.auth_service.middleware;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.thathsara.authservice.auth_service.util.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String tenantHeader = request.getHeader("Tenant-ID");

        if (tenantHeader != null) {
            try {
                TenantContext.setTenantID(Long.valueOf(tenantHeader));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Tenant-ID format");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tenant-ID header missing");
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
