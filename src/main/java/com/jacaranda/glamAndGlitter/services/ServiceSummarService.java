package com.jacaranda.glamAndGlitter.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacaranda.glamAndGlitter.model.Cites;
import com.jacaranda.glamAndGlitter.model.ConvertToDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.AverageMedia;
import com.jacaranda.glamAndGlitter.model.Dtos.RatingDTO;
import com.jacaranda.glamAndGlitter.model.Dtos.ServiceSummary;
import com.jacaranda.glamAndGlitter.model.Dtos.SummaryCites;
import com.jacaranda.glamAndGlitter.respository.CiteRepository;
import com.jacaranda.glamAndGlitter.respository.RatingRepository;

@Service
public class ServiceSummarService {

	@Autowired
	private CiteRepository citeRepository;
	
	@Autowired
	private RatingRepository ratingRepository;
	
	/**
	 * Método para obtener los servicios y sus valoraciones 
	 * Convertimos la lista de objeto que retornamos en un ServiceSummary
	 * Ordenamos por aquellas que tengan mayor porcentaje de peticiones
	 * La lista máxima que devolveremos será de 4
	 * @return
	 */
	public List<ServiceSummary> getTopServicesWithRatings() {
	    LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
	    LocalDate endOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(31);
	    Date startDate = Date.valueOf(startOfMonth);
	    Date endDate = Date.valueOf(endOfMonth);
        List<Object[]> results = citeRepository.findTopServicesByReservationCount(startDate,endDate);

        List<ServiceSummary> reportList = new ArrayList<ServiceSummary>();
        for (Object[] result : results) {
            ServiceSummary dto = new ServiceSummary();
            dto.setServiceId((Integer) result[0]);
            dto.setServiceName((String) result[1]);
            dto.setReservationCount((Long) result[2]);
            reportList.add(dto);
        }
        
        reportList.sort((a,b) -> b.getReservationCount() .compareTo(a.getReservationCount()));

        if(reportList.size() > 4) {
        	reportList = reportList.subList(0,4);
        }
        
        return reportList;
    }
	
	/**
	 * Obtenos las citas del último mes, con su valoraciones y el usuario que las escribió
	 * @return
	 */
	public List<SummaryCites>getCitesInLastMonth(){
	    LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
	    LocalDate endOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(31);
	    Date startDate = Date.valueOf(startOfMonth);
	    Date endDate = Date.valueOf(endOfMonth);
		List<Cites>cites = citeRepository.findCitesInLastMonth(startDate,endDate);
		
		List<SummaryCites>summaryCites = new ArrayList<SummaryCites>();
		
		cites.stream().forEach(cite -> {
			if(cite.getRatings().size() > 0) {
				cite.getRatings().forEach(rating -> {
					SummaryCites summaryCite = new SummaryCites(cite.getUser().getUsername(),cite.getDay(),cite.getService().getName(),
							rating.getPunctuation(),rating.getMessage(),cite.getWorker().getName());
					summaryCites.add(summaryCite);
				});				
			}else {
				SummaryCites summaryCite = new SummaryCites(cite.getUser().getUsername(),cite.getDay(),cite.getService().getName(),
						0,null,cite.getWorker().getName());
				summaryCites.add(summaryCite);
			}
		});
		
		return summaryCites;
	}
	
	/**
	 * Método para obtener las valoracion media del último mes 
	 *(Si estamos en noviembre el último será desde el 1 de oct hasta el 31 de oct)
	 * Devolvemos la valoración de ese último mes
	 * @return
	 */
	public AverageMedia getRatingMediaLastMonth() {
	    LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
	    LocalDate endOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(31);
	    Date startDate = Date.valueOf(startOfMonth);
	    Date endDate = Date.valueOf(endOfMonth);
	    Double averageMedia = ratingRepository.getAverageRatingFromCitasInLastMonth(startDate, endDate);
	    return new AverageMedia(averageMedia);
	}
	
	/**
	 * Método para devolver la valoración media en total de la compañia
	 * @return
	 */
	public AverageMedia getTotalMedia() {
		Double averageMedia = ratingRepository.getAverageFromCompany();
		AverageMedia media = new AverageMedia(averageMedia);
		return media;
	}
	
	/**
	 * Método para devolver las peores calificaciones del último mes
	 * @return
	 */
	public List<RatingDTO> getRatingLess() {
	    LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
	    LocalDate endOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(31);
	    Date startDate = Date.valueOf(startOfMonth);
	    Date endDate = Date.valueOf(endOfMonth);
		List<RatingDTO>ratings = ConvertToDTO.convertToRatingDTO(ratingRepository.getRatingLessThanThree(startDate,endDate));
		return ratings.stream().limit(5).collect(Collectors.toList());
	}
	
	/**
	 * Método para devolver las mejores valoraciones del último mes
	 * @return
	 */
	public List<RatingDTO> getRatingGreater() {
	    LocalDate startOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
	    LocalDate endOfMonth = LocalDate.now().minusMonths(1).withDayOfMonth(31);
	    Date startDate = Date.valueOf(startOfMonth);
	    Date endDate = Date.valueOf(endOfMonth);
		List<RatingDTO>ratings = ConvertToDTO.convertToRatingDTO(ratingRepository.getRatingGreaterThanOrEqualThree(startDate,endDate));
		return ratings.stream().limit(5).collect(Collectors.toList());
	}
}
