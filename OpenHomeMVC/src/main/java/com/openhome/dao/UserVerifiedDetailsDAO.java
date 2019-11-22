package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openhome.data.UserVerifiedDetails;

public interface UserVerifiedDetailsDAO extends JpaRepository<UserVerifiedDetails, Long>{

}
