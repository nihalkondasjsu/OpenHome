package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openhome.data.Booking;

public interface BookingDAO extends JpaRepository<Booking, Long>{

}
