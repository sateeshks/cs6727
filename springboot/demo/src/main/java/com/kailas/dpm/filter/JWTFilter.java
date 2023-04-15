package com.kailas.dpm.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kailas.dpm.domain.AppErrorResponse;
import com.kailas.dpm.util.TokenUtil;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;

@Component
public class JWTFilter extends HttpFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) {
        jakarta.servlet.ServletContext context = filterConfig.getServletContext();
        context.log("Jwt filter initialized");
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String token = servletRequest.getHeader("Authorization");
        if (token == null) {
            handleInvalidToken(servletRequest, servletResponse, new RuntimeException("Auth token not found!"));
            return;
        }

        try {
            token = token.replace("Bearer ", "");

            @SuppressWarnings("unused")
            String userHandelBytes = TokenUtil.parseToken(token);

        } catch (ParseException | JOSEException | SecurityException e) {
            handleInvalidToken(servletRequest, servletResponse, e);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private void handleInvalidToken(HttpServletRequest req, HttpServletResponse servletResponse, Exception ex) throws IOException {
        AppErrorResponse errorResponse = new AppErrorResponse();
        errorResponse.setPath(req.getRequestURI());
        errorResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.setError(ex.getMessage());
        errorResponse.setTrace(ExceptionUtils.getStackTrace(ex));
        errorResponse.setTimestamp(LocalDateTime.now());

        servletResponse.setContentType("application/json");
        servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        servletResponse.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
