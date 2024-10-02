package com.jacaranda.glamAndGlitter.services;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jacaranda.glamAndGlitter.exceptions.ElementNotFoundException;
import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.ConvertToDTO;
import com.jacaranda.glamAndGlitter.model.User;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.RegisterUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.UserChangePasswordDTO;
import com.jacaranda.glamAndGlitter.respository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JavaMailSender mailSender;
	
	
	//Mapa para almacenar de forma temporal el código enviado al correo electrónico
	private Map<String,String>codesStorage = new HashMap<String,String>();
	
	/**
	 * Método para obtener todos los usuarios
	 * @return
	 */
	public List<GetUserDTO> getUsers(){
		return ConvertToDTO.getUsersDTO(userRepository.findAll());
	}
	
	/**
	 * Método para encontrar usuarios por un email
	 * @param email
	 * @return
	 */
	public List<GetUserDTO>findByEmail(String email){
		return ConvertToDTO.getUsersDTO(userRepository.findByEmail(email));
	}
	
	/**
	 * Método para registrarse en la aplicación
	 * Validamos todos los campos para que no existan nulos
	 * Encriptamos la contraseña
	 * Creamos y guardamos el usuario, enviando un correo electrónico a su email
	 * @param registerUser
	 * @return
	 * @throws ValueNotValidException
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public RegisterUserDTO addUser(RegisterUserDTO registerUser) throws ValueNotValidException, UnsupportedEncodingException, MessagingException {
		
		if(registerUser.getName() == null || registerUser.getName().isBlank()) {
			throw new ValueNotValidException("Name can´t be null");
		}
		
		if(registerUser.getEmail() == null || registerUser.getEmail().isBlank()) {
			throw new ValueNotValidException("Email can´t be null");
		}
		
		if(!registerUser.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
			throw new ValueNotValidException("Email format not valid");
		}
		
		if(registerUser.getPhone() == null || registerUser.getPhone().isBlank()) {
			throw new ValueNotValidException("Phone can´t be null");
		}
		
		if(!registerUser.getPhone().matches("^\\d{9}$")) {
			throw new ValueNotValidException("Phone format not valid");
		}
		
		if(registerUser.getPassword() == null || registerUser.getPassword().isBlank()) {
			throw new ValueNotValidException("Password can´t be null");
		}
		
		List<User>users = userRepository.findByEmail(registerUser.getEmail());
		
		if(!users.isEmpty()) {
			throw new ValueNotValidException("User already exist!");
		}
		String encodedPassword = encryptPassword(registerUser.getPassword());
		User user = new User(registerUser.getName(),registerUser.getEmail(),registerUser.getPhone(),
				encodedPassword,"user",false);
		userRepository.save(user);
		successRegistration(user);
	
		return registerUser;
		
	}
	
	/**
	 * Método para enviar el código al correo del usuario
	 * Comprobamos que exista el correo
	 * Vacíamos el mapa por si le da 2 veces o cambia el correo
	 * Generamos el código y enviamos el correo
	 * @param email
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public void sendCodeToUser(String email) throws UnsupportedEncodingException, MessagingException {
		List<User> users = userRepository.findByEmail(email);
		if(users.isEmpty()) {
			throw new ElementNotFoundException("This user not exist");
		}
		
		codesStorage.clear();
		String code = UUID.randomUUID().toString();
		
		forgotPassword(users.get(0), code);
		codesStorage.put(email, code);
		
	}
	
	/**
	 * Método para verificar si el código introducido por el usuario es el mismo que el enviado
	 * Si es el mismo devolvemos true y vacíamos el mapa, sino lanzamos exception
	 * @param email
	 * @param codeToCheck
	 * @return
	 */
	public Boolean verifyCode(String email,String codeToCheck) {
		String code = codesStorage.get(email);
		
		if(!code.equals(codeToCheck)) {
			throw new ValueNotValidException("Sorry this code is not the same");
		}
		
		codesStorage.clear();
		return true;
	}
	
	/**
	 * Método para cambiar la contraseña del usuario
	 * Buscamos de nuevo el usuario por su email (a modo de seguridad)
	 * Encriptamos la contraseña se la asignamos al usuario y guardamos los cambios
	 * @param email
	 * @param newPassword
	 * @return
	 */
	public UserChangePasswordDTO changePassword(String email, String newPassword) {
		List<User>users = userRepository.findByEmail(email);
		
		if(users.isEmpty()) {
			throw new ElementNotFoundException("This user not exist");
		}
		String encodedPassword = encryptPassword(newPassword);
		users.get(0).setPassword(encodedPassword);
		userRepository.save(users.get(0));
		
		UserChangePasswordDTO user = new UserChangePasswordDTO(users.get(0).getEmail(),users.get(0).getPassword());
		return user;
	}
	
	/**
	 * Método para enviar el correo con el código al usuario
	 * @param user
	 * @param code
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public void forgotPassword(User user, String code) throws UnsupportedEncodingException, MessagingException {

	    String toAddress = user.getEmail();

	    String fromAddress = "a.fraramgar@gmail.com";
	    String senderName = "Glam&Glitter";

	    String subject = "Forgot Password";
	    String content = "Dear [[user]],<br><br>"
	    	    + "Don't share this code with any people: " + code;



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
	 * Método para avisar a un usuario por correo de su registro en la aplicación
	 * @param user
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	private void successRegistration(User user)
	        throws MessagingException, UnsupportedEncodingException {

	    String toAddress = user.getEmail();

	    String fromAddress = "a.fraramgar@gmail.com";
	    String senderName = "Glam&Glitter";

	    String subject = "Welcome to Glam&Glitter";
	    String content = "Dear [[user]],<br><br>"
	    	    + "Welcome to Glam&Glitter! We are excited to have you as part of our beauty community.<br><br>"
	    	    + "At Glam&Glitter, we are passionate about helping you look and feel your best. Whether you're here for a makeover or a simple touch-up, we are thrilled to accompany you on your beauty journey.<br><br>"
	    	    + "Thank you for choosing us! If you have any questions or need to schedule your next appointment, don’t hesitate to reach out.<br><br>"
	    	    + "Looking forward to seeing you soon!<br><br>"
	    	    + "Warm regards,<br>"
	    	    + "The Glam&Glitter Team";



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
	 * Método para encriptar la contraseña con BCrypt
	 * @param password
	 * @return encodedPassword
	 */
	public static String encryptPassword(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(password);
		return encodedPassword;
	}

	/**
	 * Método para loguearnos en la aplicación por nuestro correo electrónico
	 * Buscamos el usuario por email, si no existe, lanzamos exception
	 */
	@Override
	public UserDetails loadUserByUsername(String email)throws UsernameNotFoundException{
		List<User>users = userRepository.findByEmail(email);
		
		if(!users.isEmpty() && users.size() == 1) 
			return users.get(0);
		else 
			throw new UsernameNotFoundException("User not found with email: " + email );
		
	}
	
}
