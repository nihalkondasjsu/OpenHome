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
import com.openhome.dao.HostDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.UserDetails;
import com.openhome.security.Encryption;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/{userRole}/login")
public class UserLoginController {

	@Autowired
	GuestDAO guestDao;
	
	@Autowired
	HostDAO hostDao;
	
	@Autowired
	UserDetailsDAO userDetailsDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String loginForm( @PathVariable("userRole") String userRole, Model model ) {
		System.out.println("LoginController");
		
		if(userRole.equals("host")==false)
			userRole = "guest";
		
		return userRole+"/login";
	}

	@RequestMapping(method=RequestMethod.POST)
	public String loginFormSubmission( @PathVariable("userRole") String userRole, UserDetails userDetails , Model model , HttpSession httpSession ) {
		Json.printObject(userDetails);
		
		if(userRole.equals("host")==false)
			userRole = "guest";
		
		try {
			UserDetails userDetailsDB = null;
			Long userId = null;
			try {
				Host host = hostDao.findHostByEmail(userDetails.getEmail());
				Guest guest = guestDao.findGuestByEmail(userDetails.getEmail());
				if(host != null) {
					userDetailsDB = host.getUserDetails();
					userId = host.getId();
					userRole = "host";
				}
				if(guest != null) {
					userDetailsDB = guest.getUserDetails();
					userId = guest.getId();
					userRole = "guest";
				}
			} catch (Exception e) {
				
			}
			
			Json.printObject(userDetailsDB);
			
			if(userDetailsDB == null) {
				model.addAttribute("errorMessage", "Invalid Credentials.");
			} else {
				if(userDetailsDB.checkPassword(userDetails.getPassword())) {
					sessionManager.setGuest(httpSession, userId);
					model.addAttribute("successLink", userRole+"/dashboard");
					return "redirect";
				}else {
					model.addAttribute("errorMessage", "Invalid Credentials.");
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		
		return userRole+"/login";
	}
	
}
