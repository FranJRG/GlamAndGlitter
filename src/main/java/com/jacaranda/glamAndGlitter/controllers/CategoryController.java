package com.jacaranda.glamAndGlitter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Dtos.CategoryDTO;
import com.jacaranda.glamAndGlitter.services.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	@Operation(summary = "Obtener las categorias existentes, cualquier usuario podrá verlas")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/categories")
	public ResponseEntity<?>getCategories(){
		List<CategoryDTO>categories = categoryService.getCategories();
		return ResponseEntity.ok().body(categories);
	}
	
	
}
