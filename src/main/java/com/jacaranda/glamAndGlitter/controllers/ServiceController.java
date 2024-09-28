package com.jacaranda.glamAndGlitter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Dtos.ServiceDTO;
import com.jacaranda.glamAndGlitter.services.ServiceService;

@RestController
public class ServiceController {

	@Autowired
	private ServiceService serviceService;
	
	@GetMapping("/services")
	public ResponseEntity<?> getServices(){
		List<ServiceDTO>services = serviceService.getServices();
		return ResponseEntity.ok().body(services);
	}
	
	@GetMapping("/randomServices")
	public ResponseEntity<?>getRandomServices(){
		List<ServiceDTO>services = serviceService.getRandomServices();
		return ResponseEntity.ok().body(services);
	}
	
	@GetMapping("/servicesByCategory/{idCategory}")
	public ResponseEntity<?>getServicesByCategory(@PathVariable String idCategory){
		List<ServiceDTO>services = serviceService.getServicesByCategory(idCategory);
		return ResponseEntity.ok().body(services);
	}
	
}
