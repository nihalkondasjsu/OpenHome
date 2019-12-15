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

import com.openhome.exception.CustomException;

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
	private List<Reservation> reservations;
	
	public Guest() {
		userDetails = new UserDetails();
		reservations = new ArrayList<Reservation>();
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

	public List<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	
	public boolean canAccess(UserDetails userDetails) throws CustomException {
		if(getUserDetails().getEmail().equals(userDetails.getEmail()) == false)
			throw new CustomException("Invalid Credentials");
		if(getUserDetails().checkPassword(userDetails.getPassword()) == false)
			throw new CustomException("Invalid Credentials");
		return true;	
	}
	
}
