package com.openhome.controllers.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.Json;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.UserDetails;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/{userRole}/dashboard")
public class UserDashboardController {

	@Autowired
	GuestDAO guestDao;
	
	@Autowired
	UserDetailsDAO userDetailsDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String registerForm(@PathVariable("userRole") String userRole, Model model , HttpSession httpSession ) {
		System.out.println("DashboardController");
		
		if(userRole.equals("host")==false)
			userRole = "guest";
		
		if(userRole.equals("host")) {
			Host h = sessionManager.getHost(httpSession);
			model.addAttribute(userRole, h == null ? new Host() : h);
		}else {
			Guest g = sessionManager.getGuest(httpSession);
			model.addAttribute(userRole, g == null ? new Guest() : g);
		}
		
		return userRole+"/dashboard";
	}
	
}
