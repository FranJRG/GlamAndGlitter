package com.jacaranda.glamAndGlitter.utility;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.jacaranda.glamAndGlitter.exceptions.ExceptionTokenNotValid;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class TokenUtils {
	
	// Declaramos el token que usaremos para el cifrado y la decodificación.
	// Lo suyo es que este token sea generado de forma aleatoria al empezar la aplicación
	// Podemos poner el valor que queramos

	private final static String ACCESS_TOKEN_SECRET = "sksksskskskskiwoapqowlakskiwoaskskskskiwoapqowlapqowla";
	
	// Definimos el tiempo de validez del token en segundos. Mientras estemos en fase de desarrollo
	// podemos definir tiempos largos para que no nos den problema la caducidad del token.
	// Cuando nuestra aplicación esté en explotación deberemos ajustar este tiempo.
	// Estos dos parámetro también se pueden definir el el fichero aplicattion.properties
	
	private final static Long ACCESS_TOKEN_VALIDATY_SECONDS = (long)30*24*60*60; //30 días
    

	/**
	 * Este método va a generar un token. En el token incluiremos el username, email y role. 
	 * Esto es un ejemplo, en el token podemos almacenar la información que necesite nuestra aplicación, por ejemplo, nombre y apellido 
	 * o lo que consideremos necesario.
	 * @param username: guardaremos el  username dentro del token
	 * @param email: guardaremos el email dentro del token
	 * @param role: guardaremos el role dentro del token. Si puediera tener varios roles, también se podría guardar.
	 * @return: el token que debe empezar por Bearer y un espacio.
	 */
    public static String generateToken(String email,String role, Integer userId, String name)  {
    	
    	// Establecemos la fecha de expiración del token en milisegundos
    	Date expirationDate = new Date(System.currentTimeMillis()+ ACCESS_TOKEN_VALIDATY_SECONDS*1000);
    	
    	// Creamos un mapa para guardar toda la información que queramos guardar en el token
    	// El username no es necesario guardarlo porque ya va en el token, en el subject.
    	Map<String,Object> extra = new HashMap<>();
    	extra.put("userId", userId);
    	extra.put("name", name);
    	extra.put("role", role);
    	
    	// Construimos el token con el nombre del usuario, la fecha de expiración, la información
    	// que queremos guardar y el token que vamos a user para encriptarlo.
        String token =  Jwts.builder()
        		.subject(email)
        		.issuedAt(expirationDate)
        		.claims(extra)
        		.signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
        		.compact();
        
        return "Bearer " + token;
    }
    
    /**
     * Obtiene el payLoad de un token
     * @param token
     * @return
     * @throws JwtException
     * @throws IllegalArgumentException
     * @throws NoSuchAlgorithmException
     */
    private static Claims getAllClaimsFromToken(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException {
    	
    	Claims claims = 
    			Jwts.parser().verifyWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes())) 
    					.build()
    					.parseSignedClaims(token)
    					.getPayload();
        return claims;
    }
    
      
    /**
     * Método para ver el usuario y el role que "contiene" el token. Lo primero
     * que haremos es decodificar el claims. Si lanza una exception es que no es 
     * válido usando nuestra token secreto. Si es válido vemos la fecha de expiracion
     * Sacamos el nombre y los roles y lo devolvemos.
     * @param token
     * @return
     * @throws ExceptionTokenNotValid 
     
     */
    public static UsernamePasswordAuthenticationToken getAuthentication(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException, ExceptionTokenNotValid   {
    	Claims claims;
    	
    	if (!token.startsWith("Bearer ")) {
			throw new ExceptionTokenNotValid("Formato token no válido");
    	}
    	token = token.substring(7);
    	try {
    		//Claims == PayLoad
        	claims = getAllClaimsFromToken(token);
    	} catch (IllegalArgumentException e) {
    		throw new ExceptionTokenNotValid("Imposible encontra un JWT Token");
        } catch (ExpiredJwtException e) {
        	throw new ExceptionTokenNotValid("Token expirado");
        } catch (NoSuchAlgorithmException e) {
			throw new ExceptionTokenNotValid("Algoritmo no válido");
        }
        
    
       	String username = claims.getSubject();
        	
        String role = (String) claims.get("role");
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    	authorities.add(new SimpleGrantedAuthority(role));
        	
        return new UsernamePasswordAuthenticationToken(username,null,authorities);
		
    	
    }
    
    public static String getSubject(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException {
    	String tokenValid = token.replaceAll("Bearer ", "");
    	Claims claims = getAllClaimsFromToken(tokenValid);
    	return claims.getSubject();
    }
    
    public static String getRole(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException {
    	String tokenValid = token.replaceAll("Bearer ", "");
    	Claims claims = getAllClaimsFromToken(tokenValid);
    	return (String) claims.get("role");
    }
    
}
