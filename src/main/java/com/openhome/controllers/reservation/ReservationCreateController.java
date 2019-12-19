package com.openhome.controllers.reservation;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.Json;
import com.openhome.OpenHomeMvcApplication;
import com.openhome.aop.helper.annotation.GuestLoginRequired;
import com.openhome.aop.helper.annotation.ValidAlivePlaceId;
import com.openhome.aop.helper.annotation.ValidPlaceId;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.controllers.helper.Mail;
import com.openhome.dao.PlaceDAO;
import com.openhome.dao.ReservationDAO;
import com.openhome.data.Place;
import com.openhome.data.Reservation;
import com.openhome.data.helper.ReservationManager;
import com.openhome.exception.CustomException;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/reservation/create")
public class ReservationCreateController {

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	ReservationManager reservationManager;
	
	@Autowired(required=true)
	SessionManager sessionManager;

	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidAlivePlaceId
	@GuestLoginRequired
	public String getReservationCreatePage(@RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ) {
		model.addAttribute("place", placeDao.getOne(placeId));
		return "reservation/create";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	@ValidAlivePlaceId
	@GuestLoginRequired
	public String postReservationCreate(@RequestParam(value="placeId",required=false) Long placeId, Reservation reservation,Model model , HttpSession httpSession ) {
		
		try {
			
			Json.printObject(reservation);
			
			Place place = placeDao.getOne(placeId);
				
			reservation = reservationManager.createReservation(timeAdvancementManagement.getCurrentDate(), place, reservation, sessionManager.getGuest(httpSession));
			
			return ControllerHelper.popupMessageAndRedirect("Reservation Successfull", "/reservation/view?reservationId="+reservation.getId(),
					new Mail(reservation.getPlace().getHost().getUserDetails().getEmail(),"OpenHome: A Reservation has been made on your place '"+place.getPlaceDetails().getName()+"'","Link to view reservation : "+OpenHomeMvcApplication.baseUrl+"/reservation/view?reservationId="+reservation.getId()),
					new Mail(reservation.getGuest().getUserDetails().getEmail(),"OpenHome: Reservation successfull on '"+place.getPlaceDetails().getName()+"'","Link to view reservation : "+OpenHomeMvcApplication.baseUrl+"/reservation/view?reservationId="+reservation.getId())
					);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "/guest/dashboard");
		}
	}
	
}
