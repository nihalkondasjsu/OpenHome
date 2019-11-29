package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openhome.data.Host;

public interface HostDAO extends JpaRepository<Host,Long>{

	@Query("select h from Host h left join h.userDetails ud where ud.email = :email")
	//@Query("select p from Person p left join p.qualifications q where q.experienceInMonths > ?1 and q.experienceInMonths < ?2 and q.name = ?3")
	public Host findHostByEmail(@Param("email")String email);
	
}
