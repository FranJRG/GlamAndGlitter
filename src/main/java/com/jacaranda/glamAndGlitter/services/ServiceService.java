package com.jacaranda.glamAndGlitter.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.jacaranda.glamAndGlitter.exceptions.ElementNotFoundException;
import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.Category;
import com.jacaranda.glamAndGlitter.model.ConvertToDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.ServiceDTO;
import com.jacaranda.glamAndGlitter.respository.CategoryRepository;
import com.jacaranda.glamAndGlitter.respository.ServiceRepository;

@org.springframework.stereotype.Service
public class ServiceService {

	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	public List<ServiceDTO> getServices(){
		return ConvertToDTO.getServicesDTO(serviceRepository.findAll());
	}
	
	public List<ServiceDTO> getRandomServices(){
		List<ServiceDTO>servicesDTO = ConvertToDTO.getServicesDTO(serviceRepository.findAll());
		Collections.shuffle(servicesDTO);
		return servicesDTO.stream().limit(15).collect(Collectors.toList());
	}
	
	public List<ServiceDTO> getServicesByCategory(String idCategory){
		Integer id;
		
		try {
			id = Integer.valueOf(idCategory);
		}catch(ValueNotValidException e) {
			throw new ValueNotValidException("Id must be numeric");
		}
		
		Category category = categoryRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Category not found with id " + id));
		
		return ConvertToDTO.getServicesDTO(serviceRepository.findByCategory(category));
	}
	
}
