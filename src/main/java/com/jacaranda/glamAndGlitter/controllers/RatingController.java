package com.jacaranda.glamAndGlitter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Dtos.AverageMedia;
import com.jacaranda.glamAndGlitter.model.Dtos.CreateRatingDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.RatingDTO;
import com.jacaranda.glamAndGlitter.services.RatingService;
import com.jacaranda.glamAndGlitter.services.ServiceSummarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class RatingController {

	@Autowired
	private RatingService ratingService;
	
	@Autowired
	private ServiceSummarService summaryService;
	
	@Operation(summary = "Método para obtener la media de puntuacion del último mes, solo los administradores accederán aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/punctuationLastMonth")
	public ResponseEntity<?>getPunctuation(){
		AverageMedia rating = summaryService.getRatingMediaLastMonth();
		return ResponseEntity.ok().body(rating);
	}
	
	@Operation(summary = "Método para obtener la media de puntuacion de la empresa en total, solo los administradores accederán aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/punctuation")
	public ResponseEntity<?>getTotalPunctuation(){
		AverageMedia rating = summaryService.getTotalMedia();
		return ResponseEntity.ok().body(rating);
	}
	
	@Operation(summary = "Método para obtener las peores puntuaciones del último mes (menores que 3), solo los administradores accederán aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/worstPunctuation")
	public ResponseEntity<?>getRatingLessThanThree(){
		List<RatingDTO> ratings = summaryService.getRatingLess();
		return ResponseEntity.ok().body(ratings);
	}
	
	@Operation(summary = "Método para obtener las mejores puntuaciones, solo los administradores accederán aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/bestPunctuation")
	public ResponseEntity<?>getRatingGreaterThanThree(){
		List<RatingDTO> ratings = summaryService.getRatingGreater();
		return ResponseEntity.ok().body(ratings);
	}
	
	@Operation(summary = "Método para obtener las valoraciones de servicios, solo los administradores accederán aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/ratings/{id}")
	public ResponseEntity<?>getServiceRatings(@PathVariable String id){
		List<RatingDTO>ratings = ratingService.getServiceRating(id);
		return ResponseEntity.ok().body(ratings);
	}
	
	@Operation(summary = "Método para añadir una valoración, solo los usuarios logueados accederán aquí")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/addRating")
	public ResponseEntity<?>addRating(@RequestBody CreateRatingDTO rating){
		ratingService.addRating(rating);
		return ResponseEntity.ok().body(rating);
	}
	
}
