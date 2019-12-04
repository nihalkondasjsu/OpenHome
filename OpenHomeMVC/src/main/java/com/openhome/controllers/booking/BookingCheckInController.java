package com.openhome.controllers.booking;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.aop.helper.annotation.BookingAssociatedGuestLoginRequired;
import com.openhome.aop.helper.annotation.ValidBookingId;
import com.openhome.dao.BookingDAO;
import com.openhome.data.Booking;
import com.openhome.data.manager.BookingManager;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/booking/checkIn")
public class BookingCheckInController {

	@Autowired
	BookingDAO bookingDao;

	@Autowired
	BookingManager bookingProcessor;
	
	@Autowired
	SessionManager sessionManager;
	
	@Autowired
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidBookingId
	@BookingAssociatedGuestLoginRequired
	public String guestCheckIn(@RequestParam(value="bookingId",required=false) Long bookingId, Model model , HttpSession httpSession ) {
		Booking booking = bookingDao.getOne(bookingId);
		try {
			bookingProcessor.setBooking(booking);
			bookingProcessor.performGuestCheckIn(timeAdvancementManagement.getCurrentDate());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		bookingDao.save(booking);
		return "redirect:/guest/dashboard";
	}
	
}
