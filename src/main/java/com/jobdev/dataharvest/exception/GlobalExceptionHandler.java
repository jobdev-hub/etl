package com.jobdev.dataharvest.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.jobdev.dataharvest.dto.ResponseErrorDTO;
import com.jobdev.dataharvest.enums.DataIntegrityViolationEnum;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<?> handlePropertyReference(PropertyReferenceException e, WebRequest request) {
        log.error("PropertyReferenceException: {}", e.getMessage());
        var status = HttpStatus.BAD_REQUEST;
        var message = e.getPropertyName() + " is not a valid property";
        var responseError = getResponseError(message, request, status);
        return ResponseEntity.status(status).body(responseError);
    }

    private ResponseErrorDTO getResponseError(String message, WebRequest request, HttpStatus status) {
        ResponseErrorDTO responseError = ResponseErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return responseError;
    }
}
