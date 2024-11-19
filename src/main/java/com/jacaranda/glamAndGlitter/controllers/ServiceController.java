package com.jacaranda.glamAndGlitter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Dtos.ServiceDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.ServiceSummary;
import com.jacaranda.glamAndGlitter.services.ServiceService;
import com.jacaranda.glamAndGlitter.services.ServiceSummarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class ServiceController {

	@Autowired
	private ServiceService serviceService;
	
	@Autowired
	private ServiceSummarService serviceSumService;
	
	@Operation(summary = "Método para obtener un reporte sobre los servicios mas usados, solo los administradores podran acceder aqui")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/servicesSummary")
	public ResponseEntity<?> getServicesWithRating(){
		List<ServiceSummary>services = serviceSumService.getTopServicesWithRatings();
		return ResponseEntity.ok().body(services);
	}
	
	@Operation(summary = "Método para ver los servicios, solo los administradores podran acceder aqui")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/services")
	public ResponseEntity<?> getServices(){
		List<ServiceDTO>services = serviceService.getServices();
		return ResponseEntity.ok().body(services);
	}
	
	@Operation(summary = "Método para ver los servicios activos, cualquier usuario podrá acceder aqui")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/gridServices")
	public ResponseEntity<?> getServicesActive(){
		List<ServiceDTO>services = serviceService.getServicesActives();
		return ResponseEntity.ok().body(services);
	}
	
	@Operation(summary = "Obtener un servicio por su id, cualquier usuario podrá acceder aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/services/{id}")
	public ResponseEntity<?> getService(@PathVariable String id){
		ServiceDTO service = serviceService.getService(id);
		return ResponseEntity.ok().body(service);
	}
	
	@Operation(summary = "Obtener servicios de forma aleatoria, cualquier usuario podrá acceder")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/randomServices")
	public ResponseEntity<?>getRandomServices(){
		List<ServiceDTO>services = serviceService.getRandomServices();
		return ResponseEntity.ok().body(services);
	}
	
	@Operation(summary = "Obtener los servicios filtrados por su categoría, cualquier usuario podrá verlo")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/servicesByCategory/{idCategory}")
	public ResponseEntity<?>getServicesByCategory(@PathVariable String idCategory){
		List<ServiceDTO>services = serviceService.getServicesByCategory(idCategory);
		return ResponseEntity.ok().body(services);
	}
	
	@Operation(summary = "Desactivar un servicio, solo los administradores accederán aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PutMapping("/disabledService/{id}")
	public ResponseEntity<?>disabledService(@PathVariable String id){
		ServiceDTO service = serviceService.disabledService(id);
		return ResponseEntity.ok().body(service);
	}
	
	
	
}
