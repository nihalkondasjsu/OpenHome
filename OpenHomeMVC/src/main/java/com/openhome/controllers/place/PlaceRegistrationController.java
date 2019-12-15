package com.openhome.controllers.place;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.openhome.FileSystem;
import com.openhome.Json;
import com.openhome.OpenHomeMvcApplication;
import com.openhome.aop.helper.annotation.HostLoginRequired;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.controllers.helper.Mail;
import com.openhome.dao.PlaceDAO;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.data.PlaceDetails;
import com.openhome.data.helper.PlaceManager;
import com.openhome.exception.ExceptionManager;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/place/register")
public class PlaceRegistrationController {
	
	@Autowired(required=true)
	SessionManager sessionManager;

	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@Autowired(required=true)
	PlaceManager placeManager;
	
	@Autowired(required=true)
	ExceptionManager exceptionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@HostLoginRequired
	public String registerForm( HttpSession httpSession , Model model ) {
		System.out.println("PlaceRegistrationController");
		return "place/register";
	}

	@RequestMapping(method=RequestMethod.POST)
	@HostLoginRequired
	public String registerFormSubmission( PlaceDetails placeDetails , Model model , HttpSession httpSession , @RequestParam(value="image",required=false) MultipartFile image, @RequestParam(value="imageUrl",required=false) String imageUrl) {
		try {
			Json.printObject(placeDetails);
			
			Host host = sessionManager.getHost(httpSession);
			
			Place place = placeManager.registerPlace(timeAdvancementManagement.getCurrentDate(), host, placeDetails, image, imageUrl);
			
			return ControllerHelper.popupMessageAndRedirect(
					"Place Registered Successfully",
					"place/view?placeId="+place.getId(),
					new Mail(
							host.getUserDetails().getEmail(),
							"OpenHome: Place Created Successfully",
							"Link to your new place : "+OpenHomeMvcApplication.baseUrl+"/place/view?placeId="+place.getId()
							)
					);
		} catch (Exception e) {
			exceptionManager.reportException(e);
			return ControllerHelper.popupMessageAndRedirect(e.toString(), "place/register");
		}
		
	}
	
}
