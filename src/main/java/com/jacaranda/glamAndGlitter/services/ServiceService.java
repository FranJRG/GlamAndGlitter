package com.jacaranda.glamAndGlitter.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.jacaranda.glamAndGlitter.exceptions.ElementNotFoundException;
import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.Category;
import com.jacaranda.glamAndGlitter.model.ConvertToDTO;
import com.jacaranda.glamAndGlitter.model.Service;
import com.jacaranda.glamAndGlitter.model.Dtos.ServiceDTO;
import com.jacaranda.glamAndGlitter.respository.CategoryRepository;
import com.jacaranda.glamAndGlitter.respository.ServiceRepository;

@org.springframework.stereotype.Service
public class ServiceService {

	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	/**
	 * Método para obtener todos los servicios
	 * @return
	 */
	public List<ServiceDTO> getServices(){
		return ConvertToDTO.getServicesDTO(serviceRepository.findAll());
	}
	
	/**
	 * Método para obtener todos los servicios activos
	 * @return
	 */
	public List<ServiceDTO> getServicesActives(){
		return ConvertToDTO.getServicesDTO(serviceRepository.findAllByActive(true));
	}
	
	/**
	 * Método para obtener servicios por su id y que esten activos
	 * @param idString
	 * @return
	 */
	public ServiceDTO getService(String idString){
		Integer id;
		
		try {
			id = Integer.valueOf(idString);
		}catch(NumberFormatException e){
			throw new ValueNotValidException("Id must be numeric");
		}
		
		List<Service> services = serviceRepository.findByIdAndActive(id,true);
		
		if(services.isEmpty()) {
			throw new ElementNotFoundException("This service is not found or is inactive");
		}
		
		ServiceDTO service = new ServiceDTO(services.get(0).getId(),services.get(0).getName(),
				services.get(0).getDescription(),services.get(0).getPrice(),
				services.get(0).getActive(),services.get(0).getCategory().getName(),services.get(0).getImageUrl(),services.get(0).getDuration());
		
		return service;
	}
	
	/**
	 * Método para obtener servicios aleatorios
	 * @return
	 */
	public List<ServiceDTO> getRandomServices(){
		List<ServiceDTO>servicesDTO = ConvertToDTO.getServicesDTO(serviceRepository.findAll());
		Collections.shuffle(servicesDTO);
		return servicesDTO.stream().limit(15).collect(Collectors.toList());
	}
	
	/**
	 * Métodos para obtener servicios por su categoría
	 * @param idCategory
	 * @return
	 */
	public List<ServiceDTO> getServicesByCategory(String idCategory){
		Integer id;
		
		try {
			id = Integer.valueOf(idCategory);
		}catch(NumberFormatException e) {
			throw new ValueNotValidException("Id must be numeric");
		}
		
		Category category = categoryRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Category not found with id " + id));
		
		return ConvertToDTO.getServicesDTO(serviceRepository.findByCategoryAndActive(category,true));
	}
	
	/**
	 * Método para desactivar o activar un servicio
	 */
	public ServiceDTO disabledService(String idString) {
		
		Integer id;
		
		try {
			id = Integer.valueOf(idString);
		}catch(NumberFormatException e) {
			throw new ValueNotValidException("Id must be numeric");
		}
		
		Service service = serviceRepository.findById(id).orElseThrow(() 
				-> new ElementNotFoundException("Service not found with this id"));
		
		service.setActive(!service.getActive());
		
		ServiceDTO serviceAux = new ServiceDTO(service.getId(),service.getName(),service.getDescription(),service.getPrice(),
				service.getActive(),service.getCategory().getName(),service.getImageUrl(),service.getDuration());
		
		serviceRepository.save(service);
		
		return serviceAux;
		
	}
}
