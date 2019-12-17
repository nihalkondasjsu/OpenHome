package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.openhome.data.PlaceDetails;

public interface PlaceDetailsDAO extends JpaRepository<PlaceDetails, Long>{
	

	@Transactional
	@Modifying
	@Query("update PlaceDetails pd set pd.averageRating = :averageRating , pd.totalReviewsCount = :totalReviewsCount where pd.id = :id")
	public void updateRating(@Param("id") Long id, @Param("averageRating") Double averageRating, @Param("totalReviewsCount") Integer totalReviewsCount);
	
	
}
