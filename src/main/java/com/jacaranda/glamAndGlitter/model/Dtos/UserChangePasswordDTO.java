package com.jacaranda.glamAndGlitter.model.Dtos;

import java.util.Objects;

public class UserChangePasswordDTO {

	private String email;
	
	private String password;

	public UserChangePasswordDTO() {
		super();
	}

	public UserChangePasswordDTO(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, password);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserChangePasswordDTO other = (UserChangePasswordDTO) obj;
		return Objects.equals(email, other.email) && Objects.equals(password, other.password);
	}
	
	
	
}
