package com.jacaranda.glamAndGlitter.respository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jacaranda.glamAndGlitter.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Integer>{

	 @Query("SELECT AVG(r.punctuation) FROM Rating r JOIN r.cites c "
	 		+ "WHERE c.day >= :lastMonthDate AND c.day < :today")
	 Double getAverageRatingFromCitasInLastMonth(@Param("lastMonthDate") Date lastMonthDate,@Param("today")Date today);
	
	 @Query("SELECT AVG(r.punctuation) FROM Rating r JOIN r.cites c "
	 		+ "WHERE r.punctuation is not null")
	 Double getAverageFromCompany();
	 
	 @Query("SELECT r FROM Rating r JOIN r.cites c "
		 		+ " WHERE r.punctuation is not null "
		 		+ "AND r.punctuation >= 3 AND c.day >= :lastMonthDate AND c.day < :today")
	 List<Rating> getRatingGreaterThanOrEqualThree(@Param("lastMonthDate") Date lastMonthDate,@Param("today")Date today);
	 
	 @Query("SELECT r FROM Rating r JOIN r.cites c "
	 		+ " WHERE r.punctuation is not null AND r.punctuation < 3 "
	 		+ "AND c.day >= :lastMonthDate AND c.day < :today")
	 List<Rating> getRatingLessThanThree(@Param("lastMonthDate") Date lastMonthDate,@Param("today")Date today);
		
}
