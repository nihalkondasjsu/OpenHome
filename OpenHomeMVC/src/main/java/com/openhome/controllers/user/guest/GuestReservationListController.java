package com.openhome.controllers.user.guest;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.aop.helper.annotation.GuestLoginRequired;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/guest/reservation/list")
public class GuestReservationListController {
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@GuestLoginRequired
	public String getReservationCreatePage( Model model , HttpSession httpSession ) {
		model.addAttribute("reservations", sessionManager.getGuest(httpSession).getReservations());
		return "reservation/list";
	}
	
	
}
