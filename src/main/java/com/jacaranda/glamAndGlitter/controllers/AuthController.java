package com.jacaranda.glamAndGlitter.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.exceptions.CredentialsNotValidException;
import com.jacaranda.glamAndGlitter.model.LoginCredentials;
import com.jacaranda.glamAndGlitter.model.User;
import com.jacaranda.glamAndGlitter.model.Dtos.TokenDTO;
import com.jacaranda.glamAndGlitter.utility.TokenUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Operation(summary = "Pagina de bienvenidad para saber que la aplicación está operativa")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/")
	public ResponseEntity<?> welcomePage(){
		Map<String,Object>newMessage = new HashMap<String,Object>();
		
		newMessage.put("status", 200);
		newMessage.put("message", "Welcome to Glam&Glitter");
		
		return ResponseEntity.ok().body(newMessage);
	}
	
	@Operation(summary = "Loguearse en la aplicación")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginCredentials loginRequest) throws CredentialsNotValidException {

		Authentication authentication;
		//Si el usuario y el password que le paso son los adecuados me 
		// devuele un autentication. Si no lo encuentra, lanza una exception
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
	
		} catch (Exception e) {
			throw new CredentialsNotValidException(e.getMessage());
		}
		
		User user = (User)authentication.getPrincipal();
		String jwt = TokenUtils.generateToken(loginRequest.getEmail(),user.getRole(), user.getId());
		TokenDTO tokenDTO = new TokenDTO(jwt);
		return ResponseEntity.ok(tokenDTO);
	}
	
}
