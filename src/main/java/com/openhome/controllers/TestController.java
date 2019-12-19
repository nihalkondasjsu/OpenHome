package com.openhome.controllers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.controllers.helper.Mail;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.PlaceDAO;
import com.openhome.dao.ReservationDAO;
import com.openhome.data.Guest;
import com.openhome.data.Place;
import com.openhome.data.Reservation;
import com.openhome.data.helper.ReservationProcessor;
import com.openhome.exception.CustomException;

@Controller
public class TestController {

	@Autowired(required=true)
	ReservationProcessor reservationManager;
	
	@Autowired(required=true)
	ReservationDAO reservationDao;
	
	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	GuestDAO guestDao;
	
	Place place;
	Guest guest;
	Reservation reservation;
	Date currentDate;
	
	SimpleDateFormat simpleDateFormat;
	
	
	
	@GetMapping("/test1")
	public String test1() throws Exception {
		
		//simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		//testNoShow();
		
		//testAutoCheckOut();
		
		//testInit();
		
		
		
		return ControllerHelper.popupMessageAndRedirect("Good Test", "", 
				new Mail("openhomedksv@gmail.com","Welcome to Open Home","Here is the link https://open-home-dksv.herokuapp.com/")
				);
		
		
	}

	public void testInit() throws Exception {
		place = placeDao.getOne(415l);
		
		guest = guestDao.getOne(376l);
		
		reservation = new Reservation();

		currentDate = simpleDateFormat.parse("2020-04-10 10:00");
		
		reservation.setCheckInDateString("2020-04-20");
		reservation.setCheckOutDateString("2020-04-30");
		
		try {
			reservation.prepareForRegistration(currentDate, place, guest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(reservation);
		
		reservationManager.setReservation(reservation);
		
		reservationManager.showAllCalculations(currentDate);

		currentDate = simpleDateFormat.parse("2020-04-20 10:00");
		
		reservationManager.showAllCalculations(currentDate);
		
		currentDate = simpleDateFormat.parse("2020-04-21 16:00");
		
		reservationManager.showAllCalculations(currentDate);
	}
	
	public void testNoShow() throws Exception {
		testInit();
		reservationManager.processReservation(currentDate);

		currentDate = simpleDateFormat.parse("2020-04-20 14:59");

		reservationManager.processReservation(currentDate);

		//reservationManager.performGuestCheckIn(currentDate);
		
		currentDate = simpleDateFormat.parse("2020-04-20 15:01");

		reservationManager.processReservation(currentDate);
		
		currentDate = simpleDateFormat.parse("2020-04-21 02:59");
		
		reservationManager.processReservation(currentDate);		
		
		currentDate = simpleDateFormat.parse("2020-04-21 03:01");

		reservationManager.processReservation(currentDate);
		
		System.out.println(reservation);
		
		if(reservation.getReservationState() != Reservation.ReservationState.GuestCancelled) {
			throw new CustomException("Reservation was not in GuestCancelled state . No Show did not happen.");
		}
	}
	
	public void testAutoCheckOut() throws Exception {
		testProperCheckIn();

		currentDate = simpleDateFormat.parse("2020-04-30 10:59");
		
		reservationManager.processReservation(currentDate);
		
		currentDate = simpleDateFormat.parse("2020-04-30 11:01");
		
		reservationManager.processReservation(currentDate);
		
		System.out.println(reservation);
		
		if(reservation.getReservationState() != Reservation.ReservationState.CheckedOut) {
			throw new CustomException("Auto check out failed");
		}
	}

	public void testEarlyCheckIn() {
		
	}
	
	public void testProperCheckIn() throws Exception {
		testInit();
		
		currentDate = simpleDateFormat.parse("2020-04-20 15:01");

		reservationManager.performGuestCheckIn(currentDate);
		
		System.out.println(reservation);
		
		if(reservation.getReservationState() != Reservation.ReservationState.CheckedIn) {
			throw new CustomException("Guest Could not check in.");
		}
	}

	public void testLateCheckIn() {
		
	}
	
	public void testEarlyCheckOut() {
		
	}
	
	public void testProperCheckOut() {
		
	}

	public void testLateCheckOut() {
		
	}
	
	public void testGuestCancellation() {
		
	}
	
	public void testGuestCancellationEarly() {
		
	}

	public void testGuestCancellationLate() {
		
	}
	
	public void testHostCancellation() {
		
	}
	
	public void testHostCancellationEarly() {
		
	}

	public void testHostCancellationLate() {
		
	}

	public void testHostCancellationVeryLate() {
		
	}
	
}
