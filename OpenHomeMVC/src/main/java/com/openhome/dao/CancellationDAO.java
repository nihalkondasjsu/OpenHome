package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openhome.data.Cancellation;

public interface CancellationDAO extends JpaRepository<Cancellation, Long>{

}
