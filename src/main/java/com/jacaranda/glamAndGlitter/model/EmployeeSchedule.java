package com.jacaranda.glamAndGlitter.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EmployeeSchedule")
public class EmployeeSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String turn;
	
	private String day;
	
	@ManyToOne
	@JoinColumn(name = "idWorker")
	private User worker;
	
	public EmployeeSchedule() {
		super();
	}

	public EmployeeSchedule(User worker, String turn, String day) {
		super();
		this.turn = turn;
		this.day = day;
		this.worker = worker;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTurn() {
		return turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public User getWorker() {
		return worker;
	}

	public void setWorker(User worker) {
		this.worker = worker;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeeSchedule other = (EmployeeSchedule) obj;
		return Objects.equals(id, other.id);
	}
	
}
