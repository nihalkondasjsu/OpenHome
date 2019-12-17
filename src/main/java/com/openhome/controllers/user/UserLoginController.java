package com.openhome.controllers.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.Json;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.HostDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.UserDetails;
import com.openhome.data.helper.UserManager;
import com.openhome.exception.ExceptionManager;
import com.openhome.security.Encryption;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/{userRole}/login")
public class UserLoginController {

	@Autowired(required=true)
	SessionManager sessionManager;

	@Autowired(required=true)
	UserManager userManager;
	
	@Autowired(required=true)
	ExceptionManager exceptionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String loginForm( @PathVariable("userRole") String userRole, Model model, HttpSession httpSession ) {
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
			if(userRole.equals("host")) {
				sessionManager.setHost(httpSession, userManager.loginHost(userDetails).getId());
			}else {
				sessionManager.setGuest(httpSession, userManager.loginGuest(userDetails).getId());
			}

			return ControllerHelper.popupMessageAndRedirect("Successful Login", userRole+"/dashboard");

		} catch (Exception e) {
			exceptionManager.reportException(e);
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), userRole+"/login");
		}
	}
	
}
