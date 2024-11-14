package com.jacaranda.glamAndGlitter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Dtos.CreateRatingDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.RatingDTO;
import com.jacaranda.glamAndGlitter.services.RatingService;

@RestController
public class RatingController {

	@Autowired
	private RatingService ratingService;
	
	@GetMapping("/ratings/{id}")
	public ResponseEntity<?>getServiceRatings(@PathVariable String id){
		List<RatingDTO>ratings = ratingService.getServiceRating(id);
		return ResponseEntity.ok().body(ratings);
	}
	
	@PostMapping("/addRating")
	public ResponseEntity<?>addRating(@RequestBody CreateRatingDTO rating){
		ratingService.addRating(rating);
		return ResponseEntity.ok().body(rating);
	}
	
}
