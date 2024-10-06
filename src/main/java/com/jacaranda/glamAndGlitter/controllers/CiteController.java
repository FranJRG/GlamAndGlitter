package com.jacaranda.glamAndGlitter.controllers;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Dtos.BookCiteDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;
import com.jacaranda.glamAndGlitter.services.CiteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;

@RestController
public class CiteController {

	@Autowired
	private CiteService citeService;
	
	@Operation(summary = "Método para saber si una cita ya existe por su hora y fecha")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/checkCite")
	public ResponseEntity<?>checkCite(@RequestParam LocalDate date,@RequestParam String time){
		List<BookCiteDTO>cites =  citeService.findByDateAndTime(date,time);
		return ResponseEntity.ok().body(cites);
	}
	
	@Operation(summary = "Método para reservar una cita, solo los usuarios logueados podrán acceder aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/addCite")
	public ResponseEntity<?>postCite(@RequestBody BookCiteDTO citeDTO){
		citeService.addCite(citeDTO);
		return ResponseEntity.ok().body(citeDTO);
	}
	
	@Operation(summary = "Método para setear un empleado a una cita, solo los usuarios administradores podrán acceder aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/setWorker")
	public ResponseEntity<?>setWorkerToCite(@RequestParam String idCite, @RequestParam String idWorker){
		GetUserDTO worker = citeService.setManuallyWorker(idCite, idWorker);
		return ResponseEntity.ok().body(worker);
	}
	
	@Operation(summary = "Método para modificar una cita existente, solo los usuarios logueados que sea suya la cita "
			+ "y los administradores podrán acceder aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PutMapping("/modifyCite/{id}")
	public ResponseEntity<?>modifyCite(@PathVariable String id, @RequestBody BookCiteDTO citeDTO){
		BookCiteDTO newCiteDTO = citeService.updateCite(id, citeDTO);
		return ResponseEntity.ok().body(newCiteDTO);
	}
	
	@Operation(summary = "Método para cancelar una cita, solo los usuarios logueados y que sea suya la cita "
			+ "y los administradores podrán acceder aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@DeleteMapping("/cancelCite/{id}")
	public ResponseEntity<?>deleteCite(@PathVariable String id) throws UnsupportedEncodingException, MessagingException{
		BookCiteDTO citeDTO = citeService.removeCite(id);
		return ResponseEntity.ok().body(citeDTO);
	}
	
	
}
