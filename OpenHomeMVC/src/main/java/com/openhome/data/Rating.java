package com.openhome.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Rating {
	@Id
	@GeneratedValue
	private Long id;
	
	private Double rating=0.0;
	
	private String title="";
	private String review="";

	private Date createdDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Reservation reservation;
	
	public enum RatingTarget{
		HOST,PLACE,GUEST
	}
	
	@Enumerated(EnumType.STRING)
	private RatingTarget target;
	
	public Rating() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public RatingTarget getTarget() {
		return target;
	}

	public void setTarget(RatingTarget target) {
		this.target = target;
	}

	public void prepareForRegistration(Date createdDate,Reservation reservation) {
		this.createdDate = createdDate;
		this.reservation = reservation;
	}
	
}
