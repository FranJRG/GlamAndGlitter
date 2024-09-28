package com.jacaranda.glamAndGlitter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Dtos.CategoryDTO;
import com.jacaranda.glamAndGlitter.services.CategoryService;

@RestController
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/categories")
	public ResponseEntity<?>getCategories(){
		List<CategoryDTO>categories = categoryService.getCategories();
		return ResponseEntity.ok().body(categories);
	}
	
	
}
