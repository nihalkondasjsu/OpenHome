package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openhome.data.TimeManagement;

public interface TimeManagementDAO extends JpaRepository<TimeManagement, Integer>{

}
