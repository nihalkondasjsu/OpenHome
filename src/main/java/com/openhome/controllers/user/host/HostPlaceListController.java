package com.openhome.controllers.user.host;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.aop.helper.annotation.HostLoginRequired;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/host/place/list")
public class HostPlaceListController {

	@Autowired(required=true)
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@HostLoginRequired
	public String getReservationCreatePage( Model model , HttpSession httpSession ) {
		model.addAttribute("places", sessionManager.getHost(httpSession).getRentingPlaces());
		return "place/list";
	}
	
}
