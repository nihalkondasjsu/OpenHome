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
public class Host {

	@Id
	@GeneratedValue
	private Long id;
	
	@OneToOne(fetch=FetchType.EAGER,
			orphanRemoval=true,
			cascade=CascadeType.ALL)
	private UserDetails userDetails;
	
	@OneToMany(fetch = FetchType.LAZY,
			cascade=CascadeType.ALL,
			orphanRemoval=true,
			mappedBy = "host")
	private List<Place> rentingPlaces;
	
	public Host() {
		rentingPlaces = new ArrayList<Place>();
		userDetails = new UserDetails();
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

	public List<Place> getRentingPlaces() {
		return rentingPlaces;
	}

	public void setRentingPlaces(List<Place> rentingPlaces) {
		this.rentingPlaces = rentingPlaces;
	}

	public void addPlace(Place place) {
		if(getUserDetails().verifiedEmail() == false)
			return;
		rentingPlaces.add(place);
		System.out.println(this.getId()+" host | place "+place.getId());
		place.setHost(this);
	}

	public boolean canAccess(UserDetails userDetails) throws CustomException {
		if(getUserDetails().getEmail().equals(userDetails.getEmail()) == false)
			throw new CustomException("Invalid Credentials");
		if(getUserDetails().checkPassword(userDetails.getPassword()) == false)
			throw new CustomException("Invalid Credentials");
		return true;	
	}
	
}
