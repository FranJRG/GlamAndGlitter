package com.jacaranda.glamAndGlitter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Service;
import com.jacaranda.glamAndGlitter.services.ServiceService;

@RestController
public class ServiceController {

	@Autowired
	private ServiceService serviceService;
	
	@GetMapping("/services")
	public ResponseEntity<?> getServices(){
		List<Service>services = serviceService.getServices();
		return ResponseEntity.ok().body(services);
	}
	
}
