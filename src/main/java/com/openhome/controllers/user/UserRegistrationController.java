package com.openhome.controllers.user;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.openhome.Json;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.controllers.helper.Mail;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.UserDetails;
import com.openhome.data.helper.UserManager;
import com.openhome.exception.ExceptionManager;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/{userRole}/register")
public class UserRegistrationController {
	
	@Autowired(required=true)
	SessionManager sessionManager;

	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@Autowired(required=true)
	UserManager userManager;
	
	@Autowired(required=true)
	ExceptionManager exceptionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String registerForm( @PathVariable("userRole") String userRole,  Model model ) {
		System.out.println("RegistrationController");
		
		if(userRole.equals("host")==false)
			userRole = "guest";
		
		return userRole+"/register";
	}

	@RequestMapping(method=RequestMethod.POST)
	public String registerFormSubmission( @PathVariable("userRole") String userRole,  UserDetails userDetails , Model model , HttpSession httpSession , @RequestParam(value="image",required=false) MultipartFile imageFile, @RequestParam(value="imageUrl",required=false) String imageUrl) {
		Json.printObject(userDetails);
		
		final Date currentDate = timeAdvancementManagement.getCurrentDate();
		
		if(userRole.equals("host")==false)
			userRole = "guest";
		
		if(userDetails.getEmail().endsWith("@sjsu.edu")) {
			userRole = "host";
		}else {
			userRole = "guest";
		}
		
		try {
			
			if(userRole.equals("host")) {
				Host h = userManager.registerHost(currentDate, userDetails, imageFile, imageUrl);
				sessionManager.setHost(httpSession, h.getId());
			}else {
				Guest g = userManager.registerGuest(currentDate, userDetails, imageFile, imageUrl);
				sessionManager.setGuest(httpSession, g.getId());
			}

			return ControllerHelper.popupMessageAndRedirect("Successfully Registered.", userRole+"/dashboard",new Mail(userDetails.getEmail(),"OpenHome: User Created Successfully.","User created as "+userRole));
		} catch (Exception e) {
			exceptionManager.reportException(e);
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), userRole+"/register");
		}
		
	}
	
}
