package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.openhome.data.SpaceDetails;
import com.openhome.data.UserDetails;

public interface SpaceDetailsDAO extends JpaRepository<SpaceDetails, Long>{
	
}
