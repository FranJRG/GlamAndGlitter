package com.jacaranda.glamAndGlitter.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jacaranda.glamAndGlitter.model.User;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer>{
	
	//Obtenemos un usuario por su email
	List<User> findByEmail(String email);
	
	//Obtenemos un usuario por su nombre
	List<User> findByName(String name);
	
	//Obtenemos un usuario por su rol
	List<User> findByRoleIn(List<String> roles);
	
	//Obtenemos usuarios por su rol
	List<User> findByRoleAndEmployeeSchedulesNull(String role);
	
	//Obtenemos usuarios que tengan activas o desactivas sus notificaciones de email
	List<User> findAllByEmailNotifications(Boolean emailNotifications);
	
}
