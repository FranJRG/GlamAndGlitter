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
	
	public List<ServiceSummary> getTopServicesWithRatings() {
		LocalDate startOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
	    LocalDate startOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
	    Date startDate = Date.valueOf(startOfLastMonth);
	    Date endDate = Date.valueOf(startOfCurrentMonth);
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
	
	public List<SummaryCites>getCitesInLastMonth(){
		LocalDate startOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
	    LocalDate startOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
	    Date startDate = Date.valueOf(startOfLastMonth);
	    Date endDate = Date.valueOf(startOfCurrentMonth);
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
	
	public AverageMedia getRatingMediaLastMonth() {
		LocalDate startOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
	    LocalDate startOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
	    Date startDate = Date.valueOf(startOfLastMonth);
	    Date endDate = Date.valueOf(startOfCurrentMonth);
	    Double averageMedia = ratingRepository.getAverageRatingFromCitasInLastMonth(startDate, endDate);
	    return new AverageMedia(averageMedia);
	}
	
	public AverageMedia getTotalMedia() {
		Double averageMedia = ratingRepository.getAverageFromCompany();
		AverageMedia media = new AverageMedia(averageMedia);
		return media;
	}
	
	public List<RatingDTO> getRatingLess() {
		LocalDate startOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
	    LocalDate startOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
	    Date startDate = Date.valueOf(startOfLastMonth);
	    Date endDate = Date.valueOf(startOfCurrentMonth);
		List<RatingDTO>ratings = ConvertToDTO.convertToRatingDTO(ratingRepository.getRatingLessThanThree(startDate,endDate));
		return ratings.stream().limit(5).collect(Collectors.toList());
	}
	
	public List<RatingDTO> getRatingGreater() {
		LocalDate startOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
	    LocalDate startOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
	    Date startDate = Date.valueOf(startOfLastMonth);
	    Date endDate = Date.valueOf(startOfCurrentMonth);
		List<RatingDTO>ratings = ConvertToDTO.convertToRatingDTO(ratingRepository.getRatingGreaterThanOrEqualThree(startDate,endDate));
		return ratings.stream().limit(5).collect(Collectors.toList());
	}
}
