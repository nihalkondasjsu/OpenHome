package com.openhome.controllers;


import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.openhome.dao.BookingDAO;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.SpaceDAO;
import com.openhome.data.Booking;
import com.openhome.data.Guest;
import com.openhome.data.Space;
import com.openhome.data.manager.BookingManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
public class TestController {

	@Autowired
	BookingManager bookingManager;
	
	@Autowired
	BookingDAO bookingDao;
	
	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	GuestDAO guestDao;
	
	Space space;
	Guest guest;
	Booking booking;
	Date currentDate;
	
	@GetMapping("/test1")
	public String test1() throws IllegalAccessException {
		testRefresh();
		
		return "index";
	}
	
	public void testRefresh() {
		space = spaceDao.getOne(510l);
		
		guest = guestDao.getOne(492l);
		
		booking = new Booking();

		currentDate = new Date(2020-1900,04-01,10);
		
		booking.setCheckInDateString("2020-04-15");
		booking.setCheckOutDateString("2020-04-25");
		
		try {
			booking.prepareForRegistration(currentDate, space, guest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(booking);
		
		bookingManager.setBooking(booking);
		
	}
	
	public void testNoShow() throws Exception {
		testRefresh();
		bookingManager.processBooking(currentDate);

		currentDate = new Date(2020-1900,04-01,15,14,59);

		bookingManager.processBooking(currentDate);

		//bookingManager.performGuestCheckIn(currentDate);
		
		currentDate = new Date(2020-1900,04-01,15,15,01);

		bookingManager.processBooking(currentDate);
		
		currentDate = new Date(2020-1900,04-01,16,2,59);
		
		bookingManager.processBooking(currentDate);		
		
		currentDate = new Date(2020-1900,04-01,16,3,01);

		bookingManager.processBooking(currentDate);
		
		System.out.println(booking);
		
		if(booking.getBookingState().equals(Booking.BookingState.GuestCancelled) != false) {
			throw new Exception("Booking was not in GuestCancelled state . No Show did not happen.");
		}
	}
	
	public void testAutoCheckOut() {
		
	}
	
	public void testEarlyCheckIn() {
		
	}
	
	public void testProperCheckIn() {
		
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
