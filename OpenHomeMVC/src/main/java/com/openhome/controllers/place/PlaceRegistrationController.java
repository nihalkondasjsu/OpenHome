package com.openhome.controllers.place;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhome.FileSystem;
import com.openhome.Json;
import com.openhome.aop.helper.annotation.HostLoginRequired;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.HostDAO;
import com.openhome.dao.PlaceDAO;
import com.openhome.dao.PlaceDetailsDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Host;
import com.openhome.data.Image;
import com.openhome.data.Place;
import com.openhome.data.PlaceDetails;
import com.openhome.data.UserDetails;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/place/register")
public class PlaceRegistrationController {

	@Autowired
	PlaceDAO placeDao;
	
	@Autowired
	SessionManager sessionManager;

	@Autowired
	FileSystem fileSystem;

	@Autowired
	TimeAdvancementManagement timeAdvancementManagement;
	
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
			
			Place place = new Place();
			
			placeDetails.prepareForRegistration();
			
			Image imageObj = null;
			if(image == null || image.getSize()<1000) {
				if(imageUrl == null || imageUrl.equals("")) {
					System.out.println("No Image Change");
				}else
					imageObj = fileSystem.saveImage(imageUrl);
			}else {
				imageObj = fileSystem.saveImage(image);
			}
			
			if(imageObj == null) {
				System.out.println("No Image Provided");
				throw new IllegalArgumentException("No Image Provided");
			}
			
			placeDetails.addImage(imageObj);
			
			place.setPlaceDetails(placeDetails);
			
			host.addPlace(place);
			
			place.setHost(host);
			
			place = placeDao.save(place);
			
			return ControllerHelper.popupMessageAndRedirect("Place Registered Successfully", "place/view?placeId="+place.getId());
			
		} catch (Exception e) {
			return ControllerHelper.popupMessageAndRedirect(e.toString(), "place/register");
		}
		
	}
	
}
