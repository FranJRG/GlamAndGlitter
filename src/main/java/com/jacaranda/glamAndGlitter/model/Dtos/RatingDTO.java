package com.jacaranda.glamAndGlitter.model.Dtos;

import java.util.Objects;

public class RatingDTO {

	private Integer punctuation;
	
	private String message;
	
	private String username;

	public RatingDTO() {
		super();
	}

	public RatingDTO(Integer punctuation, String message, String username) {
		super();
		this.punctuation = punctuation;
		this.message = message;
		this.username = username;
	}

	public Integer getPunctuation() {
		return punctuation;
	}

	public void setPunctuation(Integer punctuation) {
		this.punctuation = punctuation;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public int hashCode() {
		return Objects.hash(message, punctuation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RatingDTO other = (RatingDTO) obj;
		return Objects.equals(message, other.message) && Objects.equals(punctuation, other.punctuation);
	}
	
}
