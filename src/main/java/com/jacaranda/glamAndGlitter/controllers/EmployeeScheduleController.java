package com.jacaranda.glamAndGlitter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	@Operation(summary = "Asignarle un horario a un usuario, solo administradores podrán ejercer esta función")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/setSchedule/{id}")
	public ResponseEntity<?>setSchedule(@PathVariable String id, @RequestParam String day, @RequestParam String turn){
		List<EmployeeScheduleDTO> schedules = employeeScheduleService.addSchedule(id,day,turn);
		return ResponseEntity.ok().body(schedules);
	}
	
}
