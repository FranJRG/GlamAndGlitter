package com.jacaranda.glamAndGlitter.model.Dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetUserDTO {
	
	private Integer id;
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private String role;
	
	private List<EmployeeScheduleDTO>employeeSchedulesDTO = new ArrayList<EmployeeScheduleDTO>();

	public GetUserDTO() {
		super();
	}
	
	public GetUserDTO(Integer id, String name, String email, String phone, String role,
			List<EmployeeScheduleDTO> employeeSchedulesDTO) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.role = role;
		this.employeeSchedulesDTO = employeeSchedulesDTO;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<EmployeeScheduleDTO> getEmployeeSchedules() {
		return employeeSchedulesDTO;
	}

	public void setEmployeeSchedules(List<EmployeeScheduleDTO> employeeSchedulesDTO) {
		this.employeeSchedulesDTO = employeeSchedulesDTO;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, employeeSchedulesDTO, id, name, phone, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GetUserDTO other = (GetUserDTO) obj;
		return Objects.equals(email, other.email) && Objects.equals(employeeSchedulesDTO, other.employeeSchedulesDTO)
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(phone, other.phone) && Objects.equals(role, other.role);
	}
	
}
