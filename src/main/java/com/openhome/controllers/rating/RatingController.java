package com.openhome.controllers.rating;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.aop.helper.annotation.ReservationAssociatedUserLoginRequired;
import com.openhome.aop.helper.annotation.ValidGuestId;
import com.openhome.aop.helper.annotation.ValidHostId;
import com.openhome.aop.helper.annotation.ValidPlaceId;
import com.openhome.aop.helper.annotation.ValidReservationId;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.HostDAO;
import com.openhome.dao.PlaceDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.data.Rating;
import com.openhome.data.Reservation;
import com.openhome.data.helper.RatingManager;
import com.openhome.exception.ExceptionManager;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
public class RatingController {

	@Autowired(required=true)
	HostDAO hostDao;
	
	@Autowired(required=true)
	GuestDAO guestDao;
	
	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	RatingManager ratingManager;
	
	@Autowired(required=true)
	SessionManager sessionManager;

	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@Autowired(required=true)
	ExceptionManager exceptionManager;
	
	//rating list
	
	@RequestMapping(value = "host/rating/list" ,method=RequestMethod.GET)
	@ValidHostId
	public String hostList(@RequestParam(value="hostId",required=false) Long hostId, Model model , HttpSession httpSession ) {
		List<Rating> ratings = new ArrayList<Rating>();
		Host h = hostDao.getOne(hostId);
		for(Place place:h.getRentingPlaces()) {
			for(Reservation reservation:place.getReservations()) {
				Rating temp = reservation.getHostRating();
				if(temp != null)
					ratings.add(temp);
			}
		}
		return "rating/list";
	}	
	
	@RequestMapping(value = "guest/rating/list" ,method=RequestMethod.GET)
	@ValidGuestId
	public String guestList(@RequestParam(value="guestId",required=false) Long guestId, Model model , HttpSession httpSession ) {
		List<Rating> ratings = new ArrayList<Rating>();
		Guest g = guestDao.getOne(guestId);
		for(Reservation reservation:g.getReservations()) {
			Rating temp = reservation.getGuestRating();
			if(temp != null)
				ratings.add(temp);
		}
		return "rating/list";
	}	
	
	@RequestMapping(value = "place/rating/list" ,method=RequestMethod.GET)
	@ValidPlaceId
	public String placeList(@RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ) {
		List<Rating> ratings = new ArrayList<Rating>();
		Place place = placeDao.getOne(placeId);
		for(Reservation reservation:place.getReservations()) {
			Rating temp = reservation.getPlaceRating();
			if(temp != null)
				ratings.add(temp);
		}
		return "rating/list";
	}	
	
	//rating create
	
	@RequestMapping(value = "/rating/create" ,method=RequestMethod.POST)
	@ValidReservationId
	@ReservationAssociatedUserLoginRequired
	public String createRating(@RequestParam(value="reservationId",required=false) Long reservationId,Rating rating, Model model , HttpSession httpSession ) {
		try {
			rating =
				ratingManager.addRating(
					timeAdvancementManagement.getCurrentDate(),
					reservationId,
					rating,
					sessionManager.getHost(httpSession) != null
					);
			return ControllerHelper.popupMessageAndRedirect("Rating Successfully Created", "reservation/view?reservationId="+reservationId);
		} catch (Exception e) {
			exceptionManager.reportException(e);
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "reservation/view?reservationId="+reservationId);
		}
		
	}
	
	//rating read
	
	//rating update
	
	//rating delete
	
}
