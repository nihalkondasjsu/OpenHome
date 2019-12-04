package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openhome.data.Reservation;

public interface ReservationDAO extends JpaRepository<Reservation, Long>{

}
