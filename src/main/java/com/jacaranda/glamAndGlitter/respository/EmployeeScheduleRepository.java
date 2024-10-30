package com.jacaranda.glamAndGlitter.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jacaranda.glamAndGlitter.model.EmployeeSchedule;
import java.util.List;
import com.jacaranda.glamAndGlitter.model.User;


public interface EmployeeScheduleRepository extends JpaRepository<EmployeeSchedule, Integer>{

	//Método para buscar horarios por su trabajador y dia de la semana
	List<EmployeeSchedule> findByWorkerAndDay(User worker, String day);
	
	//Método para obtener un horario por su dia
	List<EmployeeSchedule> findByDay(String day);
	
}
