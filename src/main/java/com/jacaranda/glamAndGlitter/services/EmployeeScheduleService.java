package com.jacaranda.glamAndGlitter.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacaranda.glamAndGlitter.exceptions.ElementNotFoundException;
import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.ConvertToDTO;
import com.jacaranda.glamAndGlitter.model.EmployeeSchedule;
import com.jacaranda.glamAndGlitter.model.User;
import com.jacaranda.glamAndGlitter.model.Dtos.EmployeeScheduleDTO;
import com.jacaranda.glamAndGlitter.respository.EmployeeScheduleRepository;
import com.jacaranda.glamAndGlitter.respository.UserRepository;

@Service
public class EmployeeScheduleService {

	@Autowired
	private EmployeeScheduleRepository employeeScheduleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CiteService citeService;
	
	/**
	 * Método para obtener el horario de un trabajador
	 */
	
	public List<EmployeeScheduleDTO> getUserSchedule(String idString){
		
		Integer id;
		try {
			id = Integer.parseInt(idString);
		}catch(NumberFormatException e) {
			throw new ValueNotValidException("Id must be numeric");
		}
		
		User worker = userRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("User not found with this id"));
		
		if(worker.getRole().equals("user") || worker.getRole().equals("admin")) {
			throw new ValueNotValidException("This user is not a worker!");
		}
		
