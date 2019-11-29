package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openhome.data.UserDetails;

public interface UserDetailsDAO extends JpaRepository<UserDetails, Long>{

	@Query("select ud from UserDetails ud where email = :email")
	public UserDetails getUserByEmail(@Param("email")String email);
	
}
