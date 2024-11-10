package com.jacaranda.glamAndGlitter.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jacaranda.glamAndGlitter.model.Category;
import com.jacaranda.glamAndGlitter.model.Service;
import java.util.List;


public interface ServiceRepository extends JpaRepository<Service, Integer>{

	//Obtenemos servicios por su categoría
	List<Service> findByCategoryAndActive(Category category, Boolean active);
	
	//Obtenemos servicios por su id y que esten activos
	List<Service> findByIdAndActive(Integer id, Boolean active);
	
	List<Service> findAllByActive(Boolean active);
	
	
}
