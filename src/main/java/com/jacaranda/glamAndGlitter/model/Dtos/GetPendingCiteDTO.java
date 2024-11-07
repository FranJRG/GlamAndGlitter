package com.jacaranda.glamAndGlitter.model.Dtos;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class GetPendingCiteDTO {
	
	Integer id;

	@JsonFormat(shape= Shape.STRING, pattern ="yyyy-MM-dd")
	private Date day;
	
	private Time startTime;
	
	private Integer idService;
	
	private String username;
	
	private String eventId;

	public GetPendingCiteDTO() {
		super();
	}

	public GetPendingCiteDTO(Integer id,Date day, Time startTime, Integer idService, String username, String eventId) {
		super();
		this.id = id;
		this.day = day;
		this.startTime = startTime;
		this.idService = idService;
		this.username = username;
		this.eventId = eventId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(day, idService, startTime, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GetPendingCiteDTO other = (GetPendingCiteDTO) obj;
		return Objects.equals(day, other.day) && Objects.equals(idService, other.idService)
				&& Objects.equals(startTime, other.startTime) && Objects.equals(username, other.username);
	}
	
	
	
}
