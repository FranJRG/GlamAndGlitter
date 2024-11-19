package com.jacaranda.glamAndGlitter.model.Dtos;

import java.sql.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class SummaryCites {

	private String username;
	
	@JsonFormat(shape= Shape.STRING, pattern ="yyyy-MM-dd")
	private Date day;
	
	private String serviceName;
	
	private Integer punctuation;
	
	private String comment;
	
	private String workerName;

	public SummaryCites() {
		super();
	}

	public SummaryCites(String username, Date day, String serviceName, 
			Integer punctuation, String comment,String workerName) {
		super();
		this.username = username;
		this.day = day;
		this.serviceName = serviceName;
		this.punctuation = punctuation;
		this.comment = comment;
		this.workerName = workerName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Integer getPunctuation() {
		return punctuation;
	}

	public void setPunctuation(Integer punctuation) {
		this.punctuation = punctuation;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(comment, day, punctuation, serviceName, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SummaryCites other = (SummaryCites) obj;
		return Objects.equals(comment, other.comment) && Objects.equals(day, other.day)
				&& Objects.equals(punctuation, other.punctuation) && Objects.equals(serviceName, other.serviceName)
				&& Objects.equals(username, other.username);
	}
	
}
