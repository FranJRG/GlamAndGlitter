package com.jacaranda.glamAndGlitter.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Rating")
public class Rating {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer punctuation;
	
	private String message;
	
	@ManyToOne
	@JoinColumn(name = "idUser")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "idCite")
	private Cites cites;

	public Rating() {
		super();
	}

	public Rating(Integer punctuation, String message, User user, Cites cites) {
		super();
		this.punctuation = punctuation;
		this.message = message;
		this.user = user;
		this.cites = cites;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPunctuation() {
		return punctuation;
	}

	public void setPunctuation(Integer punctuation) {
		this.punctuation = punctuation;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Cites getCites() {
		return cites;
	}

	public void setCites(Cites cites) {
		this.cites = cites;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rating other = (Rating) obj;
		return Objects.equals(id, other.id);
	}
	
	
	
}
