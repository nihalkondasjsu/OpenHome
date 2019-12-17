package com.openhome.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openhome.data.Reservation;

public interface ReservationDAO extends JpaRepository<Reservation, Long>{

	@Query("select r from Reservation r where r.reservationState = 'Booked' OR r.reservationState = 'CheckedIn'")
	public List<Reservation> getAllRunningReservations();

	@Query("select r from Reservation r where r.place = ( select p from Place p where p.id = :placeId ) and r.actualCheckOut >= :startDate and r.checkIn <= :endTime and ( r.reservationState = 'Booked' or r.reservationState = 'CheckedIn' or r.reservationState = 'HostBlock' )")
	public List<Reservation> getUnCancelledReservationsAndHostBlockOnPlaceBetweenDates(
			@Param("placeId")Long placeId,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime
			);
	
	
	@Query("select r from Reservation r where r.place = ( select p from Place p where p.id = :placeId ) and ( r.reservationState = 'Booked' or r.reservationState = 'CheckedIn' )")
	public List<Reservation> getUnCancelledReservationsOnPlace(
			@Param("placeId")Long placeId
			);
	
}
