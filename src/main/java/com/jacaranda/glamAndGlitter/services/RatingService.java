package com.jacaranda.glamAndGlitter.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jacaranda.glamAndGlitter.exceptions.ElementNotFoundException;
import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.Cites;
import com.jacaranda.glamAndGlitter.model.ConvertToDTO;
import com.jacaranda.glamAndGlitter.model.Rating;
import com.jacaranda.glamAndGlitter.model.User;
import com.jacaranda.glamAndGlitter.model.Dtos.CreateRatingDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.RatingDTO;
import com.jacaranda.glamAndGlitter.respository.CiteRepository;
import com.jacaranda.glamAndGlitter.respository.RatingRepository;
import com.jacaranda.glamAndGlitter.respository.UserRepository;

@Service
public class RatingService {

	@Autowired
	private RatingRepository ratingRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CiteRepository citeRepository;
	
	public List<RatingDTO> getServiceRating(String idService) {
		
		Integer id;
		try {
			id = Integer.parseInt(idService);
		}catch(NumberFormatException e) {
			throw new ValueNotValidException("Id must be numeric");
		}
		
		List<Cites>cites = citeRepository.findAll();
		List<Rating>ratings = new ArrayList<Rating>();
		List<Cites>citesService = new ArrayList<Cites>();
		
		citesService = cites.stream().filter(cite -> cite.getService().getId().equals(id)).collect(Collectors.toList());
		
		citesService.forEach(cite -> {
			cite.getRatings().forEach(rating -> ratings.add(rating));
		});
		
		return ConvertToDTO.convertToRatingDTO(ratings);
		
	}
	
	public CreateRatingDTO addRating(CreateRatingDTO rating) {
		
		if(rating.getMessage() == null || rating.getMessage().isEmpty()) {
			throw new ValueNotValidException("Message can`t be null");
		}
		
		if(rating.getCiteId() == null) {
			throw new ValueNotValidException("Cite can`t be null");
		}
		
		if(rating.getPunctuation() == null) {
			throw new ValueNotValidException("Punctuation can`t be null");
		}
		
		if(rating.getPunctuation() <= 0 || rating.getPunctuation() > 5) {
			throw new ValueNotValidException("Punctuation must be between 1 and 5");
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		User user = userRepository.findByEmail(auth.getName()).get(0);
		
		Cites cite = citeRepository.findById(rating.getCiteId()).orElseThrow(()-> 
			new ElementNotFoundException("Cite not found"));
		
		if(!cite.getUser().getId().equals(user.getId())) {
			throw new ValueNotValidException("Sorry, this cite its not yours");
		}
		
		Rating ratingCreated = new Rating(rating.getPunctuation(),rating.getMessage(),user,cite);
		
		ratingRepository.save(ratingCreated);
		
		return rating;
		
	}
	
}
