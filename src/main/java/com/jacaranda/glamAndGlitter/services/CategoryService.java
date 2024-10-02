package com.jacaranda.glamAndGlitter.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacaranda.glamAndGlitter.model.ConvertToDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.CategoryDTO;
import com.jacaranda.glamAndGlitter.respository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	/**
	 * Método para obtener todas las categorías
	 * Usaremos la clase ConvertToDTO para convertiras en DTOs
	 * @return
	 */
	public List<CategoryDTO>getCategories(){
		return ConvertToDTO.getCategoriesDTO(categoryRepository.findAll());
	}
	
}
