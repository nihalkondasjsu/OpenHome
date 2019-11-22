package com.openhome.data;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.openhome.dao.helper.StringListConverter;

@Entity
public class SpaceDetails {

	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id;
	
	private String name;
	
	private String propertyType = "Apartment";
	
	private String roomType = "Shared Room";
	
	private Integer accomodates = 2;
	
	@Column(length=512)
	@Convert(converter = StringListConverter.class)
	private List<String> amenities;
	//private String amenities = "Kitchen;Shampoo;Heating;Air conditioning;Washer;Dryer;Wifi;Breakfast;Indoor fireplace;Hangers;Iron;Hair dryer;Laptop friendly workspace;TV;Crib;High chair;Self check-in;Smoke detector;Carbon monoxide detector;Private bathroom;Beachfront;Waterfront;";
	
	@Convert(converter = StringListConverter.class)
	private List<String> facilities;
	//private String facilities = "Free parking on premises;Gym;Hot tub;Pool;";
	
	@Convert(converter = StringListConverter.class)
	private List<String> houseRules;
	//private String houseRules = "Suitable for events;Pets allowed;Smoking allowed;";
	
	@Convert(converter = StringListConverter.class)
	private List<String> images;
	
	@Column(nullable = false)
	private Integer noOfBedrooms = 2;

	@Column(nullable = false)
	private Integer noOfBathrooms = 2;

	@Column(nullable = false)
	private Integer noOfBeds = 2;

	@Column(nullable = false)
	private Double weekdayRentPrice = 100.0;

	@Column(nullable = false)
	private Double weekendRentPrice = 140.0;
	
	@Column(nullable = false)
	private Double sqft = 400.0;
	
	@Column(length=512,nullable = false)
	private String description = "Good Place";

	private Boolean freeWiFi = false;
	
	private Boolean parkingAvailable = false;
	
	@Column(nullable = false)
	private Double dailyParkingFee = 0.0;
	
	@OneToOne(fetch=FetchType.EAGER,
			orphanRemoval=true,
			cascade=CascadeType.ALL)
	private Address address;

	@Column(nullable=false,updatable=false)
	private Date registeredDate;
	
	public SpaceDetails() {
		registeredDate = new Date();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	public String getRoomType() {
		return roomType;
	}
	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}
	public Integer getAccomodates() {
		return accomodates;
	}
	public void setAccomodates(Integer accomodates) {
		this.accomodates = accomodates;
	}
	public List<String> getAmenities() {
		return amenities;
	}
	public void setAmenities(List<String> amenities) {
		this.amenities = amenities;
	}
	public List<String> getFacilities() {
		return facilities;
	}
	public void setFacilities(List<String> facilities) {
		this.facilities = facilities;
	}
	public List<String> getHouseRules() {
		return houseRules;
	}
	public void setHouseRules(List<String> houseRules) {
		this.houseRules = houseRules;
	}
	public List<String> getImages() {
		return images;
	}
	public void setImages(List<String> images) {
		this.images = images;
	}
	public Integer getNoOfBedrooms() {
		return noOfBedrooms;
	}
	public void setNoOfBedrooms(Integer noOfBedrooms) {
		this.noOfBedrooms = noOfBedrooms;
	}
	public Integer getNoOfBathrooms() {
		return noOfBathrooms;
	}
	public void setNoOfBathrooms(Integer noOfBathrooms) {
		this.noOfBathrooms = noOfBathrooms;
	}
	public Integer getNoOfBeds() {
		return noOfBeds;
	}
	public void setNoOfBeds(Integer noOfBeds) {
		this.noOfBeds = noOfBeds;
	}
	public Double getWeekdayRentPrice() {
		return weekdayRentPrice;
	}
	public void setWeekdayRentPrice(Double weekdayRentPrice) {
		this.weekdayRentPrice = weekdayRentPrice;
	}
	public Double getWeekendRentPrice() {
		return weekendRentPrice;
	}
	public void setWeekendRentPrice(Double weekendRentPrice) {
		this.weekendRentPrice = weekendRentPrice;
	}
	public Double getSqft() {
		return sqft;
	}
	public void setSqft(Double sqft) {
		this.sqft = sqft;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getFreeWiFi() {
		return freeWiFi;
	}
	public void setFreeWiFi(Boolean freeWiFi) {
		this.freeWiFi = freeWiFi;
	}
	public Boolean getParkingAvailable() {
		return parkingAvailable;
	}
	public void setParkingAvailable(Boolean parkingAvailable) {
		this.parkingAvailable = parkingAvailable;
	}
	public Double getDailyParkingFee() {
		return dailyParkingFee;
	}
	public void setDailyParkingFee(Double dailyParkingFee) {
		this.dailyParkingFee = dailyParkingFee;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
}
