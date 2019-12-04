package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.openhome.data.PlaceDetails;
import com.openhome.data.UserDetails;

public interface PlaceDetailsDAO extends JpaRepository<PlaceDetails, Long>{
	
}
