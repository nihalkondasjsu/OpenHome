package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.openhome.data.Guest;

public interface GuestDAO extends JpaRepository<Guest, Long>{

	@Query("select g from Guest g left join g.userDetails ud where ud.email = ?1")
	public Guest findGuestByEmail(String email);

}
