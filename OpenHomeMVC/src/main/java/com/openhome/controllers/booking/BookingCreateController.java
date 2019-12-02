package com.openhome.controllers.booking;

import java.text.ParseException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.Json;
import com.openhome.aop.helper.annotation.GuestLoginRequired;
import com.openhome.aop.helper.annotation.ValidSpaceId;
import com.openhome.dao.BookingDAO;
import com.openhome.dao.SpaceDAO;
import com.openhome.data.Booking;
import com.openhome.data.Space;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/booking/create")
public class BookingCreateController {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	BookingDAO bookingDao;

	
	@Autowired
	SessionManager sessionManager;

	@Autowired
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidSpaceId
	@GuestLoginRequired
	public String getBookingCreatePage(@RequestParam(value="spaceId",required=false) Long spaceId, Model model , HttpSession httpSession ) {
		model.addAttribute("space", spaceDao.getOne(spaceId));
		return "booking/create";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	@ValidSpaceId
	@GuestLoginRequired
	public String postBookingCreate(@RequestParam(value="spaceId",required=false) Long spaceId, Booking booking,Model model , HttpSession httpSession ) {
		
		try {
			
			Json.printObject(booking);
			
			Space space = spaceDao.getOne(spaceId);
			
			booking.prepareForRegistration(timeAdvancementManagement.getCurrentDate(), space, sessionManager.getGuest(httpSession));
			
			if(spaceDao.getSpecifiSpacesCountByDates(spaceId, booking.getCheckIn(), booking.getCheckOut(), booking.getRequiredDays()) == 0) {
				throw new IllegalArgumentException("Space Unavailable");
			}
				
			bookingDao.save(booking);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/guest/dashboard";
	}
	
}
