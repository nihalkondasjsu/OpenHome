package com.openhome.controllers.space;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.Json;
import com.openhome.aop.helper.annotation.ValidSpaceId;
import com.openhome.aop.helper.annotation.SpaceHostLoginRequired;
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
	@ValidSpaceId
	@SpaceHostLoginRequired
	public String updateForm(@RequestParam(value="spaceId",required=false) Long spaceId, Model model , HttpSession httpSession ) {
		System.out.println("SpaceUpdateController");
		Space s = null;
		
		try {
			
			s = spaceDao.getOne(spaceId);
			
			Host h = sessionManager.getHost(httpSession);
			
			model.addAttribute("space", s);
			
		} catch (IllegalArgumentException e ) {
			model.addAttribute("errorMessage", e.getMessage());
			return "space/viewall";
		}
		
		return "space/update";
		
	}

	@RequestMapping(method=RequestMethod.POST)
	@ValidSpaceId
	@SpaceHostLoginRequired
	public String updateFormSubmission(@RequestParam(value="spaceId",required=false) Long spaceId, SpaceDetails spaceDetails , Model model , HttpSession httpSession ) {
		System.out.println("HaPPY");
		try {
			Space s = spaceDao.getOne(spaceId);
			model.addAttribute("space", s);
			
			Host host = sessionManager.getHost(httpSession);
			
			Space temp =null;
			
			try {
				temp = spaceDao.getOne(spaceDao.getSpaceByHostAndName(host.getId(), spaceDetails.getName()));
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			
			if(temp.getSpaceDetails().getId() != s.getSpaceDetails().getId()) {
				model.addAttribute("errorMessage", "A space with this name is already existing in your account.");
				return "space/update";
			}
			
			spaceDetails.setRegisteredDate(s.getSpaceDetails().getRegisteredDate());
			
			spaceDetails.setImages(s.getSpaceDetails().getImages());
			
			spaceDetails.setAddress(s.getSpaceDetails().getAddress());
			
			spaceDetails.setId(s.getSpaceDetails().getId());
			
			spaceDetailsDao.save(spaceDetails);
			
			model.addAttribute("successLink", "space/view?spaceId="+s.getId());
			
			return "redirect";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", e.toString());
		}
		
		return "space/update";
	}
	
}
