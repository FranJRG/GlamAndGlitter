package com.jacaranda.glamAndGlitter.services;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jacaranda.glamAndGlitter.exceptions.ValueNotValidException;
import com.jacaranda.glamAndGlitter.model.ConvertToDTO;
import com.jacaranda.glamAndGlitter.model.User;
import com.jacaranda.glamAndGlitter.model.Dtos.GetUserDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.RegisterUserDTO;
import com.jacaranda.glamAndGlitter.respository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JavaMailSender mailSender;
	
	public List<GetUserDTO> getUsers(){
		return ConvertToDTO.getUsersDTO(userRepository.findAll());
	}
	
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
	
	public static String encryptPassword(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(password);
		return encodedPassword;
	}

	@Override
	public UserDetails loadUserByUsername(String email)throws UsernameNotFoundException{
		List<User>users = userRepository.findByEmail(email);
		
		if(!users.isEmpty() && users.size() == 1) 
			return users.get(0);
		else 
			throw new UsernameNotFoundException("User not found with email: " + email );
		
	}
	
}
