package com.openhome.controllers.space;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.Json;
import com.openhome.dao.SpaceDAO;
import com.openhome.dao.SpaceDetailsDAO;
import com.openhome.data.Host;
import com.openhome.data.Space;
import com.openhome.data.SpaceDetails;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/space/update")
public class SpaceUpdateController {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	SpaceDetailsDAO spaceDetailsDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String updateForm(@RequestParam(value="spaceId",required=false) Long spaceId, Model model , HttpSession httpSession ) {
		System.out.println("SpaceUpdateController");
		Space s = null;
		
		try {
			
			if(spaceId == null)
				throw new IllegalArgumentException("No SpaceId provided.");
			
			s = spaceDao.getOne(spaceId);
			
			if( s == null )
				throw new IllegalArgumentException("Invalid SpaceId provided.");
			
			Host h = sessionManager.getHost(httpSession);
			
			if( h == null )
				throw new IllegalArgumentException("No Host Login found.");
			
			if(s.getHost().getId() != h.getId())
				throw new IllegalArgumentException("Wrong Host Login found.");

			model.addAttribute("space", s);
			
		} catch (IllegalArgumentException e ) {
			model.addAttribute("errorMessage", e.getMessage());
			return "space/viewall";
		}
		
		return "space/update";
		
	}

	@RequestMapping(method=RequestMethod.POST)
	public String updateFormSubmission(@RequestParam(value="spaceId",required=false) Long spaceId, SpaceDetails spaceDetails , Model model , HttpSession httpSession ) {
		System.out.println("HaPPY");
		try {
			Space s = spaceDao.getOne(spaceId);
			model.addAttribute("space", s);
			
			if(spaceId == null) {
				model.addAttribute("errorMessage", "No SpaceId provided.");
				return "space/update";
			}
			
			Json.printObject(spaceDetails);
			
			Host host = sessionManager.getHost(httpSession);
			
			
			if(host == null) {
				model.addAttribute("errorMessage", "No Host Login found in session.");
				return "space/update";
			}
			
			if(s.getId() != spaceId) {
				System.out.println("Crazy" );
			}
			
			System.out.println(s.getId()+" spaceId "+spaceId );
			
			Json.printObject(s);
			
			if(s == null || s.getHost().getId() != host.getId()) {
				model.addAttribute("errorMessage", "Invalid SpaceDetails Id.");
				return "space/update";
			}
			
			Space temp =null;
			
			try {
				temp = spaceDao.getOne(spaceDao.getSpaceByHostAndName(host.getId(), spaceDetails.getName()));
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.toString());
			}
			
			System.out.println(temp+" sdid "+s.getSpaceDetails().getId());
			
			if(temp.getSpaceDetails().getId() != s.getSpaceDetails().getId()) {
				model.addAttribute("errorMessage", "A space with this name is already existing in your account.");
				return "space/update";
			}
			
			spaceDetails.getAddress().setId(s.getSpaceDetails().getAddress().getId());
			
			spaceDetails.setId(s.getSpaceDetails().getId());
			
			//spaceDetails.setAddress(s.getSpaceDetails().getAddress());
			
			s.setSpaceDetails(spaceDetails);
			
			//spaceDetailsDao.save(spaceDetails);
			
			spaceDao.save(s);
			
			model.addAttribute("successLink", "space/view?spaceId="+s.getId());
			
			return "redirect";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", e.toString());
		}
		
		return "space/update";
	}
	
}
