package com.openhome.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.openhome.data.Reservation;

public interface ReservationDAO extends JpaRepository<Reservation, Long>{

	@Query("select r from Reservation r where r.reservationState = 'Booked' OR r.reservationState = 'CheckedIn'")
	public List<Reservation> getAllRunningReservations();
	
	//public List<Reservation> getAllReservationsOnPlaceBetweenDates();
	
}
