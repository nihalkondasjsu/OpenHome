package com.openhome.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import com.openhome.data.Image;
import com.openhome.data.PlaceDetails;
import com.openhome.tam.TimeAdvancementManagement;

public class SampleDBData {

	
	Double[][] gps = new Double[][] {
		{37.336259,-121.887067},
		{38.7034611,-121.1685456},
		{37.337456,-121.8846333},
		{40.755279,-73.99515229},
		{40.776502,-73.9353651},
		{32.7573662,-96.859636},
		{30.385848,-97.8866334},
		{36.2765547,-115.330728},
		{35.9995025,-115.1534734},
		{38.9088436,-76.99967679}
	};
	
	String[] addressLine1 = new String[] {
		"101  East San Fernando Street",
		"7550  Folsom-Auburn Road",
		"55  South 6th Street",
		"400  West 37th Street",
		"10  Halletts Point",
		"2511  Wedglea Drive",
		"4306  North Quinlan Park Road",
		"10697  West Centennial Parkway",
		"430  East Cactus Avenue",
		"1270  4th Street Northeast"
	};
	
	String[] city = new String[] {
		"San Jose",
		"Folsom",
		"San Jose",
		"New York",
		"Queens",
		"Dallas",
		"Austin",
		"Las Vegas",
		"Las Vegas",
		"Washington"
	};
	
	String[] state = new String[] {
		"California",
		"California",
		"California",
		"New York",
		"New York",
		"Texas",
		"Texas",
		"Nevada",
		"Nevada",
		"District of Columbia"
	};
	
	String[] zip = new String[] {
		"95112",
		"95630",
		"95112",
		"10018",
		"11102",
		"75211",
		"78732",
		"89166",
		"89183",
		"20002"
	};
	
	List<String> amenities = Arrays.asList("Kitchen;Shampoo;Heating;Air conditioning;Washer;Dryer;Wifi;Breakfast;Indoor fireplace;Hangers;Iron;Hair dryer;Laptop friendly workplace;TV;Crib;High chair;Self check-in;Smoke detector;Carbon monoxide detector;Private bathroom;Beachfront;Waterfront;".split(";"));
	
	List<String> facilities = Arrays.asList("Free parking on premises;Gym;Hot tub;Pool;".split(";"));
	
	List<String> houseRules = Arrays.asList("Suitable for events;Pets allowed;Smoking allowed;".split(";"));
	
	public List<String> getRandomElement(List<String> list) { 
		Random rand = new Random(); 
		
		int totalItems = rand.nextInt(list.size());
		
		ArrayList<String> newList = new ArrayList<>(list); 
		
		Collections.shuffle(newList); 
		
		return newList.subList(0, newList.size()/2); 
	} 
	
	public PlaceDetails getRandomPlaceDetails() {
		PlaceDetails sd = new PlaceDetails();
		
		Random R = new Random();
		
		int bedrooms = 0; 

		while(bedrooms <= 1) {
			bedrooms = R.nextInt(5);
		}
		
		sd.setAccomodates(bedrooms*2);
		
		sd.setAmenities(getRandomElement(amenities));
		
		sd.setFacilities(getRandomElement(facilities));
		
		sd.setFreeWiFi(sd.getAmenities().contains("Wifi"));
		
		sd.setHouseRules(getRandomElement(houseRules));
		
		sd.setImages(new ArrayList<Image>());
		
		sd.setNoOfBathrooms(R.nextInt(bedrooms));
		
		sd.setNoOfBedrooms(bedrooms);
		
		sd.setNoOfBeds(R.nextInt(bedrooms)+bedrooms);
		
		sd.setParkingAvailable(sd.getFacilities().contains("Free parking on premises")?true:R.nextBoolean());

		sd.setDailyParkingFee(sd.getParkingAvailable()?(sd.getFacilities().contains("Free parking on premises")?0.0:bedrooms*5.0):0.0);
		
		sd.setSqft(bedrooms*200.0);
		
		sd.setWeekdayRentPrice(bedrooms*40.0);

		sd.setWeekendRentPrice(bedrooms*50.0);
		
		return sd;
	}
	
}
