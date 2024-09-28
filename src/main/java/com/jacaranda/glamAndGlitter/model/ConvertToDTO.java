package com.jacaranda.glamAndGlitter.model;

import java.util.List;
import java.util.stream.Collectors;

import com.jacaranda.glamAndGlitter.model.Dtos.CategoryDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.EmployeeScheduleDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.ServiceDTO;

public class ConvertToDTO {

	public static List<GetUserDTO>getUsersDTO(List<User>users){
		return users.stream().map(user -> new GetUserDTO(user.getId(),user.getName(),
				user.getEmail(),user.getPhone(),user.getRole(),getEmployeeScheduleDTO(user.getEmployeeSchedules()))).collect(Collectors.toList());
	}

	public static List<EmployeeScheduleDTO>getEmployeeScheduleDTO(List<EmployeeSchedule>employeeSchedule){
		return employeeSchedule.stream().map(schedule -> 
									new EmployeeScheduleDTO(schedule.getTurn(),schedule.getDay())).collect(Collectors.toList());
	}
	
	public static List<ServiceDTO>getServicesDTO(List<Service>services){
		return services.stream().map(service -> new ServiceDTO(service.getId(),service.getName(),service.getDescription()
				,service.getPrice(),service.getActive(),service.getCategory().getName())).collect(Collectors.toList());
	}
	
	public static List<CategoryDTO>getCategoriesDTO(List<Category>categories){
		return categories.stream().map(category -> new CategoryDTO(category.getId(),category.getName())).collect(Collectors.toList());
	}

}
