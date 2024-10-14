package com.jacaranda.glamAndGlitter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users")
public class User implements UserDetails{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3178405153549864651L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private String password;
	
	private String role;
	
	private Boolean emailNotifications;
	
	private Boolean smsNotifications;
	
	private Boolean calendarNotifications;
	
	@OneToMany(mappedBy = "user")
	private List<Cites>cites = new ArrayList<Cites>();
	
	@OneToMany(mappedBy = "worker")
	private List<Cites>workerCites = new ArrayList<Cites>();

	@OneToMany(mappedBy = "worker")
	private List<EmployeeSchedule>employeeSchedules = new ArrayList<EmployeeSchedule>();
	
	@OneToMany(mappedBy = "user")
	private List<Rating>ratings = new ArrayList<Rating>();
	
	public User() {
		super();
	}

	public User(String name, String email, String phone, String password, String role,
			Boolean emailNotifications, Boolean smsNotifications, Boolean calendarNotifications) {
		super();
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.role = role;
		this.emailNotifications = emailNotifications;
		this.smsNotifications = smsNotifications;
		this.calendarNotifications = calendarNotifications;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getEmailNotifications() {
		return emailNotifications;
	}

	public void setEmailNotifications(Boolean emailNotifications) {
		this.emailNotifications = emailNotifications;
	}

	public Boolean getSmsNotifications() {
		return smsNotifications;
	}

	public void setSmsNotifications(Boolean smsNotifications) {
		this.smsNotifications = smsNotifications;
	}

	public Boolean getCalendarNotifications() {
		return calendarNotifications;
	}

	public void setCalendarNotifications(Boolean calendarNotifications) {
		this.calendarNotifications = calendarNotifications;
	}

	public List<Cites> getCites() {
		return cites;
	}

	public void setCites(List<Cites> cites) {
		this.cites = cites;
	}

	public List<EmployeeSchedule> getEmployeeSchedules() {
		return employeeSchedules;
	}

	public void setEmployeeSchedules(List<EmployeeSchedule> employeeSchedules) {
		this.employeeSchedules = employeeSchedules;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public List<Cites> getWorkerCites() {
		return workerCites;
	}

	public void setWorkerCites(List<Cites> workerCites) {
		this.workerCites = workerCites;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(email, other.email);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}




}
