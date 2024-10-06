package com.jacaranda.glamAndGlitter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Dtos.ServiceDTO;
import com.jacaranda.glamAndGlitter.services.ServiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class ServiceController {

	@Autowired
	private ServiceService serviceService;
	
	@Operation(summary = "Método para ver los servicios, cualquier usuario podrá acceder aqui")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/services")
	public ResponseEntity<?> getServices(){
		List<ServiceDTO>services = serviceService.getServices();
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
	
}
