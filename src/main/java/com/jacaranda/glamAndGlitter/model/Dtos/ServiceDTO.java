package com.jacaranda.glamAndGlitter.model.Dtos;

import java.util.Objects;

public class ServiceDTO {
	
	private Integer id;
	
	private String name;
	
	private String description;
	
	private Double price;
	
	private Boolean active;
	
	private String categoryName;
	
	private String duration;
	
	private String imageUrl;

	public ServiceDTO() {
		super();
	}

	public ServiceDTO(Integer id, String name,String description, Double price, Boolean active,
			String categoryName, String imageUrl, String duration) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.active = active;
		this.categoryName = categoryName;
		this.imageUrl = imageUrl;
		this.duration = duration;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getCategory() {
		return categoryName;
	}

	public void setCategory(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	@Override
	public int hashCode() {
		return Objects.hash(active, categoryName, description, id, name, price);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceDTO other = (ServiceDTO) obj;
		return Objects.equals(active, other.active) && Objects.equals(categoryName, other.categoryName)
				&& Objects.equals(description, other.description)
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(price, other.price);
	}
	
}
