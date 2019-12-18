package com.openhome.controllers.user.host;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.aop.helper.annotation.HostLoginRequired;
import com.openhome.data.Place;
import com.openhome.data.Reservation;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/host/reservation/list")
public class HostReservationListController {
	
	@Autowired(required=true)
	SessionManager sessionManager;
	
	@Autowired(required=true)
	TimeAdvancementManagement tam;
	
	@RequestMapping(method=RequestMethod.GET)
	@HostLoginRequired
	public String getReservationCreatePage( Model model , HttpSession httpSession ) {
		List<Place> places = sessionManager.getHost(httpSession).getRentingPlaces();
		
		List<Reservation> reservations = new ArrayList<Reservation>();
		
		Date date = tam.getCurrentDate();
		
		for(Place place:places) {
			for(Reservation reservation:place.getReservations()) {
				reservation.calculatePcf(date);
				reservations.add(reservation);
			}
		}
		model.addAttribute("reservations", reservations);
		return "reservation/list";
	}
	
	
}
