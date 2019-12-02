package com.openhome.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.tam.TimeAdvancementManagement;

@Entity
@Component
public class Transaction {
	@Id
	@GeneratedValue
	private Long id;
	
	private Double amount;
	
	@Column(nullable=false,updatable=false)
	private Date createdDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Booking booking;
	
	enum TransactionNature{
		Charge,Fee,Payment
	}
	
	@Enumerated(EnumType.STRING)
	TransactionNature transactionNature;
	
	public Transaction() {
		createdDate = new Date();
	}
	
	public Transaction(Date createdDate) {
		createdDate = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
}
