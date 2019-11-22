package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openhome.data.Rating;

public interface RatingDAO extends JpaRepository<Rating, Long>{

}
