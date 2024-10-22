package com.jacaranda.glamAndGlitter.respository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jacaranda.glamAndGlitter.model.Cites;
import com.jacaranda.glamAndGlitter.model.User;


public interface CiteRepository extends JpaRepository<Cites, Integer>{
	
	List<Cites> findByDayAndWorkerAndStartTime(Date day, User worker, Time startTime);
	
	List<Cites> findByUser(User user);
	
	@Query("SELECT c FROM Cites c WHERE c.day > :today")
	List<Cites> findByDayAfterToday(@Param("today") Date today);
	
	@Query("SELECT c FROM Cites c WHERE c.worker.id = :workerId AND c.day = :day AND ( " +
	       "(:startTime BETWEEN c.startTime AND c.endTime) OR " +
	       "(:endTime BETWEEN c.startTime AND c.endTime) OR " +
	       "(c.startTime BETWEEN :startTime AND :endTime) OR " +
	       "(c.endTime BETWEEN :startTime AND :endTime))")
	List<Cites> findCitesBetweenHours(@Param("workerId") Integer workerId, 
	                                @Param("day") Date date, 
	                                @Param("startTime") Time startTime, 
	                                @Param("endTime") Time endTime);
	
	List<Cites> findByDayAndStartTime(Date date, Time startTime);

	
}
