package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.openhome.data.UserDetails;

public interface UserDetailsDAO extends JpaRepository<UserDetails, Long>{

	@Query("select ud from UserDetails ud where email = :email")
	public UserDetails getUserByEmail(@Param("email")String email);
	
	@Transactional
	@Modifying
	@Query("update UserDetails ud set ud.averageRating = :averageRating , ud.totalReviewsCount = :totalReviewsCount where ud.id = :id")
	public void updateRating(@Param("id") Long id, @Param("averageRating") Double averageRating, @Param("totalReviewsCount") Integer totalReviewsCount);
	
}