		return ConvertToDTO.getEmployeeScheduleDTO(worker.getEmployeeSchedules());
		
		
	}
	
	/**
	 * Método para añadir un horario
	 * Comprobamos que le pasemos los datos necesarios
	 * Buscamos el trabajador por el id y comprobamos que sea trabajador y no usuario
	 * Creamos el horario y devolvemos la lista de horarios creados
	 * @param idString
	 * @param day
	 * @param turn
	 * @return
	 */
	public List<EmployeeScheduleDTO> addSchedule(String idString, String turnNormal,String dayNormal) {
		
		List<String> daysOfWeek = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY");
		List<String> availablesTurns = Arrays.asList("MORNING","AFTERNOON","CLEAR","DUPLICATE");
		
		Integer id = citeService.convertStringToInteger(idString);
		
		
		if(dayNormal == null || dayNormal.isEmpty()) {
			throw new ValueNotValidException("Day can't be null");
		}
		
		if(turnNormal == null || turnNormal.isEmpty()) {
			throw new ValueNotValidException("Turn can't be null");
		}
		
		if(!daysOfWeek.contains(dayNormal.toUpperCase())) {
			throw new ValueNotValidException("This day is not valid!");
		}

		if(!availablesTurns.contains(turnNormal.toUpperCase())) {
			throw new ValueNotValidException("This turn is not valid!");
		}
		
		//Normalizamos las variables introducidas por el usuario para que se guarden adaptadas a la base de datos
		String day = capitalizeFirstLetter(dayNormal);
		String turn = capitalizeFirstLetter(turnNormal);
		
		List<EmployeeScheduleDTO>schedulesTemp = new ArrayList<EmployeeScheduleDTO>();
		
		User worker = userRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Worker not found with this id"));
		
		if(!worker.getRole().equals("stylist")) {
			throw new ValueNotValidException("Only workers can have schedules");
		}
		
		List<EmployeeSchedule>aux = employeeScheduleRepository.findByWorkerAndDay(worker, day);
		
		if(aux.size() > 0) {
			throw new ValueNotValidException("This worker already have a schedule for this day, is the id: " + aux.get(0).getId());
		}
		
		if(aux.size() == 0 && (turn.equals("Clear") || turn.equals("Duplicate"))) {
			throw new ValueNotValidException("This turn is not valid for create a schedule");
		}
		
		EmployeeSchedule employeeSchedule = new EmployeeSchedule(worker,turn,day);
		EmployeeScheduleDTO employeeScheduleDto = new EmployeeScheduleDTO(employeeSchedule.getId(),turn,employeeSchedule.getDay());
		schedulesTemp.add(employeeScheduleDto);
		employeeScheduleRepository.save(employeeSchedule);
		
		return schedulesTemp;
	}
	
	public EmployeeScheduleDTO updateSchedule(String idString, String dayNormal, String turnNormal, String userIdString){
		
		List<String> daysOfWeek = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY");
		List<String> availablesTurns = Arrays.asList("MORNING","AFTERNOON","CLEAR","DUPLICATE");
		
		Integer id;
		try {
			id = Integer.parseInt(idString);
		}catch(NumberFormatException e) {
			throw new ValueNotValidException("Id must be numeric");
		}
		
		Integer userId;
		try {
			userId = Integer.parseInt(userIdString);
		}catch(NumberFormatException e) {
			throw new ValueNotValidException("User id must be numeric");
		}
		
		if(turnNormal == null || turnNormal.isEmpty()) {
			throw new ValueNotValidException("Turn can't be empty");
		}
		
		if(!daysOfWeek.contains(dayNormal.toUpperCase())) {
			throw new ValueNotValidException("This day is not valid!");
		}
		
		if(!availablesTurns.contains(turnNormal.toUpperCase())) {
			throw new ValueNotValidException("This turn is not valid!");
		}
		
		EmployeeScheduleDTO schedule;
		User worker = userRepository.findById(userId).orElseThrow(() -> new ElementNotFoundException("Worker not found with this id!"));
		
		if(!worker.getRole().equals("stylist")){
			throw new ValueNotValidException("This user is not a worker");
		}
		
		EmployeeSchedule employeeSchedule = employeeScheduleRepository.findById(id).orElse(null);
		
		//Normalizamos el turno introducido por el usuario para que no haya errores en base de datos
		String turn = capitalizeFirstLetter(turnNormal);
		String day = capitalizeFirstLetter(dayNormal);
		
		if(employeeSchedule != null) {
			
			if(!worker.getEmployeeSchedules().contains(employeeSchedule)) {
				throw new ValueNotValidException("This schedule is not of this worker");
			}
			
			if(!turn.equals("Clear")) {
				
				if(turn.equals("Duplicate")) {
					
					List<EmployeeSchedule>schedulesByDays = employeeScheduleRepository.findByWorkerAndDay(worker, day);
					
					if(schedulesByDays.size() == 2) {
						throw new ValueNotValidException("This worker already have duplicate turn for this day");
					}
					
					if(employeeSchedule.getTurn().equals("Morning")) {
						addSchedule(employeeSchedule.getWorker().getId().toString(),"Afternoon",employeeSchedule.getDay());					
					}else if(employeeSchedule.getTurn().equals("Afternoon")) {
						addSchedule(employeeSchedule.getWorker().getId().toString(),"Morning",employeeSchedule.getDay());										
					}
					
				}else {
					
					if(turn != null && !turn.isEmpty()) {
						employeeSchedule.setTurn(turn);
					}
					
					employeeScheduleRepository.save(employeeSchedule);
				}			
				
				schedule = new EmployeeScheduleDTO(employeeSchedule.getId(),employeeSchedule.getTurn(),employeeSchedule.getDay());
				
			}else {
				employeeScheduleRepository.delete(employeeSchedule);			
			}				
			schedule = new EmployeeScheduleDTO(employeeSchedule.getId(),employeeSchedule.getTurn(),employeeSchedule.getDay());
		}else {
			
			List<EmployeeSchedule>aux = employeeScheduleRepository.findByWorkerAndDay(worker, day);
			
			if(aux.size() > 0) {
				throw new ValueNotValidException("This worker already have a schedule for this day, is the id: " + aux.get(0).getId());
			}
			
			if(!turn.equals("Afternoon") && !turn.equals("Morning")) {
				throw new ValueNotValidException("This schedule does not exist yet and we can't duplicate or clear it!");
			}
			
			schedule = addSchedule(userId.toString(),turn,day).get(0);
		}
		
		
		return schedule;
		
	}
	
	public String capitalizeFirstLetter(String input) {
	    if (input == null || input.isEmpty()) {
	        return input;
	    }
	    return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}

	
}
