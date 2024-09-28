package com.jacaranda.glamAndGlitter.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jacaranda.glamAndGlitter.model.Category;
import com.jacaranda.glamAndGlitter.model.Service;
import java.util.List;


public interface ServiceRepository extends JpaRepository<Service, Integer>{

	List<Service> findByCategory(Category category);
	
}
