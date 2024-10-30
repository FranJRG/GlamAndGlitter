package com.jacaranda.glamAndGlitter.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacaranda.glamAndGlitter.exceptions.ElementNotFoundException;
import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
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
	 * Método para añadir un horario
	 * Comprobamos que le pasemos los datos necesarios
	 * Buscamos el trabajador por el id y comprobamos que sea trabajador y no usuario
	 * Creamos el horario y devolvemos la lista de horarios creados
	 * @param idString
	 * @param day
	 * @param turn
	 * @return
	 */
	public List<EmployeeScheduleDTO> addSchedule(String idString, String day,String turn) {
		
		Integer id = citeService.convertStringToInteger(idString);
		
		
		if(day == null || day.isEmpty()) {
			throw new ValueNotValidException("Day can't be null");
		}
		
		if(turn == null || turn.isEmpty()) {
			throw new ValueNotValidException("Turn can't be null");
		}
		
		List<EmployeeScheduleDTO>schedulesTemp = new ArrayList<EmployeeScheduleDTO>();
		
		
		User worker = userRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Worker not found with this id"));
		
		if(!worker.getRole().equals("stylist")) {
			throw new ValueNotValidException("Only workers can have schedules");
		}
		
		EmployeeSchedule employeeSchedule = new EmployeeSchedule(worker,turn,day);
		EmployeeScheduleDTO employeeScheduleDto = new EmployeeScheduleDTO(day,turn);
		schedulesTemp.add(employeeScheduleDto);
		employeeScheduleRepository.save(employeeSchedule);
		
		return schedulesTemp;
	}
	
}
