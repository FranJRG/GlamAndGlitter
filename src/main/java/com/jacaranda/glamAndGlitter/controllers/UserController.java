package com.jacaranda.glamAndGlitter.controllers;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.RegisterUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.UserChangePasswordDTO;
import com.jacaranda.glamAndGlitter.services.UserService;

import jakarta.mail.MessagingException;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<?> findAll(){
		List<GetUserDTO>users = userService.getUsers();
		return ResponseEntity.ok().body(users);
	}
	
	@PostMapping("/users/")
	public ResponseEntity<?>register(@RequestBody RegisterUserDTO user) throws ValueNotValidException, UnsupportedEncodingException, MessagingException{
		RegisterUserDTO newUser = userService.addUser(user);
		return ResponseEntity.ok().body(newUser);
	}
	
	/*
	 * Métodos para recuperar la contraseña en caso de olvidarla
	 * */
	
	@PostMapping("/forgotPassword")
	public ResponseEntity<?>forgotPassword(@RequestParam String email) throws UnsupportedEncodingException, MessagingException{
		userService.sendCodeToUser(email);
		Map<String,Object>newMessage = new HashMap<String,Object>();
		
		newMessage.put("status", 200);
		newMessage.put("message", "Check out your email");
		return ResponseEntity.ok().body(newMessage);
	}
	
	@PostMapping("/verifyCode")
	public ResponseEntity<?>verifyCode(@RequestParam String email,@RequestParam String codeToCheck){
		Boolean isValidCode = userService.verifyCode(email, codeToCheck);
		Map<String,Object>newMessage = new HashMap<String,Object>();
		
		if(isValidCode) {
			newMessage.put("status", 200);
			newMessage.put("message", "Is correct you can change your password");
			return ResponseEntity.ok().body(newMessage);
		}
		return ResponseEntity.ok().body(isValidCode);
	}
	
	@PostMapping("/changePassword")
	public ResponseEntity<?>changePassword(@RequestParam String email,@RequestParam String password){
		UserChangePasswordDTO userPassword = userService.changePassword(email,password);
		return ResponseEntity.ok().body(userPassword);
	}
	
}
