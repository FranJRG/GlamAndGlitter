package com.jacaranda.glamAndGlitter.model.Dtos;

import java.util.Objects;

public class EmployeeScheduleDTO {
	
	private Integer id;
	
	private String turn;
	
	private String day;
	
	public EmployeeScheduleDTO() {
		super();
	}

	public EmployeeScheduleDTO(Integer id,String turn,String day) {
		super();
		this.id = id;
		this.day = day;
		this.turn = turn;
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

	@Override
	public int hashCode() {
		return Objects.hash(day, turn);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeeScheduleDTO other = (EmployeeScheduleDTO) obj;
		return Objects.equals(day, other.day) && Objects.equals(turn, other.turn);
	}
	
}
