package com.jacaranda.glamAndGlitter.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Dtos.BookCiteDTO;
import com.jacaranda.glamAndGlitter.services.CiteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class CiteController {

	@Autowired
	private CiteService citeService;
	
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
	
	
}
