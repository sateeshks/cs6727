package com.kailas.dpm.controller;


import com.kailas.dpm.domain.AppErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {


    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<AppErrorResponse> handleSecurityException(HttpServletRequest req, SecurityException ex) {
        log.error("Security error", ex);
        AppErrorResponse resp = new AppErrorResponse();
        resp.setPath(req.getRequestURI());
        resp.setStatus(401);
        resp.setError(ex.getMessage());
        resp.setTrace(ExceptionUtils.getStackTrace(ex));
        resp.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
    }
}
