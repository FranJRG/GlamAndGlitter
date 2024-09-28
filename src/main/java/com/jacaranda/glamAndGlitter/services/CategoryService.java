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
	
	public List<CategoryDTO>getCategories(){
		return ConvertToDTO.getCategoriesDTO(categoryRepository.findAll());
	}
	
}
