package com.openhome.controllers.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhome.FileSystem;
import com.openhome.Json;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.HostDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.Image;
import com.openhome.data.UserDetails;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/{userRole}/register")
public class UserRegistrationController {

	@Autowired
	HostDAO hostDao;
	
	@Autowired
	GuestDAO guestDao;
	
	@Autowired
	UserDetailsDAO userDetailsDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@Autowired
	FileSystem fileSystem;

	@Autowired
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	public String registerForm( @PathVariable("userRole") String userRole,  Model model ) {
		System.out.println("RegistrationController");
		
		if(userRole.equals("host")==false)
			userRole = "guest";
		
		return userRole+"/register";
	}

	@RequestMapping(method=RequestMethod.POST)
	public String registerFormSubmission( @PathVariable("userRole") String userRole,  UserDetails userDetails , Model model , HttpSession httpSession , @RequestParam(value="image",required=false) MultipartFile image, @RequestParam(value="imageUrl",required=false) String imageUrl) {
		Json.printObject(userDetails);
		
		if(userRole.equals("host")==false)
			userRole = "guest";
		
		if(userDetails.getEmail().endsWith("@sjsu.edu")) {
			userRole = "host";
		}else {
			userRole = "guest";
		}
		
		try {
			
			if(userDetails.getCreditCard() == null) {
				throw new IllegalArgumentException("Invalid Credit Card");
			}
			
			userDetails.getCreditCard().validateCard(timeAdvancementManagement.getCurrentDate());
			
			userDetails.prepareForRegistration(timeAdvancementManagement.getCurrentDate());
			
			UserDetails userDetailsDB = userDetailsDao.getUserByEmail(userDetails.getEmail());
			
			if(userDetailsDB == null) {
				
				Image imageObj = null;
				
				if(image == null || image.getSize()<1000) {
					if(imageUrl == null || imageUrl.equals("")) {
						System.out.println("No Image Change");
					}else
						imageObj = fileSystem.saveImage(imageUrl);
				}else {
					imageObj = fileSystem.saveImage(image);
				}
				
				if(imageObj != null) {
					System.out.println("Image Provided");
					userDetails.setDisplayPictureId(imageObj);
				}
				
				if(userRole.equals("host")) {
					Host h = new Host();
					h.setUserDetails(userDetails);
					hostDao.save(h);
					sessionManager.setHost(httpSession, h.getId());
				}else {
					Guest g = new Guest();
					g.setUserDetails(userDetails);
					guestDao.save(g);
					sessionManager.setGuest(httpSession, g.getId());
				}
				model.addAttribute("successLink", userRole+"/dashboard");

				return ControllerHelper.popupMessageAndRedirect("Successfully Registered.", userRole+"/dashboard");
			}else {
				return ControllerHelper.popupMessageAndRedirect("Email id being used by a existing User.", userRole+"/register");
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), userRole+"/register");
		}
		
	}
	
}
