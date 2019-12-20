package com.openhome.data.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.dao.PlaceDAO;
import com.openhome.dao.ReservationDAO;
import com.openhome.data.Guest;
import com.openhome.data.Place;
import com.openhome.data.Reservation;
import com.openhome.exception.CustomException;

@Component
public class ReservationManager {
	
	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	ReservationDAO reservationDao;

	public Reservation createReservation(Date currentDate,Place place, Reservation reservation, Guest guest) throws ParseException, CustomException {
		reservation.prepareForRegistration(currentDate, place, guest);
		
		if(reservation.getCheckOut() - currentDate.getTime() > 365*24*60*60*1000l) {
			throw new CustomException("Reservation failed.Reservation contains days that are 365 days away from now.");
		}
		
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		Long checkIn = simpleDateFormat.parse(reservation.getCheckInDateString()+" "+"01:00").getTime();
		Long checkOut = simpleDateFormat.parse(reservation.getCheckInDateString()+" "+"01:00").getTime();
		
		if(checkOut - checkIn > 14*24*60*60*1000l) {
			throw new CustomException("Reservation failed.Reservation contains more than 14 days.");
		}
		
		if(reservationDao.getUnCancelledReservationsAndHostBlockOnPlaceBetweenDates(place.getId(), reservation.getCheckIn(), reservation.getCheckOut()).size() > 0) {
			throw new CustomException("Place Unavailable");
		}
			
		return reservationDao.save(reservation);
	}
	
}
