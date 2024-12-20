package com.jacaranda.glamAndGlitter.controllers;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.jacaranda.glamAndGlitter.model.Dtos.GetPendingCiteDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.SummaryCites;
import com.jacaranda.glamAndGlitter.model.Dtos.UpdateCiteDTO;
import com.jacaranda.glamAndGlitter.services.CiteService;
import com.jacaranda.glamAndGlitter.services.ServiceSummarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;

@RestController
public class CiteController {

	@Autowired
	private CiteService citeService;
	
	@Autowired
	private ServiceSummarService summaryService;
	
	@Operation(summary = "Método para obtener las citas del último mes, solo los administradores accederán aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/lastCites")
	public ResponseEntity<?>getLastMonthCites(){
		List<SummaryCites>cites =  summaryService.getCitesInLastMonth();
		return ResponseEntity.ok().body(cites);
	}
	
	@Operation(summary = "Método para obtener los trabajadores disponibles filtrando por fecha y hora se usará para modificar los trabajadores disponibles cuando los administradores cambien fecha u hora de una cita puedan ver que trabajadores hay disponibles a esa hora, solo los administradores accederán aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/workers/{id}")
	public ResponseEntity<?>workers(@Parameter(description = "ID del trabajador") @PathVariable String id, 
			@Parameter(description="Fecha para filtrar (yyyy-MM-dd)")@RequestParam Optional<String> dateFilter, 
			@Parameter(description="Hora para filtrar (HH:mm:ss)")@RequestParam Optional<String> timeFilter){
		List<GetUserDTO>users =  citeService.getWorkersAvailablesById(id,dateFilter.orElse(null),timeFilter.orElse(null));
		return ResponseEntity.ok().body(users);
	}
	
	@Operation(summary = "Método para obtener una cita por su id, solo los administradores podrán ver un listado de citas")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/cite/{id}")
	public ResponseEntity<?>getCiteById(@PathVariable String id){
		GetPendingCiteDTO citeDTO =  citeService.getCite(id);
		return ResponseEntity.ok().body(citeDTO);
	}
	
	@Operation(summary = "Método para obtener las citas pendientes, solo los administradores accederán aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/cites")
	public ResponseEntity<?>cites(){
		List<GetPendingCiteDTO>cites =  citeService.getPendingCites();
		return ResponseEntity.ok().body(cites);
	}
	
	@Operation(summary = "Método para obtener las citas de un usuario, los usuarios logueados podrán ver solo sus citas")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/myCites/{id}")
	public ResponseEntity<?>myCites(@PathVariable String id){
		List<GetPendingCiteDTO>cites =  citeService.myCites(id);
		return ResponseEntity.ok().body(cites);
	}
	
	@Operation(summary = "Método para saber si una cita ya existe por su hora y fecha")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/checkCite")
	public ResponseEntity<?>checkCite(@Parameter(description = "Formato válido yyyy-MM-dd")@RequestParam LocalDate date, @Parameter(description = "Formato válido HH:mm:ss") @RequestParam String time,@Parameter(description = "Formato válido HH:mm:ss") @RequestParam String endTime){
		List<BookCiteDTO>cites =  citeService.findByDateAndTime(date,time, endTime);
		return ResponseEntity.ok().body(cites);
	}
	
	@Operation(summary = "(El eventId irá nulo | vacío o se quedará tal como está)Método para reservar una cita, solo los usuarios logueados podrán acceder aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/addCite")
	public ResponseEntity<?>postCite(@RequestBody BookCiteDTO citeDTO){
		citeService.addCite(citeDTO);
		return ResponseEntity.ok().body(citeDTO);
	}
	
	@Operation(summary = "Método para modificar una cita existente, solo los usuarios logueados que sea suya la cita "
			+ "y los administradores podrán acceder aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PutMapping("/modifyCite/{id}")
	public ResponseEntity<?>modifyCite(@PathVariable String id, @RequestBody UpdateCiteDTO citeDTO, 
			@RequestParam Optional<String>idWorker){
		UpdateCiteDTO newCiteDTO = citeService.updateCite(id, citeDTO, idWorker.orElse(""));
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
