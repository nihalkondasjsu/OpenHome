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

import org.springframework.stereotype.Component;

@Entity
@Component
public class Transaction {
	@Id
	@GeneratedValue
	private Long id;
	
	private Double amount;
	
	@Column(nullable=false,updatable=false)
	private Date createdDate;
	
	@Column(nullable=false,updatable=false)
	private Date dayToChargeFor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Reservation reservation;
	
	public enum TransactionNature{
		Charge,Fee,Payment
	}
	
	public enum TransactionUser{
		Guest,Host
	}
	
	@Enumerated(EnumType.STRING)
	TransactionUser transactionUser;

	@Enumerated(EnumType.STRING)
	TransactionNature transactionNature;
	
	public Transaction() {
		createdDate = new Date();
	}

	public Transaction(Double amount, Date createdDate, Date dayToChargeFor, Reservation reservation, TransactionNature transactionNature, TransactionUser transactionUser) {
		this.amount = amount;
		this.createdDate = createdDate;
		this.dayToChargeFor = dayToChargeFor;
		this.reservation = reservation;
		this.transactionNature = transactionNature;
		this.transactionUser = transactionUser;
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
	
	public Date getDayToChargeFor() {
		return dayToChargeFor;
	}

	public void setDayToChargeFor(Date dayToChargeFor) {
		this.dayToChargeFor = dayToChargeFor;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public TransactionNature getTransactionNature() {
		return transactionNature;
	}

	public void setTransactionNature(TransactionNature transactionNature) {
		this.transactionNature = transactionNature;
	}

	public TransactionUser getTransactionUser() {
		return transactionUser;
	}

	public void setTransactionUser(TransactionUser transactionUser) {
		this.transactionUser = transactionUser;
	}

	@Override
	public String toString() {
		return String.format("{ amount : %f , createdDate : %s , dayToChargeFor : %s , transactionNature : %s , transactionUser : %s }\n"
				,amount,createdDate,dayToChargeFor,transactionNature,transactionUser);
	}
	
}
