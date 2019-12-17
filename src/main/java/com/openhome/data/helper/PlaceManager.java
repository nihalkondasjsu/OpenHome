package com.openhome.data.helper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.openhome.dao.PlaceDAO;
import com.openhome.dao.PlaceDetailsDAO;
import com.openhome.dao.ReservationDAO;
import com.openhome.data.Host;
import com.openhome.data.Image;
import com.openhome.data.Place;
import com.openhome.data.PlaceDetails;
import com.openhome.data.PlaceSearchQuery;
import com.openhome.data.Reservation;
import com.openhome.exception.CustomException;

@Component
public class PlaceManager {

	@Autowired(required=true)
	PlaceDAO placeDao;

	@Autowired(required=true)
	PlaceDetailsDAO placeDetailsDao;
	
	@Autowired(required=true)
	ReservationDAO reservationDao;
	
	@Autowired(required=true)
	ImageManager imageManager;

	public Place registerPlace(Date currentDate,Host host,PlaceDetails placeDetails,MultipartFile imageFile,String imageUrl) throws CustomException {
		Place place = new Place();
		placeDetails.prepareForRegistration(currentDate);
		Image imageObj = imageManager.getImage(imageFile, imageUrl);
		if(imageObj != null) {
			System.out.println("Image Provided");
			placeDetails.addImage(imageObj);
		}
		place.setPlaceDetails(placeDetails);
		place.setHost(host);
		host.addPlace(place);
		place = placeDao.save(place);
		return place;
	}
	
	public Place updatePlace(Long placeId, PlaceDetails placeDetails) throws CustomException {
		Place s = placeDao.getOne(placeId);
		
		placeDetails.updateDetails(s.getPlaceDetails());
		
		placeDetailsDao.save(placeDetails);

		return s;
	}
	
	public List<Place> searchPlaces(PlaceSearchQuery placeSearchQuery) throws CustomException, ParseException{
		List<Place> places ;
		
		if(placeSearchQuery.isCityQuery()) {
			places = placeDao.getPlacesByCityAndDatesAndPrice(placeSearchQuery.getCityOrZip(), placeSearchQuery.getReservationStartDateTimeObj().getTime(), placeSearchQuery.getReservationEndDateTimeObj().getTime(), placeSearchQuery.getMinPrice(), placeSearchQuery.getMaxPrice(), placeSearchQuery.getWeekDays());
		}else {
			places = placeDao.getPlacesByZipAndDatesAndPrice(placeSearchQuery.getCityOrZip(), placeSearchQuery.getReservationStartDateTimeObj().getTime(), placeSearchQuery.getReservationEndDateTimeObj().getTime(), placeSearchQuery.getMinPrice(), placeSearchQuery.getMaxPrice(), placeSearchQuery.getWeekDays());	
		}
		
		List<Place> aList = new ArrayList<Place>();
		List<Place> bList = new ArrayList<Place>();
		
		for (Place place : places) {
			boolean temp = placeSearchQuery.suitableMatch(place.getPlaceDetails());
			place.setBestSuitedSearchResult(temp);
			if(temp)
				aList.add(place);
			else
				bList.add(place);
		}
		
		aList.addAll(bList);
		
		return aList;
	}
	
	public Place deletePlace(Date currentDate,Long placeId) throws CustomException {
		Place s = placeDao.getOne(placeId);
		
		List<Reservation> reservations = reservationDao.getUnCancelledReservationsOnPlace(placeId);
		
		if(reservations.size() != 0)
			throw new CustomException("Cannot delete Place.Place has future reservations.");
		
		s.setPermanentlyUnavailable(true);
		
		return placeDao.save(s);
	}
	
	public List<Place> listAllPlaces(){
		return placeDao.findAll();
	}
	
	public Place getPlaceById(Long id) {
		return placeDao.getOne(id);
	}
	
}
