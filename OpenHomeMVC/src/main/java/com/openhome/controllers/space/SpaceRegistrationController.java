package com.openhome.controllers.space;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhome.Json;
import com.openhome.dao.HostDAO;
import com.openhome.dao.SpaceDAO;
import com.openhome.dao.SpaceDetailsDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Host;
import com.openhome.data.Space;
import com.openhome.data.SpaceDetails;
import com.openhome.data.UserDetails;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/space/register")
public class SpaceRegistrationController {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String registerForm( Model model ) {
		System.out.println("SpaceRegistrationController");
		return "space/register";
	}

	@RequestMapping(method=RequestMethod.POST)
	public String registerFormSubmission( SpaceDetails spaceDetails , Model model , HttpSession httpSession ) {
		try {
			Json.printObject(spaceDetails);
			
			Host host = sessionManager.getHost(httpSession);
			
			if(host == null) {
				model.addAttribute("errorMessage", "No Host Login found in session.");
				return "space/register";
			}
			
			if(host.getUserDetails().verifiedEmail() == false) {
				model.addAttribute("errorMessage", "Host is unverified.");
				return "space/register";
			}
			
			if(spaceDao.getSpaceByHostAndName(host.getId(), spaceDetails.getName()) != null) {
				model.addAttribute("errorMessage", "A space with this name is already existing in your account.");
				return "space/register";
			}
			
			Space space = new Space();
			
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
