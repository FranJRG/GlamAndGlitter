package com.jacaranda.glamAndGlitter.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jacaranda.glamAndGlitter.model.EmployeeSchedule;
import java.util.List;
import com.jacaranda.glamAndGlitter.model.User;


public interface EmployeeScheduleRepository extends JpaRepository<EmployeeSchedule, Integer>{

	List<EmployeeSchedule> findByWorkerAndDay(User worker, String day);
	
	List<EmployeeSchedule> findByDay(String day);
	
}
