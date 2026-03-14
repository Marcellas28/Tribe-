package com.dayworks_ltd.loyalty_engine.common;

import com.dayworks_ltd.loyalty_engine.common.ApiResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseBody> handleIllegalState(IllegalStateException e) {
        ApiResponseBody response = new ApiResponseBody();
        response.setStatus("FAILURE");
        response.setMessage(e.getMessage());
        response.setRespObject(null);
        return ResponseEntity.badRequest().body(response);
    }

    // Other exception handlers can be added here
}
