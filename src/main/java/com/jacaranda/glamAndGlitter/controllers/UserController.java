package com.jacaranda.glamAndGlitter.controllers;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.RegisterUserDTO;
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
	
}
