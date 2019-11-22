package com.openhome.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

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
	private List<Space> rentingSpaces;
	
	public Host() {
		rentingSpaces = new ArrayList<Space>();
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

	public List<Space> getRentingSpaces() {
		return rentingSpaces;
	}

	public void setRentingSpaces(List<Space> rentingSpaces) {
		this.rentingSpaces = rentingSpaces;
	}

	public void addSpace(Space space) {
		if(getUserDetails().verifiedEmail() == false)
			return;
		rentingSpaces.add(space);
		space.setHost(this);
	}

	public boolean canAccess(UserDetails userDetails) throws IllegalAccessException {
		if(getUserDetails().getEmail().equals(userDetails.getEmail()) == false)
			throw new IllegalAccessException("Invalid Credentials");
		if(getUserDetails().checkPassword(userDetails.getPassword()) == false)
			throw new IllegalAccessException("Invalid Credentials");
		return true;	
	}
	
}
