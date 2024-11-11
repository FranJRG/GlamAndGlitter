package com.jacaranda.glamAndGlitter.controllers;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.RegisterUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.UserChangePasswordDTO;
import com.jacaranda.glamAndGlitter.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@Operation(summary = "Endpoint para obtener la lista de los trabajadores, solo los administradores podrán ver esta lista")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/allWorkers")
	public ResponseEntity<?> getWorkers(){
		List<GetUserDTO> user = userService.getWorkers();
		return ResponseEntity.ok().body(user);
	}
	
	@Operation(summary = "Endpoint para otbener un usuario por su id, cualquier usuario podrá acceder")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/user/{id}")
	public ResponseEntity<?> findById(@PathVariable String id){
		GetUserDTO user = userService.findById(id);
		return ResponseEntity.ok().body(user);
	}
	
	@Operation(summary = "Endpoint obtener los estilistas sin horario de trabajo, solo los administradores podrán comprobar este endpoint")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/userWithoutSchedule")
	public ResponseEntity<?> findWorkerWithoutSchedule(){
		List<GetUserDTO> users = userService.findWorkerWithoutSchedule();
		return ResponseEntity.ok().body(users);
	}
	
	@Operation(summary = "Endpoint para comprobar si existe un usuario por un email, cualquier usuario podrá acceder")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/checkEmail")
	public ResponseEntity<?> findEmail(@RequestParam String email){
		List<GetUserDTO>users = userService.findByEmail(email);
		return ResponseEntity.ok().body(users);
	}
	
	@Operation(summary = "Registrarse en la app, cualquier usuario podrá acceder")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/users")
	public ResponseEntity<?>register(@RequestBody RegisterUserDTO user) throws ValueNotValidException, UnsupportedEncodingException, MessagingException{
		RegisterUserDTO newUser = userService.addUser(user);
		return ResponseEntity.ok().body(newUser);
	}
	
	/*
	 * Métodos para recuperar la contraseña en caso de olvidarla
	 * */
	
	@Operation(summary = "Endpoint para usuario que haya olvidado su contraseña, cualquier usuario podrá acceder")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/forgotPassword")
	public ResponseEntity<?>forgotPassword(@RequestParam String email) throws UnsupportedEncodingException, MessagingException{
		userService.sendCodeToUser(email);
		Map<String,Object>newMessage = new HashMap<String,Object>();
		
		newMessage.put("status", 200);
		newMessage.put("message", "Check out your email");
		return ResponseEntity.ok().body(newMessage);
	}
	
	@Operation(summary = "Verificar código enviado por email, cualquier usuario podrá acceder")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
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
	
	@Operation(summary = "Endpoint para cambiar la contraseña del usuario, cualquier usuario podrá acceder aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/changePassword")
	public ResponseEntity<?>changePassword(@RequestParam String email,@RequestParam String password){
		UserChangePasswordDTO userPassword = userService.changePassword(email,password);
		return ResponseEntity.ok().body(userPassword);
	}
	
	/**
	 * Métodos para el recordatorio de citas
	 */
	
	@Operation(summary = "Endpoint para activar las notificaciones del usuario, solo los usuarios logueados podrán activar o desactivar sus notificaciones")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "200", description = "Complete")
	})
	@PostMapping("/activateNotifications")
	public ResponseEntity<?>activateNotifications(@RequestParam Optional<Boolean> emailNotifications, @RequestParam Optional<Boolean> calendarNotifications){
		
		GetUserDTO userDTO = userService.updateNotifications(emailNotifications.orElse(null),calendarNotifications.orElse(null));
		return ResponseEntity.ok().body(userDTO);
		
	}
	
}
