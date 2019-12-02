package com.openhome.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Guest {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToOne(fetch=FetchType.EAGER,
			orphanRemoval=true,
			cascade=CascadeType.ALL)
	private UserDetails userDetails;
	
	@OneToMany(fetch = FetchType.LAZY,
			orphanRemoval=true,
			mappedBy = "guest")
	private List<Booking> bookings;
	
	public Guest() {
		userDetails = new UserDetails();
		bookings = new ArrayList<Booking>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
	
	public boolean canAccess(UserDetails userDetails) throws IllegalAccessException {
		if(getUserDetails().getEmail().equals(userDetails.getEmail()) == false)
			throw new IllegalAccessException("Invalid Credentials");
		if(getUserDetails().checkPassword(userDetails.getPassword()) == false)
			throw new IllegalAccessException("Invalid Credentials");
		return true;	
	}
	
}
