package com.jacaranda.glamAndGlitter.model;

import java.util.List;
import java.util.stream.Collectors;

import com.jacaranda.glamAndGlitter.model.Dtos.BookCiteDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.CategoryDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.EmployeeScheduleDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.GetPendingCiteDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.RatingDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.ServiceDTO;

public class ConvertToDTO {
	
	/**
	 * Clase para convertir listas a listasDTOs
	 * @param List<T>
	 * @return List<Tdto>
	 */

	public static List<GetUserDTO>getUsersDTO(List<User>users){
		return users.stream().map(user -> new GetUserDTO(user.getId(),user.getName(),user.getEmail(),user.getPhone(),
				user.getRole(),ConvertToDTO.getEmployeeScheduleDTO(user.getEmployeeSchedules()),user.getCalendarNotifications(),user.getEmailNotifications())).collect(Collectors.toList());
	}

	public static List<EmployeeScheduleDTO>getEmployeeScheduleDTO(List<EmployeeSchedule>employeeSchedule){
		return employeeSchedule.stream().map(schedule -> 
									new EmployeeScheduleDTO(schedule.getTurn(),schedule.getDay())).collect(Collectors.toList());
	}
	
	public static List<ServiceDTO>getServicesDTO(List<Service>services){
		return services.stream().map(service -> new ServiceDTO(service.getId(),service.getName(),service.getDescription()
				,service.getPrice(),service.getActive(),service.getCategory().getName(),service.getImageUrl(),service.getDuration())).collect(Collectors.toList());
	}
	
	public static List<CategoryDTO>getCategoriesDTO(List<Category>categories){
		return categories.stream().map(category -> new CategoryDTO(category.getId(),category.getName())).collect(Collectors.toList());
	}
	
	public static List<BookCiteDTO>convertCites(List<Cites>cites){
		return cites.stream().map(cite -> new BookCiteDTO(cite.getDay(),cite.getStartTime(),
				cite.getService().getId())).collect(Collectors.toList());
	}
	
	public static List<GetPendingCiteDTO>getPendingCitesDTO(List<Cites>cites){
		return cites.stream().map(cite -> new GetPendingCiteDTO(cite.getId(),cite.getDay(),cite.getStartTime(),
				cite.getService().getId(),cite.getUser().getName(),cite.getEventId())).collect(Collectors.toList());
	}
	
	public static List<RatingDTO>convertToRatingDTO(List<Rating>ratings){
		return ratings.stream().map(rating -> new RatingDTO(rating.getPunctuation(),rating.getMessage())).collect(Collectors.toList());
	}

}
