package com.jacaranda.glamAndGlitter.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jacaranda.glamAndGlitter.model.Service;
import com.jacaranda.glamAndGlitter.respository.ServiceRepository;

@org.springframework.stereotype.Service
public class ServiceService {

	@Autowired
	private ServiceRepository serviceRepository;
	
	public List<Service> getServices(){
		return serviceRepository.findAll();
	}
	
}
