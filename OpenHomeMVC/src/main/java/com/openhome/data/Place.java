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
public class Place {

	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Host host;
	
	@OneToMany(fetch = FetchType.LAZY,
			orphanRemoval=true,
			mappedBy = "place")
	private List<Reservation> reservations;
	
	@OneToOne(fetch=FetchType.EAGER,
			orphanRemoval=true,
			cascade=CascadeType.ALL)
	private PlaceDetails placeDetails;
	
	@Transient
	private Boolean bestSuitedSearchResult = false;
	
	public Boolean permanentlyUnavailable = false;
	
	public Place() {
		reservations = new ArrayList<Reservation>();
		placeDetails = new PlaceDetails();
	}
	
	public Place(List<Reservation> reservations, PlaceDetails placeDetails) {
		super();
		this.reservations = reservations;
		this.placeDetails = placeDetails;
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
	public List<Reservation> getReservations() {
		return reservations;
	}
	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	public PlaceDetails getPlaceDetails() {
		return placeDetails;
	}
	public void setPlaceDetails(PlaceDetails placeDetails) {
		this.placeDetails = placeDetails;
	}

	public Boolean getBestSuitedSearchResult() {
		return bestSuitedSearchResult;
	}

	public void setBestSuitedSearchResult(Boolean bestSuitedSearchResult) {
		this.bestSuitedSearchResult = bestSuitedSearchResult;
	}

	public Boolean getPermanentlyUnavailable() {
		return permanentlyUnavailable == null ? false : permanentlyUnavailable;
	}

	public void setPermanentlyUnavailable(Boolean permanentlyUnavailable) {
		this.permanentlyUnavailable = permanentlyUnavailable;
	}
	
}
