package com.jacaranda.glamAndGlitter.model.Dtos;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import io.swagger.v3.oas.annotations.media.Schema;

public class BookCiteDTO {
	
	@Schema(description = "La fecha de la cita deberá cumplir el formato yyyy-MM-dd")
	@JsonFormat(shape= Shape.STRING, pattern ="yyyy-MM-dd")
	private Date day;
	
	@Schema(description = "La hora de inicio deberá mantener el formato HH:mm:ss")
	private Time startTime;
	
	private Integer idService;
	
	@Schema(description = "El eventId irá vacío o se quedará como está, la aplicación se encargará de crearlo")
	private String eventId;

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

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
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
