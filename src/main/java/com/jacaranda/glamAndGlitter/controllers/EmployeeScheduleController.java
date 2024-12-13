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
import io.swagger.v3.oas.annotations.Parameter;
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
	public ResponseEntity<?>getUserSchedule(@Parameter(description = "ID del trabajador") @PathVariable String id){
		List<EmployeeScheduleDTO> schedules = employeeScheduleService.getUserSchedule(id);
		return ResponseEntity.ok().body(schedules);
	}
	
	@Operation(summary = "Asignarle un horario a un trabajador, solo administradores podrán ejercer esta función")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PostMapping("/setSchedule/{id}")
	public ResponseEntity<?>setSchedule(@Parameter(description = "ID del trabajador") @PathVariable String id,@Parameter(description = "Dia de la semana (Monday,Tuesday,etc...)") @RequestParam String day,@Parameter(description = "Turno (Morning | Afternoon)") @RequestParam String turn){
		List<EmployeeScheduleDTO> schedules = employeeScheduleService.addSchedule(id,day,turn);
		return ResponseEntity.ok().body(schedules);
	}
	
	@Operation(summary = "Actualizar el horario a un trabajador o crearle uno en caso de no tener, solo administradores podrán ejercer esta función")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "200", description = "Complete!")
	})
	@PutMapping("/updateSchedule/{id}")
	public ResponseEntity<?>updateSchedule(@Parameter(description = "ID del horario (Si no existe se creará uno con el id autoincrementado)") @PathVariable String id,@Parameter(description = "Dia de la semana de trabajo (Monday,Tuestay,etc...)") @RequestParam String day,@Parameter(description = "Turno (Morning | Afternoon, Clear (para eliminar el turno), Duplicate(Para añadir un turno extra a ese día, es decir si tiene turno de mañana se añadirá también de tarde)") @RequestParam String turn,
			@Parameter(description = "ID del trabajador") @RequestParam Optional<String> userId){
		EmployeeScheduleDTO scheduleDTO = employeeScheduleService.updateSchedule(id,day,turn,userId.orElse(""));
		return ResponseEntity.ok().body(scheduleDTO);
	}

	
}
