package com.openhome.controllers.reservation;

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
import com.openhome.aop.helper.annotation.ValidPlaceId;
import com.openhome.dao.ReservationDAO;
import com.openhome.dao.PlaceDAO;
import com.openhome.data.Reservation;
import com.openhome.data.Place;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/reservation/create")
public class ReservationCreateController {

	@Autowired
	PlaceDAO placeDao;
	
	@Autowired
	ReservationDAO reservationDao;

	
	@Autowired
	SessionManager sessionManager;

	@Autowired
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidPlaceId
	@GuestLoginRequired
	public String getReservationCreatePage(@RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ) {
		model.addAttribute("place", placeDao.getOne(placeId));
		return "reservation/create";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	@ValidPlaceId
	@GuestLoginRequired
	public String postReservationCreate(@RequestParam(value="placeId",required=false) Long placeId, Reservation reservation,Model model , HttpSession httpSession ) {
		
		try {
			
			Json.printObject(reservation);
			
			Place place = placeDao.getOne(placeId);
			
			reservation.prepareForRegistration(timeAdvancementManagement.getCurrentDate(), place, sessionManager.getGuest(httpSession));
			
			if(placeDao.getSpecifiPlacesCountByDates(placeId, reservation.getCheckIn(), reservation.getCheckOut(), reservation.getRequiredDays()) == 0) {
				throw new IllegalArgumentException("Place Unavailable");
			}
				
			reservationDao.save(reservation);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/guest/dashboard";
	}
	
}
