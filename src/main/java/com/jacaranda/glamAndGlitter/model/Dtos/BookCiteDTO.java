package com.jacaranda.glamAndGlitter.model.Dtos;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class BookCiteDTO {
	
	@JsonFormat(shape= Shape.STRING, pattern ="yyyy-MM-dd")
	private Date day;
	
	private Time startTime;
	
	private Integer idService;

	public BookCiteDTO() {
		super();
	}

	public BookCiteDTO(Date day, Time startTime,Integer idService) {
		super();
		this.day = day;
		this.startTime = startTime;
		this.idService = idService;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Integer getIdService() {
		return idService;
	}

	public void setIdService(Integer idService) {
		this.idService = idService;
	}

	@Override
	public int hashCode() {
		return Objects.hash(day, idService, startTime);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookCiteDTO other = (BookCiteDTO) obj;
		return Objects.equals(day, other.day) && Objects.equals(idService, other.idService)
				&& Objects.equals(startTime, other.startTime);
	}

	
}
