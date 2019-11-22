package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openhome.data.Payment;

public interface PaymentDAO extends JpaRepository<Payment, Long>{

}
