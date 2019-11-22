package com.openhome.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Rating {
	@Id
	@GeneratedValue
	private Long id;
	
	private Double ratingOverall;
	
	private Double ratingCommunication;
	private Double ratingCheckIn;
	private Double ratingLocation;
	private Double ratingValue;
	private Double ratingCleanliness;
	private Double ratingAccuracy;
	
	private String title;
	private String review;

	private Date createdDate;
	
	@OneToOne(fetch = FetchType.LAZY)
	private Booking booking;
	
	public Rating() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getRatingOverall() {
		return ratingOverall;
	}

	public void setRatingOverall(Double ratingOverall) {
		this.ratingOverall = ratingOverall;
	}

	public Double getRatingCommunication() {
		return ratingCommunication;
	}

	public void setRatingCommunication(Double ratingCommunication) {
		this.ratingCommunication = ratingCommunication;
	}

	public Double getRatingCheckIn() {
		return ratingCheckIn;
	}

	public void setRatingCheckIn(Double ratingCheckIn) {
		this.ratingCheckIn = ratingCheckIn;
	}

	public Double getRatingLocation() {
		return ratingLocation;
	}

	public void setRatingLocation(Double ratingLocation) {
		this.ratingLocation = ratingLocation;
	}

	public Double getRatingValue() {
		return ratingValue;
	}

	public void setRatingValue(Double ratingValue) {
		this.ratingValue = ratingValue;
	}

	public Double getRatingCleanliness() {
		return ratingCleanliness;
	}

	public void setRatingCleanliness(Double ratingCleanliness) {
		this.ratingCleanliness = ratingCleanliness;
	}

	public Double getRatingAccuracy() {
		return ratingAccuracy;
	}

	public void setRatingAccuracy(Double ratingAccuracy) {
		this.ratingAccuracy = ratingAccuracy;
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
	
}
