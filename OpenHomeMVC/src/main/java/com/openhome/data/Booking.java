package com.openhome.data;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Booking {
	
	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id;
	
	@Column(nullable=false,updatable=false)
	private Date createdDate;
	
	private Long checkIn;
	
	private Long checkOut;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Space space;
	
	@OneToOne(
			orphanRemoval=true)
//	@Column(nullable=false,updatable=false)
	private Payment payment;
	
	@OneToOne(
			orphanRemoval=true)
	private Rating rating;
	
	@ManyToOne
	private Guest guest;
	
	@OneToOne(
			orphanRemoval=true)
	private Cancellation cancellation;

	public Booking() {
		createdDate = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getCheckIn() {
		return checkIn;
	}
	
	public Date dateOfCheckIn() {
		return new Date(checkIn);
	}

	public void setCheckIn(Long checkIn) {
		this.checkIn = checkIn;
	}

	public Long getCheckOut() {
		return checkOut;
	}

	public Date dateOfCheckOut() {
		return new Date(checkOut);
	}
	
	public void setCheckOut(Long checkOut) {
		this.checkOut = checkOut;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}

	public Cancellation getCancellation() {
		return cancellation;
	}

	public void setCancellation(Cancellation cancellation) {
		this.cancellation = cancellation;
	}
	
	
	
}
