package com.jacaranda.glamAndGlitter.model.Dtos;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class BookCiteDTO {

	private Integer userId;
	
	private Integer idWorker;
	
	@JsonFormat(shape= Shape.STRING, pattern ="yyyy-MM-dd")
	private Date day;
	
	private Time startTime;
	
	private Integer idService;

	public BookCiteDTO() {
		super();
	}

	public BookCiteDTO(Integer userId, Integer idWorker, Date day, Time startTime,Integer idService) {
		super();
		this.userId = userId;
		this.idWorker = idWorker;
		this.day = day;
		this.startTime = startTime;
		this.idService = idService;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getIdWorker() {
		return idWorker;
	}

	public void setIdWorker(Integer idWorker) {
		this.idWorker = idWorker;
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
		return Objects.hash(day, idService, idWorker, startTime, userId);
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
				&& Objects.equals(idWorker, other.idWorker) && Objects.equals(startTime, other.startTime)
				&& Objects.equals(userId, other.userId);
	}

	
}
