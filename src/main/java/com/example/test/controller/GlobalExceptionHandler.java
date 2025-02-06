package com.example.test.controller;

import com.example.test.exception.CustomerNotFoundException;
import com.example.test.model.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCustomerNotFoundException(CustomerNotFoundException exception) {
        log.info(exception.getMessage());
        final ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(false);
        errorDto.setMessage(exception.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        log.error("Unknown exception during the process: {}", ex.getMessage(), ex);
        final ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(false);
        errorDto.setMessage("Internal Server Error");
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
