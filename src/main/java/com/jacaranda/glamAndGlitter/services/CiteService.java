package com.jacaranda.glamAndGlitter.services;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jacaranda.glamAndGlitter.exceptions.ElementNotFoundException;
import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.Cites;
import com.jacaranda.glamAndGlitter.model.ConvertToDTO;
import com.jacaranda.glamAndGlitter.model.EmployeeSchedule;
import com.jacaranda.glamAndGlitter.model.Service;
import com.jacaranda.glamAndGlitter.model.User;
import com.jacaranda.glamAndGlitter.model.Dtos.BookCiteDTO;
import com.jacaranda.glamAndGlitter.respository.CiteRepository;
import com.jacaranda.glamAndGlitter.respository.EmployeeScheduleRepository;
import com.jacaranda.glamAndGlitter.respository.ServiceRepository;
import com.jacaranda.glamAndGlitter.respository.UserRepository;

@org.springframework.stereotype.Service
public class CiteService {

	@Autowired
	private CiteRepository citeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private EmployeeScheduleRepository employeeScheduleRepository;
	
	public List<BookCiteDTO>getCites(){
		return ConvertToDTO.convertCites(citeRepository.findAll());
	}
	
	
	public BookCiteDTO addCite(BookCiteDTO citeDTO) {
		
		isValidDateAndTime(citeDTO.getDay(),citeDTO.getStartTime());
		
		User user = new User();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		List<User> users = userRepository.findByEmail(auth.getName());
		
		if(users.size() > 0) {
			user = users.get(0);
		}else {
			throw new ElementNotFoundException("You must be loggued in");
		}
		
		User worker = userRepository.findById(citeDTO.getIdWorker())
				.orElseThrow(() -> new ElementNotFoundException("Worker not found with this id"));
		
		if(worker.getRole().equals("user")) {
			throw new ValueNotValidException("Worker not found");
		}
		
		Service service = serviceRepository.findById(citeDTO.getIdService()).orElseThrow(() 
				-> new ElementNotFoundException("Service not found with id: " + citeDTO.getIdService()));
		
		EmployeeSchedule schedule = findEmployeeSchedule(citeDTO.getDay(),worker);
				
		
		if((citeDTO.getStartTime().after(Time.valueOf("09:00:00")) && citeDTO.getStartTime().before(Time.valueOf("13:00:00"))) && (schedule.getTurn().equals("Afternoon"))) {
			throw new ValueNotValidException("We are sorry, this employee's schedule this day is only in the afternoon");
		}else if((citeDTO.getStartTime().after(Time.valueOf("13:01:00")) && citeDTO.getStartTime().after(Time.valueOf("21:00:00"))) && schedule.getTurn().equals("Morning")) {
			throw new ValueNotValidException("We are sorry, this employee's schedule this day is only in the morning");			
		}
		
		List<Cites> cites = citeRepository.findByDayAndWorkerAndStartTime(citeDTO.getDay(), worker, citeDTO.getStartTime());
		
		if(cites.size() > 0) {
			throw new ValueNotValidException("This worker have a cite at this time");
		}
		
		Time endTime = calculateEndTime(citeDTO.getStartTime(), Integer.valueOf(service.getDuration()));
		
        List<Cites> citesAux = citeRepository.findCitesBetweenHours(
                citeDTO.getIdWorker(), citeDTO.getDay(),citeDTO.getStartTime(), endTime);
		
        if(citesAux.size() > 0) {
        	throw new ValueNotValidException("At that time the worker will be busy with another appointment, please choose from this time: " + citesAux.get(0).getEndTime());
        }
		
		Cites cite = new Cites(citeDTO.getDay(),citeDTO.getStartTime(), endTime, user, worker, service);
		
		citeRepository.save(cite);
		
		return citeDTO;
	}
	
	public User setAutomaticallyWorkerToCite(Date day, Time startTime) {
		
		String dayOfWeek = day.toLocalDate().getDayOfWeek().toString();
		
		List<EmployeeSchedule>schedules = employeeScheduleRepository.findByDay(dayOfWeek);
		
		List<EmployeeSchedule>availableSchedules = new ArrayList<EmployeeSchedule>();
		
		if(startTime.after(Time.valueOf("09:00:00")) && startTime.before(Time.valueOf("13:00:00"))) {
			availableSchedules = schedules.stream().filter(schedule -> schedule.getTurn().equals("Morning")).collect(Collectors.toList());
		}else if(startTime.after(Time.valueOf("13:01:00")) && startTime.before(Time.valueOf("21:00:00"))) {
			availableSchedules = schedules.stream().filter(schedule -> schedule.getTurn().equals("Afternoon")).collect(Collectors.toList());
		}
		
		if(availableSchedules.isEmpty()) {
			throw new ValueNotValidException("No worker availables at this moment");
		}
		
		Integer index = 0;
		
		while (index < availableSchedules.size()) {
			
			User worker = availableSchedules.get(index).getWorker();
			
			List<Cites> cites = citeRepository.findByDayAndWorkerAndStartTime(day, worker, startTime);
			
	        List<Cites> citesAux = citeRepository.findCitesBetweenHours(
	                worker.getId(), day, startTime, calculateEndTime(startTime, 120));
			
			if(cites.isEmpty() && citesAux.isEmpty()) {
				return worker;
			}
			
			index++;
		}
		
		throw new ElementNotFoundException("No workers availables");
		
	}
	
	public EmployeeSchedule findEmployeeSchedule(Date date,User worker) {
		
		String day = LocalDate.parse(date.toString()).getDayOfWeek().toString();
		
		List<EmployeeSchedule> schedules = employeeScheduleRepository.findByWorkerAndDay(worker, day);
		
		if(schedules.size() > 0) {
			return schedules.get(0);
		}else {
			throw new ElementNotFoundException("This worker not work this day");
		}
	}
	
	public void isValidDateAndTime(Date date, Time startTime) {
		
		if(date.before(Date.valueOf(LocalDate.now()))) {
			throw new ValueNotValidException("Date incorrect, please the day must be after of today");
		}
		
		if(startTime.before(Time.valueOf("09:00:00")) || startTime.after(Time.valueOf("21:00:00"))) {
			throw new ValueNotValidException("Sorry, we are closed at that time.");
		}
		
		if(date.toLocalDate().equals(LocalDate.now()) && startTime.before(Time.valueOf(LocalTime.now()))) {
			throw new ValueNotValidException("Please choose a time that has not passed");
		}
		

	}

    public Time calculateEndTime(Time startTime, int durationInMinutes) {
        long durationInMillis = durationInMinutes * 60 * 1000;
        return new Time(startTime.getTime() + durationInMillis);
    }
	
}
