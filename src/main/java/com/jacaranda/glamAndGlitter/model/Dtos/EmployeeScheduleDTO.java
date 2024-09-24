package com.jacaranda.glamAndGlitter.model.Dtos;

import java.util.Objects;

public class EmployeeScheduleDTO {
	
	private String turn;
	
	private String day;
	
	public EmployeeScheduleDTO() {
		super();
	}

	public EmployeeScheduleDTO(String turn, String day) {
		super();
		this.turn = turn;
		this.day = day;
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
