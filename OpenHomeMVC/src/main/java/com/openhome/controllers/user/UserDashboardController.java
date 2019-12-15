package com.openhome.controllers.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.Json;
import com.openhome.aop.helper.annotation.UserLoginRequired;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.UserDetails;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/{userRole}/dashboard")
public class UserDashboardController {

	@Autowired(required=true)
	GuestDAO guestDao;
	
	@Autowired(required=true)
	UserDetailsDAO userDetailsDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@UserLoginRequired
	public String registerForm(@PathVariable("userRole") String userRole, Model model , HttpSession httpSession ) {
		System.out.println("DashboardController");
		
		Guest g = sessionManager.getGuest(httpSession);
		
		if(g != null) {
			userRole = "guest";
			model.addAttribute(userRole, g );
		}else {
			Host h = sessionManager.getHost(httpSession);
			userRole = "host";
			model.addAttribute(userRole, h );
		}
		
		return userRole+"/dashboard";
	}
	
}
