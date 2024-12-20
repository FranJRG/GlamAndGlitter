package com.jacaranda.glamAndGlitter.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint{
	
	/**
	 * Si algún acceso no está autorizado este método lo captura y le devuelve
	 * lo que consideremos más oportuno. En este caso una respuesta de no autorizada
	 * Para que este método reciba las peticiones no aceptada hay que inyectar esta clase
	 * en la clase de seguridad y especificar que se usará para denegar las peticiones.
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.core.AuthenticationException authException)
			throws IOException, ServletException, StreamWriteException, DatabindException, java.io.IOException {
		
			response.addHeader("Content-Type", "application/json");
		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		    final Map<String, Object> body = new HashMap<>();
		    body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
		    body.put("error", "Unauthorized");
		    body.put("message", "This action required to be admin or be loggued in");
		    body.put("path", request.getServletPath());

		    final ObjectMapper mapper = new ObjectMapper();
		    mapper.writeValue(response.getOutputStream(), body);
	}

}
