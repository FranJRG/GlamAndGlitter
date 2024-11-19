package com.jacaranda.glamAndGlitter.model.Dtos;

import java.util.Objects;

public class AverageMedia {

	private Double averageMedia;

	public AverageMedia() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AverageMedia(Double averageMedia) {
		super();
		this.averageMedia = averageMedia;
	}

	public Double getAverageMedia() {
		return averageMedia;
	}

	public void setAverageMedia(Double averageMedia) {
		this.averageMedia = averageMedia;
	}

	@Override
	public int hashCode() {
		return Objects.hash(averageMedia);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AverageMedia other = (AverageMedia) obj;
		return Objects.equals(averageMedia, other.averageMedia);
	}
	
	
	
}
