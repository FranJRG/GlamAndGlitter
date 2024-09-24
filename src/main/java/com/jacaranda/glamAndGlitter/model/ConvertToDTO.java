package com.jacaranda.glamAndGlitter.model;

import java.util.List;

import com.jacaranda.glamAndGlitter.model.Dtos.EmployeeScheduleDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;

public class ConvertToDTO {

	public static List<GetUserDTO>getUsersDTO(List<User>users){
		return users.stream().map(user -> new GetUserDTO(user.getId(),user.getName(),
				user.getEmail(),user.getPhone(),user.getRole(),getEmployeeScheduleDTO(user.getEmployeeSchedules()))).toList();
	}
	
	public static List<EmployeeScheduleDTO>getEmployeeScheduleDTO(List<EmployeeSchedule>employeeSchedule){
		return employeeSchedule.stream().map(schedule -> 
									new EmployeeScheduleDTO(schedule.getTurn(),schedule.getDay())).toList();
	}
	
}
