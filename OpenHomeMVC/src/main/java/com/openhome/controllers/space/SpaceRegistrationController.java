package com.openhome.controllers.space;

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
import com.openhome.dao.HostDAO;
import com.openhome.dao.SpaceDAO;
import com.openhome.dao.SpaceDetailsDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Host;
import com.openhome.data.Image;
import com.openhome.data.Space;
import com.openhome.data.SpaceDetails;
import com.openhome.data.UserDetails;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/space/register")
public class SpaceRegistrationController {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	SessionManager sessionManager;

	@Autowired
	FileSystem fileSystem;

	@Autowired
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@HostLoginRequired
	public String registerForm( Model model ) {
		System.out.println("SpaceRegistrationController");
		return "space/register";
	}

	@RequestMapping(method=RequestMethod.POST)
	@HostLoginRequired
	public String registerFormSubmission( SpaceDetails spaceDetails , Model model , HttpSession httpSession , @RequestParam(value="image",required=false) MultipartFile image, @RequestParam(value="imageUrl",required=false) String imageUrl) {
		try {
			Json.printObject(spaceDetails);
			
			Host host = sessionManager.getHost(httpSession);
			
			Space space = new Space();
			
			spaceDetails.prepareForRegistration();
			
			Image imageObj = null;
			if(image == null) {
				if(imageUrl == null) {
					System.out.println("No Image Provided");
				}else
				imageObj = fileSystem.saveImage(imageUrl);
			}else {
				if(image.getSize()<1000) {
					System.out.println("No Image Provided");
				}else
				imageObj = fileSystem.saveImage(image);
			}
			
			if(imageObj == null) {
				System.out.println("Image Provided");
				throw new IllegalArgumentException("No Image Provided");
			}
			
			spaceDetails.addImage(imageObj);
			
			space.setSpaceDetails(spaceDetails);
			
			host.addSpace(space);
			
			space.setHost(host);
			
			space = spaceDao.save(space);
			
			model.addAttribute("successLink", "space/view?spaceId="+space.getId());
			
			return "redirect";
			
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.toString());
		}
		
		return "space/register";
	}
	
}
