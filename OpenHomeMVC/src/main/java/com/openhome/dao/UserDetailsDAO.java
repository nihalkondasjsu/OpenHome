package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.openhome.data.UserDetails;

public interface UserDetailsDAO extends JpaRepository<UserDetails, Long>{

	@Query("select ud from UserDetails ud where email = ?1")
	public UserDetails getUserByEmail(String email);
	
}
