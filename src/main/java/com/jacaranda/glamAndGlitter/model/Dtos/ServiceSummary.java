package com.jacaranda.glamAndGlitter.model.Dtos;

import java.util.Objects;

public class ServiceSummary {

	private Integer serviceId;
	private String serviceName;
	private Long reservationCount;
	
	public ServiceSummary() {
		super();
	}
	
	public ServiceSummary(Integer serviceId, String serviceName, Long reservationCount, Integer averageRating,
			String comment) {
		super();
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.reservationCount = reservationCount;
	}
	
	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public Long getReservationCount() {
		return reservationCount;
	}
	public void setReservationCount(Long reservationCount) {
		this.reservationCount = reservationCount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(reservationCount, serviceId, serviceName);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceSummary other = (ServiceSummary) obj;
		return Objects.equals(reservationCount, other.reservationCount)
				&& Objects.equals(serviceId, other.serviceId) && Objects.equals(serviceName, other.serviceName);
	}

}
