package com.openhome.controllers.place;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.Json;
import com.openhome.aop.helper.annotation.PlaceHostLoginRequired;
import com.openhome.aop.helper.annotation.ValidAlivePlaceId;
import com.openhome.aop.helper.annotation.ValidPlaceId;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.PlaceDAO;
import com.openhome.dao.ReservationDAO;
import com.openhome.data.Place;
import com.openhome.data.Reservation;
import com.openhome.data.helper.ReservationProcessor;
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
	ReservationProcessor reservationProcessor;
	
	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidAlivePlaceId
	@PlaceHostLoginRequired
	public String updateForm(@RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ) {
		System.out.println("PlaceUpdateController");
		
		Place s = placeDao.getOne(placeId);
		
		model.addAttribute("place", s);
			
		return "reservation/block";
		
	}

	@RequestMapping(method=RequestMethod.POST)
	@ValidAlivePlaceId
	@PlaceHostLoginRequired
	public String updateFormSubmission(@RequestParam(value="placeId",required=false) Long placeId, Reservation reservation,Model model , HttpSession httpSession ) {
		System.out.println("HaPPY");
		try {
			
			Json.printObject(reservation);
			
			Place place = placeDao.getOne(placeId);
			
			reservation.prepareForHostBlock(timeAdvancementManagement.getCurrentDate(), place);//(timeAdvancementManagement.getCurrentDate(), place, sessionManager.getGuest(httpSession));
			
			List<Reservation> reservations = reservationDao.getUnCancelledReservationsAndHostBlockOnPlaceBetweenDates(place.getId(), reservation.getCheckIn(), reservation.getCheckOut());
			
			Date date = timeAdvancementManagement.getCurrentDate();
			
			if(reservations.size() > 0) {
//				for (Reservation reservation2 : reservations) {
//					reservationProcessor.setReservation(reservation2);
//					reservationProcessor.performHostCancel(date);
//					reservationDao.save(reservationProcessor.getReservation());
//				}
				throw new CustomException("Place has some reservations made in the specified time span.Please cancel them first.");
			}
				
			reservation = reservationDao.save(reservation);
			
			return ControllerHelper.popupMessageAndRedirect("Reservation Successfull", "/place/view?placeId="+placeId);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "/host/dashboard");
		}
		
	}
	
}
