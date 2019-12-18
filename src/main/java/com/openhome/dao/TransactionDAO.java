package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openhome.data.Transaction;

public interface TransactionDAO extends JpaRepository<Transaction, Long>{

	
	
}
