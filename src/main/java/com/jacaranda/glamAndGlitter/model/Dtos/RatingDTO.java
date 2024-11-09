package com.jacaranda.glamAndGlitter.model.Dtos;

import java.util.Objects;

public class RatingDTO {

	private Integer punctuation;
	
	private String message;

	public RatingDTO() {
		super();
	}

	public RatingDTO(Integer punctuation, String message) {
		super();
		this.punctuation = punctuation;
		this.message = message;
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
