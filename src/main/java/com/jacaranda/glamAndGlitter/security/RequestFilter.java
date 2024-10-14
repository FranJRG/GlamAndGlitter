package com.jacaranda.glamAndGlitter.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jacaranda.glamAndGlitter.exceptions.RoleNotValidException;
import com.jacaranda.glamAndGlitter.model.User;
import com.jacaranda.glamAndGlitter.respository.UserRepository;
import com.jacaranda.glamAndGlitter.utility.TokenUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestFilter extends OncePerRequestFilter{
	
	@Autowired
    private UserRepository userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
			FilterChain chain) throws IOException, ServletException  {
		final String requestTokenHeader = request.getHeader("Authorization");
		if (requestTokenHeader != null) {
		    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;
		    try {
		    	
		    	String email = TokenUtils.getSubject(requestTokenHeader);
		    	String role = TokenUtils.getRole(requestTokenHeader);
		    	
		    	List<User>users = userService.findByEmail(email);
		    	
		    	if(users.isEmpty() || !users.get(0).getRole().equals(role)) {
		    		throw new RoleNotValidException("This token is not valid!");
		    	}
		    	
		    	
			    usernamePasswordAuthenticationToken = TokenUtils.getAuthentication(requestTokenHeader);
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			    // After setting the Authentication in the context, we specify
			    // that the current user is authenticated. So it passes the
			    // Spring Security Configurations successfully.
			    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			    
	           } catch (Exception e) {
	                logger.error(e.getMessage());
	                throw new RoleNotValidException("This token is not valid!");
	           }
		}
			   
		chain.doFilter(request, response);

	        
	 	
	}
}
