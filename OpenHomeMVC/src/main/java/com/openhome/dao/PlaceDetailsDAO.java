package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openhome.data.PlaceDetails;

public interface PlaceDetailsDAO extends JpaRepository<PlaceDetails, Long>{
	
}
