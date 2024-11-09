package com.jacaranda.glamAndGlitter.model.Dtos;

import java.util.Objects;

public class CreateRatingDTO {
	
	private String message;
	
	private Integer punctuation;
	
	private Integer citeId;

	public CreateRatingDTO() {
		super();
	}

	public CreateRatingDTO(String message, Integer punctuation, Integer userId, Integer citeId) {
		super();
		this.message = message;
		this.punctuation = punctuation;
		this.citeId = citeId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getPunctuation() {
		return punctuation;
	}

	public void setPunctuation(Integer punctuation) {
		this.punctuation = punctuation;
	}
	
	public Integer getCiteId() {
		return citeId;
	}

	public void setCiteId(Integer citeId) {
		this.citeId = citeId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(citeId, message, punctuation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CreateRatingDTO other = (CreateRatingDTO) obj;
		return Objects.equals(citeId, other.citeId) && Objects.equals(message, other.message)
				&& Objects.equals(punctuation, other.punctuation);
	}
	

}
