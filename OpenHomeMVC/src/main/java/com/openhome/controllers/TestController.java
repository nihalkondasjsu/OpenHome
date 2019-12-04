package com.openhome.controllers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	
	SimpleDateFormat simpleDateFormat;
	
	@GetMapping("/test1")
	public String test1() throws Exception {
		
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		testNoShow();
		
		//testAutoCheckOut();
		
		return "index";
	}
	
	public void testInit() throws Exception {
		space = spaceDao.getOne(510l);
		
		guest = guestDao.getOne(492l);
		
		booking = new Booking();

		currentDate = simpleDateFormat.parse("2020-04-10 10:00");
		
		booking.setCheckInDateString("2020-04-20");
		booking.setCheckOutDateString("2020-04-30");
		
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
		testInit();
		bookingManager.processBooking(currentDate);

		currentDate = simpleDateFormat.parse("2020-04-20 14:59");

		bookingManager.processBooking(currentDate);

		//bookingManager.performGuestCheckIn(currentDate);
		
		currentDate = simpleDateFormat.parse("2020-04-20 15:01");

		bookingManager.processBooking(currentDate);
		
		currentDate = simpleDateFormat.parse("2020-04-21 02:59");
		
		bookingManager.processBooking(currentDate);		
		
		currentDate = simpleDateFormat.parse("2020-04-21 03:01");

		bookingManager.processBooking(currentDate);
		
		System.out.println(booking);
		
		if(booking.getBookingState() != Booking.BookingState.GuestCancelled) {
			throw new Exception("Booking was not in GuestCancelled state . No Show did not happen.");
		}
	}
	
	public void testAutoCheckOut() throws Exception {
		testProperCheckIn();

		currentDate = simpleDateFormat.parse("2020-04-30 10:59");
		
		bookingManager.processBooking(currentDate);
		
		currentDate = simpleDateFormat.parse("2020-04-30 11:01");
		
		bookingManager.processBooking(currentDate);
		
		System.out.println(booking);
		
		if(booking.getBookingState() != Booking.BookingState.CheckedOut) {
			throw new Exception("Auto check out failed");
		}
	}

	public void testEarlyCheckIn() {
		
	}
	
	public void testProperCheckIn() throws Exception {
		testInit();
		
		currentDate = simpleDateFormat.parse("2020-04-20 15:01");

		bookingManager.performGuestCheckIn(currentDate);
		
		System.out.println(booking);
		
		if(booking.getBookingState() != Booking.BookingState.CheckedIn) {
			throw new Exception("Guest Could not check in.");
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
