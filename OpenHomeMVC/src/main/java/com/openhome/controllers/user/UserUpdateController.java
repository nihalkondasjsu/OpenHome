package com.openhome.controllers.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.Json;
import com.openhome.dao.HostDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.UserDetails;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/{userRole}/update")
public class UserUpdateController {

	@Autowired
	HostDAO hostDao;
	
	@Autowired
	UserDetailsDAO userDetailsDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String updateForm( @PathVariable("userRole") String userRole, Model model , HttpSession httpSession ) {
		System.out.println("UpdateController");
		
		if(userRole.equals("host")==false)
			userRole = "guest";
		
		if(userRole.equals("host")) {
			Host h = sessionManager.getHost(httpSession);
			model.addAttribute(userRole, h == null ? new Host() : h);
		}else {
			Guest g = sessionManager.getGuest(httpSession);
			model.addAttribute(userRole, g == null ? new Guest() : g);
		}
		
		return userRole+"/update";
	}

	@RequestMapping(method=RequestMethod.POST)
	public String updateFormSubmission(@PathVariable("userRole") String userRole, UserDetails userDetails , Model model , HttpSession httpSession ) {
		Json.printObject(userDetails);
		
		try {
			
			UserDetails userDetailsDB = userDetailsDao.getUserByEmail(userDetails.getEmail());
		
			try {
				userDetails.updateDetails(userDetailsDB.getEmail(), userDetailsDB.getPassword(),userDetailsDB.getVerifiedDetails());
			} catch (Exception e) {
				model.addAttribute("errorMessage", "Invalid Credentials.");
				return "host/update";
			}
			
			userDetails.setId(userDetailsDB.getId());
			
			userDetailsDao.save(userDetails);
			
			model.addAttribute("successLink", userRole+"/dashboard");
			
			return "redirect";
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return updateForm(userRole, model, httpSession);
	}
	
}
