package com.openhome.controllers.place;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.Json;
import com.openhome.aop.helper.annotation.PlaceHostLoginRequired;
import com.openhome.aop.helper.annotation.ValidPlaceId;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.PlaceDAO;
import com.openhome.dao.ReservationDAO;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.data.Reservation;
import com.openhome.exception.CustomException;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/place/availability")
public class PlaceAvailabilityController {

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	ReservationDAO reservationDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;
	
	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidPlaceId
	@PlaceHostLoginRequired
	public String updateForm(@RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ) {
		System.out.println("PlaceUpdateController");
		
		Place s = null;
		
		s = placeDao.getOne(placeId);
			
		Host h = sessionManager.getHost(httpSession);
		
		model.addAttribute("place", s);
			
		return "reservation/block";
		
	}

	@RequestMapping(method=RequestMethod.POST)
	@ValidPlaceId
	@PlaceHostLoginRequired
	public String updateFormSubmission(@RequestParam(value="placeId",required=false) Long placeId, Reservation reservation,Model model , HttpSession httpSession ) {
		System.out.println("HaPPY");
		try {
			
			Json.printObject(reservation);
			
			Place place = placeDao.getOne(placeId);
			
			reservation.prepareForHostBlock(timeAdvancementManagement.getCurrentDate(), place);//(timeAdvancementManagement.getCurrentDate(), place, sessionManager.getGuest(httpSession));
			
			if(placeDao.getSpecifiPlacesCountByDates(placeId, reservation.getCheckIn(), reservation.getCheckOut(), reservation.getRequiredDays()) == 0) {
				throw new CustomException("Place Unavailable");
			}
				
			reservation = reservationDao.save(reservation);
			
			return ControllerHelper.popupMessageAndRedirect("Reservation Successfull", "/reservation/view?reservationId="+reservation.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "/guest/dashboard");
		}
		
	}
	
}
