package com.jacaranda.glamAndGlitter.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jacaranda.glamAndGlitter.model.User;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer>{
	
	List<User> findByEmail(String email);
	
	List<User> findByName(String name);
	
	List<User> findByRoleIn(List<String> roles);
	
	List<User> findByRoleAndEmployeeSchedulesNull(String role);
	
	List<User> findAllByEmailNotifications(Boolean emailNotifications);
	
}
