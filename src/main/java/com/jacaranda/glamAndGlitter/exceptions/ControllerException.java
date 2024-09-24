package com.jacaranda.glamAndGlitter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerException {

	@ExceptionHandler(ValueNotValidException.class)
	public ResponseEntity<?>valueNotValidException(ValueNotValidException e){
		ApiError newApiError = new ApiError(HttpStatus.BAD_REQUEST,e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newApiError);
	}
	
	@ExceptionHandler(CredentialsNotValidException.class)
	public ResponseEntity<?>credentialsNotValidException(CredentialsNotValidException e){
		ApiError newApiError = new ApiError(HttpStatus.BAD_REQUEST,e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newApiError);
	}
	
	@ExceptionHandler(RoleNotValidException.class)
	public ResponseEntity<?>roleNotValidException(RoleNotValidException e){
		ApiError newApiError = new ApiError(HttpStatus.BAD_REQUEST,e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newApiError);
	}
	
	@ExceptionHandler(ExceptionTokenNotValid.class)
	public ResponseEntity<?>exceptionTokenNotValid(ExceptionTokenNotValid e){
		ApiError newApiError = new ApiError(HttpStatus.BAD_REQUEST,e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newApiError);
	}
	
}
