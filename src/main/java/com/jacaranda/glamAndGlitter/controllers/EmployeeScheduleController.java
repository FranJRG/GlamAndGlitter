package com.jacaranda.glamAndGlitter.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jacaranda.glamAndGlitter.model.Dtos.EmployeeScheduleDTO;
import com.jacaranda.glamAndGlitter.services.EmployeeScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class EmployeeScheduleController {

	@Autowired
	private EmployeeScheduleService employeeScheduleService;
	
	@Operation(summary = "Obtener el horario de un trabajador, solo administradores podrán ejercer esta función")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@GetMapping("/userSchedule/{id}")
	public ResponseEntity<?>getUserSchedule(@PathVariable String id){
		List<EmployeeScheduleDTO> schedules = employeeScheduleService.getUserSchedule(id);
		return ResponseEntity.ok().body(schedules);
	}
	
	@Operation(summary = "Asignarle un horario a un trabajador, solo administradores podrán ejercer esta función")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/setSchedule/{id}")
	public ResponseEntity<?>setSchedule(@PathVariable String id, @RequestParam String day, @RequestParam String turn){
		List<EmployeeScheduleDTO> schedules = employeeScheduleService.addSchedule(id,day,turn);
		return ResponseEntity.ok().body(schedules);
	}
	
	@Operation(summary = "Actualizar el horario a un trabajador, solo administradores podrán ejercer esta función")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PutMapping("/updateSchedule/{id}")
	public ResponseEntity<?>updateSchedule(@PathVariable String id,@RequestParam String day, @RequestParam String turn,
			@RequestParam Optional<Integer> userId){
		EmployeeScheduleDTO scheduleDTO = employeeScheduleService.updateSchedule(id,day,turn,userId.orElse(0));
		return ResponseEntity.ok().body(scheduleDTO);
	}

	
}
