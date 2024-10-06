package com.jacaranda.glamAndGlitter.services;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jacaranda.glamAndGlitter.exceptions.ElementNotFoundException;
import com.jacaranda.glamAndGlitter.exceptions.RoleNotValidException;
import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.Cites;
import com.jacaranda.glamAndGlitter.model.ConvertToDTO;
import com.jacaranda.glamAndGlitter.model.EmployeeSchedule;
import com.jacaranda.glamAndGlitter.model.Service;
import com.jacaranda.glamAndGlitter.model.User;
import com.jacaranda.glamAndGlitter.model.Dtos.BookCiteDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;
import com.jacaranda.glamAndGlitter.respository.CiteRepository;
import com.jacaranda.glamAndGlitter.respository.EmployeeScheduleRepository;
import com.jacaranda.glamAndGlitter.respository.ServiceRepository;
import com.jacaranda.glamAndGlitter.respository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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
	
	@Autowired
	private JavaMailSender mailSender;
	
	public List<BookCiteDTO>getCites(){
		return ConvertToDTO.convertCites(citeRepository.findAll());
	}
	
	
	public BookCiteDTO addCite(BookCiteDTO citeDTO) {
		
		isValidDateAndTime(citeDTO.getDay(),citeDTO.getStartTime());
		
		User userLoggued = loginApp();
		
		Service service = serviceRepository.findById(citeDTO.getIdService()).orElseThrow(() 
				-> new ElementNotFoundException("Service not found with id: " + citeDTO.getIdService()));
		
		
		Time endTime = calculateEndTime(citeDTO.getStartTime(), Integer.valueOf(service.getDuration()));
		
		Cites cite = new Cites(citeDTO.getDay(),citeDTO.getStartTime(), endTime, userLoggued, service);
		
		citeRepository.save(cite);
		
		return citeDTO;
	}
	
	public GetUserDTO setManuallyWorker(String idCite, String idWorker) {
		
		Integer id = convertStringToInteger(idCite);
		
		Cites cite = citeRepository.findById(id).orElseThrow(() -> 
		new ElementNotFoundException("Cite not found with id: " + id)); 
		
		User worker = new User(); 
		
		if(cite.getWorker().getId() == null) {
			worker = setAutomaticallyWorkerToCite(cite.getDay(), cite.getStartTime());
		}else {
			Integer idWorkerInteger = convertStringToInteger(idWorker);
			
			worker = userRepository.findById(idWorkerInteger)
					.orElseThrow(() -> new ElementNotFoundException("Worker not found with this id"));
		}
		
		if(worker.getRole().equals("user")) {
			throw new ValueNotValidException("Worker not found");
		}
		
		EmployeeSchedule schedule = findEmployeeSchedule(cite.getDay(),worker);
		
		checkTime(cite.getStartTime(),schedule);
        
        checkCiteAvailability(cite,worker);
        
		cite.setWorker(worker);
		citeRepository.save(cite);
		
		GetUserDTO workerDTO = new GetUserDTO(worker.getId(),worker.getName(),worker.getEmail(),
				worker.getPhone(),worker.getRole(),ConvertToDTO.getEmployeeScheduleDTO(worker.getEmployeeSchedules()));
		
		return workerDTO;
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
	
	public BookCiteDTO updateCite(String idCite,BookCiteDTO newCiteDTO) {
		
		Integer id = convertStringToInteger(idCite);
		
		Cites cite = citeRepository.findById(id).orElseThrow(() -> 
				new ElementNotFoundException("Cite not found with id: " + id));
		
		User userLoggued = loginApp();
		
		if((userLoggued.getRole().equals("admin")) || userLoggued.getId().equals(cite.getUser().getId())) {
			
			if(newCiteDTO.getDay() != null) {
				
				isValidDateAndTime(newCiteDTO.getDay(), newCiteDTO.getStartTime());
				cite.setDay(newCiteDTO.getDay());
				
			}
			
			if(newCiteDTO.getStartTime() != null) {
				
				isValidDateAndTime(newCiteDTO.getDay(), newCiteDTO.getStartTime());
				cite.setStartTime(newCiteDTO.getStartTime());
				
			}
			
			if(newCiteDTO.getIdService() != null) {
				
				Service newService = serviceRepository.findById(newCiteDTO.getIdService()).orElseThrow(() -> 
				new ElementNotFoundException("Service not found with id: " + newCiteDTO.getIdService()));
				cite.setService(newService);
				
			}
			
			if(newCiteDTO.getDay() != null || newCiteDTO.getStartTime() != null) {
				
				EmployeeSchedule schedule = findEmployeeSchedule(cite.getDay(),cite.getWorker());
				checkTime(cite.getStartTime(),schedule);
				checkCiteAvailability(cite, cite.getWorker());
				
			}
			
			citeRepository.save(cite);
		}else {
			throw new RoleNotValidException("Only admins can update cites of other users");
		}
		
		
		return newCiteDTO;
		
	}
	
	public BookCiteDTO removeCite(String idCite) throws UnsupportedEncodingException, MessagingException {
		Integer id = convertStringToInteger(idCite);
		
		User userLoggued = loginApp();
		
		Cites cite = citeRepository.findById(id).orElseThrow(() -> 
					new ElementNotFoundException("Cite not found with id: " + id));
		
		BookCiteDTO citeDTO = new BookCiteDTO();
		
		if(userLoggued.getRole().equals("admin") || userLoggued.getId().equals(cite.getUser().getId())) {
			
			if(cite.getDay().toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
			    throw new ValueNotValidException("Sorry, you cannot change a cite that is today or in the past.");
			}
			
			citeRepository.delete(cite);
			citeDTO = new BookCiteDTO(cite.getDay(),cite.getStartTime(),cite.getService().getId());
			sendAlertMessage(userLoggued,cite);
			List<User>users = userRepository.findByRoleIn(Arrays.asList("admin", "stylist"));
			
			users.stream().forEach((user -> {
				try {
					sendAlertMessage(user,cite);
				} catch (UnsupportedEncodingException | MessagingException e) {
					e.printStackTrace();
				}
			}));;
			
		}else {
			throw new ValueNotValidException("Only admins can remove other cites");
		}
		return citeDTO;
	}
	
	public EmployeeSchedule findEmployeeSchedule(Date date,User worker) {
		
		String day = LocalDate.parse(date.toString()).getDayOfWeek().toString();
		
		List<EmployeeSchedule> schedules = employeeScheduleRepository.findByWorkerAndDay(worker, day);
		
		if(!schedules.isEmpty()) {
			return schedules.get(0);
		}else {
			throw new ElementNotFoundException("This worker not work this day");
		}
	}
	
	public void checkCiteAvailability(Cites cite, User worker) {
		
		List<Cites> cites = citeRepository.findByDayAndWorkerAndStartTime(cite.getDay(), worker, cite.getStartTime());
		
		if(!cites.isEmpty()) {
			throw new ValueNotValidException("This worker have a cite at this time");
		}
		
        List<Cites> citesAux = citeRepository.findCitesBetweenHours(
                cite.getWorker().getId(), cite.getDay(),cite.getStartTime(), cite.getEndTime());
		
        if(!citesAux.isEmpty()) {
        	throw new ValueNotValidException("At that time the worker will be busy with another appointment, please choose from this time: " + citesAux.get(0).getEndTime());
        }
        
	}
	
	public void sendAlertMessage(User user, Cites cite) throws MessagingException, UnsupportedEncodingException {
		 String toAddress = user.getEmail();

	    String fromAddress = "a.fraramgar@gmail.com";
	    String senderName = "Glam&Glitter";

	    String subject = "Cite canceled";
	    String content = "";
	    if(user.getRole().equals("admin") || user.getRole().equals("stylist")) {
	    	content = "Dear [[user]],<br><br>"
	    			+ "We notify you that the appointment We notify you that the appointment for the day " + cite.getDay() 
	    			+ " by the user " + cite.getUser().getName() + " has been canceled";
	    }else if(user.getRole().equals("user")) {
	    	content = "Dear [[user]],<br><br>"
	    			+ "We inform you that your appointment for the day has been cancelled.";
	    }



	    MimeMessage message = mailSender.createMimeMessage();

	    MimeMessageHelper helper = new MimeMessageHelper(message);


	    helper.setFrom(fromAddress, senderName);
	    helper.setTo(toAddress);
	    helper.setSubject(subject);


	    content = content.replace("[[user]]", user.getName());


	    helper.setText(content, true);

	    mailSender.send(message);
	}
	
	public void isValidDateAndTime(Date date, Time startTime) {
		
		if(date != null) {
			if(date.before(Date.valueOf(LocalDate.now()))) {
				throw new ValueNotValidException("Date incorrect, the day must be after today");
			}
		}
		
		if(startTime != null) {
			if(startTime.before(Time.valueOf("09:00:00")) || startTime.after(Time.valueOf("21:00:00"))) {
				throw new ValueNotValidException("Sorry, we are closed at that time.");
			}
		}
		
		if(date != null && startTime != null) {
			if(date.toLocalDate().equals(LocalDate.now()) && startTime.before(Time.valueOf(LocalTime.now()))) {
				throw new ValueNotValidException("Please choose a time that has not passed");
			}			
		}
	}
	
	public List<BookCiteDTO> findByDateAndTime(LocalDate date, String time){
		
		Time newTime = convertToTime(time);
		
		List<Cites>cites = citeRepository.findByDayAndStartTime(Date.valueOf(date), newTime);
		List<BookCiteDTO> citesDTO =  ConvertToDTO.convertCites(cites);
		return citesDTO;
	}
	
	public void checkTime(Time startTime, EmployeeSchedule schedule) {
		
		if((startTime.after(Time.valueOf("09:00:00")) && startTime.before(Time.valueOf("13:00:00"))) && (schedule.getTurn().equals("Afternoon"))) {
			
			throw new ValueNotValidException("We are sorry, this employee's schedule this day is only in the afternoon");
		
		}else if((startTime.after(Time.valueOf("13:01:00")) && startTime.before(Time.valueOf("21:00:00"))) && schedule.getTurn().equals("Morning")) {
			
			throw new ValueNotValidException("We are sorry, this employee's schedule this day is only in the morning");			
		
		}
		
	}
	
	public User loginApp() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<User> users = userRepository.findByEmail(auth.getName());
		
		if(users.size() == 0) {
			throw new ElementNotFoundException("You must be loggued in");
		}
		
		return users.get(0);
	}
	
	public Integer convertStringToInteger(String idString) {
		Integer id;
		try {
			id = Integer.parseInt(idString);
		}catch(NumberFormatException e) {
			throw new ValueNotValidException("Id must be numeric");
		}
		
		return id;
	}
	
	public Time convertToTime(String time) {
		Time newTime;
		
		try {
			newTime = Time.valueOf(time);	
		}catch(IllegalArgumentException e) {
			throw new ValueNotValidException("Time format must be HH:mm:ss");
		}
		return newTime;
	}

    public Time calculateEndTime(Time startTime, int durationInMinutes) {
        long durationInMillis = durationInMinutes * 60 * 1000;
        return new Time(startTime.getTime() + durationInMillis);
    }
	
}
