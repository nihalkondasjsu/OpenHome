package com.openhome.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.openhome.dao.helper.StringListConverter;

@Entity
public class Space {

	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Host host;
	
	@OneToMany(fetch = FetchType.LAZY,
			orphanRemoval=true,
			mappedBy = "space")
	private List<Booking> bookings;
	
	@OneToOne(fetch=FetchType.EAGER,
			orphanRemoval=true,
			cascade=CascadeType.ALL)
	private SpaceDetails spaceDetails;
	
	@Transient
	private Boolean bestSuitedSearchResult = false;
	
	public Space() {
		bookings = new ArrayList<Booking>();
		spaceDetails = new SpaceDetails();
	}
	
	public Space(List<Booking> bookings, SpaceDetails spaceDetails) {
		super();
		this.bookings = bookings;
		this.spaceDetails = spaceDetails;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Host getHost() {
		return host;
	}
	public void setHost(Host host) {
		this.host = host;
	}
	public List<Booking> getBookings() {
		return bookings;
	}
	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
	public SpaceDetails getSpaceDetails() {
		return spaceDetails;
	}
	public void setSpaceDetails(SpaceDetails spaceDetails) {
		this.spaceDetails = spaceDetails;
	}

	public Boolean getBestSuitedSearchResult() {
		return bestSuitedSearchResult;
	}

	public void setBestSuitedSearchResult(Boolean bestSuitedSearchResult) {
		this.bestSuitedSearchResult = bestSuitedSearchResult;
	}
	
	
	
}
