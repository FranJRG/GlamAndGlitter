package com.jacaranda.glamAndGlitter.respository;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jacaranda.glamAndGlitter.model.Cites;
import com.jacaranda.glamAndGlitter.model.User;


public interface CiteRepository extends JpaRepository<Cites, Integer>{
	
	//Método para buscar una cita por su dia trabajador y hora de inicio
	List<Cites> findByDayAndWorkerAndStartTime(Date day, User worker, Time startTime);
	
	//Buscamos la cita de un usuario
	List<Cites> findByUser(User user);
	
	//Método para obtener citas por su fecha es posterior a hoy
	@Query("SELECT c FROM Cites c WHERE c.day > :today")
	List<Cites> findByDayAfterToday(@Param("today") Date today);
	
	//Método para obtener citas entre horas por el id del trabajador, fecha y horario
	@Query("SELECT c FROM Cites c WHERE c.worker.id = :workerId AND c.day = :day AND ( " +
	       "(:startTime BETWEEN c.startTime AND c.endTime) OR " +
	       "(:endTime BETWEEN c.startTime AND c.endTime) OR " +
	       "(c.startTime BETWEEN :startTime AND :endTime) OR " +
	       "(c.endTime BETWEEN :startTime AND :endTime))")
	List<Cites> findCitesBetweenHours(@Param("workerId") Integer workerId, 
	                                @Param("day") Date date, 
	                                @Param("startTime") Time startTime, 
	                                @Param("endTime") Time endTime);
	
	//Buscamos cita por su fecha y hora de inicio
	List<Cites> findByDayAndStartTime(Date date, Time startTime);
	
	//Buscamos citas para dentro de 1 dia o dentro de 1 hora
	@Query("SELECT c FROM Cites c WHERE c.user.id = :userId AND "
	        + "(c.day = :tomorrowDate OR "
	        + "(c.day = :todayDate AND c.startTime BETWEEN :currentTime AND :oneHourLater))")
	List<Cites> findUpcomingAppointments(@Param("userId") Integer userId, 
	                                     @Param("tomorrowDate") LocalDate tomorrowDate, 
	                                     @Param("todayDate") LocalDate todayDate, 
	                                     @Param("currentTime") LocalTime currentTime, 
	                                     @Param("oneHourLater") LocalTime oneHourLater);




	
}
