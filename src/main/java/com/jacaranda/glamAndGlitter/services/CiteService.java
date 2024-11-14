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
import com.jacaranda.glamAndGlitter.model.Dtos.GetPendingCiteDTO;
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
	
	/**
	 * Método para obtener citas pendientes
	 * @return
	 */
	public List<GetPendingCiteDTO> getPendingCites(){
		return ConvertToDTO.getPendingCitesDTO(citeRepository.findByDayAfterToday(Date.valueOf(LocalDate.now())));
	}
	
	/**
	 * Método para obtener citas de un usuario
	 * @param idString
	 * @return
	 */
	public GetPendingCiteDTO getCite(String idString) {
		Integer id = convertStringToInteger(idString);
		Cites cite = citeRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Cite not found"));
		GetPendingCiteDTO citeDTO = new GetPendingCiteDTO(cite.getId(),cite.getDay(),cite.getStartTime(),cite.getService().getId(),
				cite.getUser().getName(),cite.getEventId(),cite.getWorker().getId());
		return citeDTO;
	}
	
	/**
	 * Modificar
	 * @param idString
	 * @return
	 */
	public List<GetPendingCiteDTO>myCites(String idString){
		
		Integer id = convertStringToInteger(idString);
		
		User user = userRepository.findById(id).orElseThrow(() -> 
			new ElementNotFoundException("User not found"));
		
		if(!user.getRole().equals("user")) {
			throw new ValueNotValidException("User must be user for this action");
		}
		
		return ConvertToDTO.getPendingCitesDTO(citeRepository.findByUser(user));
	}
	
	/**
	 * Método para añadir una cita
	 * Comprobamos que esten los campos requeridos
	 * Comprobamos la validez de los datos y disponibilidad de los mismos
	 * Por defecto establecemos un empleado de forma automática
	 * @param citeDTO
	 * @return
	 */
	public BookCiteDTO addCite(BookCiteDTO citeDTO) {
		
		if(citeDTO.getDay() == null) {
			throw new ValueNotValidException("Date can't be null");
		}
		
		if(citeDTO.getIdService() == null) {
			throw new ValueNotValidException("Id service can't be null");
		}
		
		if(citeDTO.getStartTime() == null) {
			throw new ValueNotValidException("Time can't be null");
		}
		
		isValidDateAndTime(citeDTO.getDay(),citeDTO.getStartTime());
		
		User userLoggued = loginApp();
		
		Service service = serviceRepository.findById(citeDTO.getIdService()).orElseThrow(() 
				-> new ElementNotFoundException("Service not found with id: " + citeDTO.getIdService()));
		
		
		Time endTime = calculateEndTime(citeDTO.getStartTime(), Integer.valueOf(service.getDuration()));
		List<BookCiteDTO> cites = findByDateAndTime(citeDTO.getDay().toLocalDate(), String.valueOf(citeDTO.getStartTime()), String.valueOf(endTime));

		if(!cites.isEmpty()) {
			throw new ValueNotValidException("No employee for this date and time");
		}
		
		User worker = setAutomaticallyWorkerToCite(citeDTO.getDay(), citeDTO.getStartTime());
		
		Cites cite = new Cites(citeDTO.getDay(),citeDTO.getStartTime(), worker, endTime,userLoggued, service, citeDTO.getEventId());
		
		citeRepository.save(cite);
		
		return citeDTO;
	}
	
	/**
	 * Método para establecer el trabajador manualmente
	 * Buscamos el trabajador y comprobamos su disponibilidad 
	 * @param idCite
	 * @param idWorker
	 * @return
	 */
	public GetUserDTO setWorker(String idCite, String idWorker) {
		
		Integer id = convertStringToInteger(idCite);
		
		Cites cite = citeRepository.findById(id).orElseThrow(() -> 
		new ElementNotFoundException("Cite not found with id: " + id)); 
		
		User worker = new User(); 
		
		if(idWorker == null || idWorker.isEmpty()) {
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
        
		cite.setWorker(worker);
        checkCiteAvailability(cite,worker);
        
		citeRepository.save(cite);
		
		GetUserDTO workerDTO = new GetUserDTO(worker.getId(),worker.getName(),worker.getEmail(),worker.getPhone(),
				worker.getRole(),ConvertToDTO.getEmployeeScheduleDTO(worker.getEmployeeSchedules()),worker.getCalendarNotifications(),worker.getEmailNotifications());
		
		return workerDTO;
	}
	
	/**
	 * Método para que el sistema establezca un usuario automáticamente
	 * Comprobamos que no sea fin de semana y buscamos el horario por el dia de la semana
	 * Filtramos los horarios disponibles entre el horario que haya seleccionado el usuario
	 * Mientras el index (variable temporal) sea menor el tamaño de la lista de trabajadores buscamos si un trabajador no tiene cita a esa hora
	 * Devolvemos el primer trabajador encontrado
	 * Establecemos un endTime predeterminado de 30 minutos
	 * @param day
	 * @param startTime
	 * @return
	 */
	public User setAutomaticallyWorkerToCite(Date day, Time startTime) {
		
		String dayOfWeek = day.toLocalDate().getDayOfWeek().toString();
		
		if(dayOfWeek.equals("SUNDAY") || dayOfWeek.equals("SATURDAY")) {
			throw new ValueNotValidException("We are closed on weekends");
		}
		
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
	                worker.getId(), day, startTime, calculateEndTime(startTime, 30));
			
			if(cites.isEmpty() && citesAux.isEmpty()) {
				return worker;
			}
			
			index++;
		}
		
		throw new ElementNotFoundException("No workers availables");
		
	}
	
	/**
	 * Método para actualziar una cita
	 * Comprobamos que sea un administrador o el id del usuario sea el mismo que el de la cita para poder modificarla
	 * Comprobamos que los valores sean válidos y los actualizamos
	 * @param idCite
	 * @param newCiteDTO
	 * @return
	 */
	public BookCiteDTO updateCite(String idCite,BookCiteDTO newCiteDTO,
			String idWorker) {
		
		Integer id = convertStringToInteger(idCite);
		
		Cites cite = citeRepository.findById(id).orElseThrow(() -> 
				new ElementNotFoundException("Cite not found with id: " + id));
		
		User userLoggued = loginApp();
		
		if((userLoggued.getRole().equals("admin")) || userLoggued.getId().equals(cite.getUser().getId())) {
			
			if(idWorker != null && !idWorker.isEmpty()) {
				
				Integer idWorkerInt = convertStringToInteger(idWorker);
				
				User worker = userRepository.findById(idWorkerInt).orElseThrow(() -> new ElementNotFoundException("Worker not found with this id"));
				
				if(!worker.getRole().equals("stylist")) {
					throw new ValueNotValidException("This user is not a worker");
				}
				
				checkCiteAvailability(cite, worker);
				cite.setWorker(worker);
				
			}
			
			if((idWorker == null || idWorker.isEmpty()) && (!newCiteDTO.getDay().equals(cite.getDay()) || !newCiteDTO.getStartTime().equals(cite.getStartTime()))) {
				User worker = setAutomaticallyWorkerToCite(newCiteDTO.getDay(), newCiteDTO.getStartTime());				
				checkCiteAvailability(cite, worker);
				cite.setWorker(worker);
			}
			
			if(newCiteDTO.getDay() != null || newCiteDTO.getStartTime() != null) {
				
				EmployeeSchedule schedule = findEmployeeSchedule(newCiteDTO.getDay(),cite.getWorker());
				checkTime(cite.getStartTime(),schedule);
				
			}
			
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
			
			citeRepository.save(cite);
		}else {
			throw new RoleNotValidException("Only admins can update cites of other users");
		}
		
		
		return newCiteDTO;
		
	}
	
	/**
	 * Método para eliminar una cita
	 * Enviamos un mensaje a todos aquellos que sean administradores o estilistas
	 * @param idCite
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public BookCiteDTO removeCite(String idCite) throws UnsupportedEncodingException, MessagingException {
		Integer id = convertStringToInteger(idCite);
		
		User userLoggued = loginApp();
		
		Cites cite = citeRepository.findById(id).orElseThrow(() -> 
					new ElementNotFoundException("Cite not found with id: " + id));
		
		BookCiteDTO citeDTO = new BookCiteDTO();
		
		if(userLoggued.getRole().equals("admin") || userLoggued.getId().equals(cite.getUser().getId())) {
			
			if(cite.getDay().toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
			    throw new ValueNotValidException("Sorry, you cannot remove a cite that is today or in the past.");
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
	
	/**
	 * Método para obtener un horario de trabajo
	 * @param date
	 * @param worker
	 * @return
	 */
	public EmployeeSchedule findEmployeeSchedule(Date date,User worker) {
		
		String day = LocalDate.parse(date.toString()).getDayOfWeek().toString();
		
		List<EmployeeSchedule> schedules = employeeScheduleRepository.findByWorkerAndDay(worker, day);
		
		if(!schedules.isEmpty()) {
			return schedules.get(0);
		}else {
			throw new ElementNotFoundException("This worker not work this day");
		}
	}
	
	/**
	 * Método para obtener la validez de una cita
	 * Comprobamos si la cita puede establecerse para ese trabajador sin que haya cita a esa hora o entre horas
	 * @param cite
	 * @param worker
	 */
	public void checkCiteAvailability(Cites cite, User worker) {
		
		List<Cites> cites = citeRepository.findByDayAndWorkerAndStartTime(cite.getDay(), worker, cite.getStartTime());
		
		if(!cites.isEmpty()) {
			throw new ValueNotValidException("This worker have a cite at this time");
		}
		
        List<Cites> citesAux = citeRepository.findCitesBetweenHours(
                worker.getId(), cite.getDay(),cite.getStartTime(), cite.getEndTime());
		
        if(!citesAux.isEmpty()) {
        	throw new ValueNotValidException("At that time the worker will be busy with another appointment, please choose from this time: " + citesAux.get(0).getEndTime());
        }
        
	}
	
	/**
	 * Método para enviar un mensaje de alerta de cita cancelada al usuario
	 * @param user
	 * @param cite
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
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
	
	/**
	 * Método para comprobar si la fecha y el horario es válido dentro del horario de trabajo
	 * @param date
	 * @param startTime
	 */
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
	
	/**
	 * Método para obtener trabajadores por una fecha
	 * Obtenemos los trabajadores del horario y los devolvemos
	 * @param idCite
	 * @return
	 */
	public List<GetUserDTO> getWorkerByDate(String idCite) {
		Integer id = convertStringToInteger(idCite);
		Cites cite = citeRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Cite not found"));
		String day = LocalDate.parse(cite.getDay().toString()).getDayOfWeek().toString();
		List<EmployeeSchedule>schedules = employeeScheduleRepository.findByDay(day);
		List<User>users = new ArrayList<User>();
		schedules.forEach(schedule -> {
           User user = schedule.getWorker();
           users.add(user);
        });
		
		if (!users.isEmpty()) {
		    return ConvertToDTO.getUsersDTO(users);
		} else {
		    throw new ValueNotValidException("No workers found");
		}
		
	}
	
	/**
	 * Buscamos citas por su fecha y su horario
	 * Si la cita es sabado o domingo lanzamos excepcion
	 * Si hay algun trabajador disponible devolvemos la lista de citas vacias
	 * @param date
	 * @param time
	 * @param endTime
	 * @return
	 */
	public List<BookCiteDTO> findByDateAndTime(LocalDate date, String time, String endTime){
		
		String day = LocalDate.parse(date.toString()).getDayOfWeek().toString();
		
		if(day.equals("SUNDAY") || day.equals("SATURDAY")) {
			throw new ValueNotValidException("We are closed on weekends");
		}
		
		Time newTime = convertToTime(time);
		Time newEndTime = convertToTime(endTime);
		List<BookCiteDTO> citesDTO;
		List<Cites> citesBetween = new ArrayList<Cites>();
		List<GetUserDTO> workersAvailables = new ArrayList<GetUserDTO>();
		
		List<Cites>cites = citeRepository.findByDayAndStartTime(Date.valueOf(date), newTime);
		List<EmployeeSchedule>schedules = employeeScheduleRepository.findByDay(day);
		
		if(cites.isEmpty()) {
			//Por cada horario comprobamos si hay alguna cita ya disponible
	        schedules.forEach(schedule -> {
	            List<Cites> tempCitesBetween = citeRepository.findCitesBetweenHours(
	                schedule.getWorker().getId(), Date.valueOf(date), newTime, newEndTime
	            );
	            citesBetween.addAll(tempCitesBetween);
	        });
	        //Si no esta vacía añadimos a la lista de trabajadores si hay alguno
			if(!citesBetween.isEmpty()) {
				citesBetween.stream().forEach(cite -> {
					workersAvailables.addAll(checkWorkersAvailables(cite,day));				
				});
			}
			citesDTO =  ConvertToDTO.convertCites(citesBetween);
		}else {
			cites.stream().forEach(cite -> {
				workersAvailables.addAll(checkWorkersAvailables(cite,day));				
			});
			citesDTO =  ConvertToDTO.convertCites(cites);			
		}
		
		if(!workersAvailables.isEmpty()) {
			citesDTO.clear();
		}
		
		return citesDTO;
	}
	
	/**
	 * Método para obtener los trabajadores disponibles por la cita y el dia
	 * Buscamos el horario del dia concreto y recorremos la lista
	 * Creamos un trabajador por cada instancia de la lista
	 * Comprobamos si hay disponibilidad de trabajadores
	 * @param cite
	 * @param day
	 * @return
	 */
	public List<GetUserDTO> checkWorkersAvailables(Cites cite, String day) {
	    List<EmployeeSchedule> schedules = employeeScheduleRepository.findByDay(day);
	    List<User> availableWorkers = new ArrayList<User>();

	    for (EmployeeSchedule schedule : schedules) {
	    	User worker = schedule.getWorker();
	    	List<Cites>tempCites = new ArrayList<Cites>();
	    	
	    	tempCites = citeRepository.findByDayAndWorkerAndStartTime(cite.getDay(), worker,  cite.getStartTime());
	    	
	    	List<Cites> tempCitesBetween = citeRepository.findCitesBetweenHours(
                schedule.getWorker().getId(), cite.getDay(), cite.getStartTime(), cite.getEndTime()
            );
	    	
	    	 //Si el trabajador no tiene citas lo añadimos a la lista de trabajadores disponibles
	    	if(tempCites.isEmpty() && tempCitesBetween.isEmpty()) {
	    		availableWorkers.add(worker);
	    	}
	    }
	    
	    if(availableWorkers.isEmpty()) {
	    	throw new ValueNotValidException("Not available workers");
	    }
	    
	    List<GetUserDTO>usersConverter =  ConvertToDTO.getUsersDTO(availableWorkers);
	    
	    return usersConverter;
	}
	
	public List<GetUserDTO> getWorkersAvailablesById(String idString, String dateFilter, String timeFilter) {
		Integer id = convertStringToInteger(idString);
		String day = "";
		Cites cite = citeRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Cite not found with this id"));
		
		if(dateFilter != null && !dateFilter.equals("undefined")) {
			day = LocalDate.parse(dateFilter).getDayOfWeek().toString();			
		}else {
			day = LocalDate.parse(cite.getDay().toString()).getDayOfWeek().toString();	
		}
	    List<EmployeeSchedule> schedules = employeeScheduleRepository.findByDay(day);
	    List<EmployeeSchedule> availableSchedules = new ArrayList<EmployeeSchedule>();
	    List<User> availableWorkers = new ArrayList<User>();
	    
	    if(timeFilter != null  && !timeFilter.equals("undefined")) {
	    	Time timeFilterConvert = Time.valueOf(timeFilter);
	    	if(timeFilterConvert.after(Time.valueOf("09:00:00")) && timeFilterConvert.before(Time.valueOf("13:00:00"))) {
	    		availableSchedules = schedules.stream().filter(schedule -> schedule.getTurn().equals("Morning")).collect(Collectors.toList());
	    	}else if(timeFilterConvert.after(Time.valueOf("13:01:00")) && timeFilterConvert.before(Time.valueOf("21:00:00"))) {
	    		availableSchedules = schedules.stream().filter(schedule -> schedule.getTurn().equals("Afternoon")).collect(Collectors.toList());
	    	}
	    	
	    }else {
	    	
	    	if(cite.getStartTime().after(Time.valueOf("09:00:00")) && cite.getStartTime().before(Time.valueOf("13:00:00"))) {
	    		availableSchedules = schedules.stream().filter(schedule -> schedule.getTurn().equals("Morning")).collect(Collectors.toList());
	    	}else if(cite.getStartTime().after(Time.valueOf("13:01:00")) && cite.getStartTime().before(Time.valueOf("21:00:00"))) {
	    		availableSchedules = schedules.stream().filter(schedule -> schedule.getTurn().equals("Afternoon")).collect(Collectors.toList());
	    	}
	    	
	    }

	    for (EmployeeSchedule schedule : availableSchedules) {
	    	User worker = schedule.getWorker();
	    	List<Cites>tempCites = new ArrayList<Cites>();
	    	
	    	tempCites = citeRepository.findByDayAndWorkerAndStartTime(cite.getDay(), worker,  cite.getStartTime());
	    	
	    	List<Cites> tempCitesBetween = citeRepository.findCitesBetweenHours(
                schedule.getWorker().getId(), cite.getDay(), cite.getStartTime(), cite.getEndTime()
            );
	    	
	    	 //Si el trabajador no tiene citas lo añadimos a la lista de trabajadores disponibles
	    	if(tempCites.isEmpty() && tempCitesBetween.isEmpty()) {
	    		availableWorkers.add(worker);
	    	}
	    }
	    
	    if(availableWorkers.isEmpty()) {
	    	throw new ValueNotValidException("Not available workers");
	    }
	    
	    List<GetUserDTO>usersConverter =  ConvertToDTO.getUsersDTO(availableWorkers);
	    
	    return usersConverter;
	}
	
	/**
	 * Método para comprobar el horario y el turno
	 * @param startTime
	 * @param schedule
	 */
	public void checkTime(Time startTime, EmployeeSchedule schedule) {
		
		if((startTime.after(Time.valueOf("09:00:00")) && startTime.before(Time.valueOf("13:00:00"))) && (schedule.getTurn().equals("Afternoon"))) {
			
			throw new ValueNotValidException("We are sorry, this employee's schedule this day is only in the afternoon");
		
		}else if((startTime.after(Time.valueOf("13:01:00")) && startTime.before(Time.valueOf("21:00:00"))) && schedule.getTurn().equals("Morning")) {
			
			throw new ValueNotValidException("We are sorry, this employee's schedule this day is only in the morning");			
		
		}
		
	}
	
	/**
	 * Método para obtener el usuario logueado
	 * @return
	 */
	public User loginApp() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<User> users = userRepository.findByEmail(auth.getName());
		
		if(users.size() == 0) {
			throw new ElementNotFoundException("You must be loggued in");
		}
		
		return users.get(0);
	}
	
	/**
	 * Método para convertir de String a Integer
	 * @param idString
	 * @return
	 */
	public Integer convertStringToInteger(String idString) {
		Integer id;
		try {
			id = Integer.parseInt(idString);
		}catch(NumberFormatException e) {
			throw new ValueNotValidException("Id must be numeric");
		}
		
		return id;
	}
	
	/**
	 * Método para convertir el tiempo a formato HH:mm:ss
	 * @param time
	 * @return
	 */
	public Time convertToTime(String time) {
		Time newTime;
		
		try {
			newTime = Time.valueOf(time);	
		}catch(IllegalArgumentException e) {
			throw new ValueNotValidException("Time format must be HH:mm:ss");
		}
		return newTime;
	}

	/**
	 * Método para calcular el tiempo final
	 * @param startTime
	 * @param durationInMinutes
	 * @return
	 */
    public Time calculateEndTime(Time startTime, int durationInMinutes) {
        long durationInMillis = durationInMinutes * 60 * 1000;
        return new Time(startTime.getTime() + durationInMillis);
    }
	
}
