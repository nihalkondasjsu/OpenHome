package com.openhome.controllers.user.guest;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.aop.helper.annotation.GuestLoginRequired;
import com.openhome.data.Reservation;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/guest/reservation/list")
public class GuestReservationListController {
	
	@Autowired(required=true)
	SessionManager sessionManager;
	
	@Autowired(required=true)
	TimeAdvancementManagement tam;
	
	@RequestMapping(method=RequestMethod.GET)
	@GuestLoginRequired
	public String getReservationCreatePage( Model model , HttpSession httpSession ) {
		List<Reservation> reservations = sessionManager.getGuest(httpSession).getReservations();
		Date date = tam.getCurrentDate();
		for (Reservation reservation : reservations) {
			reservation.calculatePcf(date);
		}
		model.addAttribute("reservations", reservations);
		return "reservation/list";
	}
	
	
}
