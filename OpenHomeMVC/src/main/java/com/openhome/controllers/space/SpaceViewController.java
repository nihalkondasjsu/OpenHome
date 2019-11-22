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
import com.openhome.data.Host;
import com.openhome.data.Space;
import com.openhome.data.SpaceDetails;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/space/view")
public class SpaceViewController {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String viewForm( @RequestParam(value="spaceId",required=false) Long spaceId , Model model , HttpSession httpSession ) {
		System.out.println("SpaceViewController");
		
		if(spaceId == null) {
			model.addAttribute("op", "view");
			model.addAttribute("spaces", spaceDao.findAll());
			return "space/viewall";
		}
		
		Space s = null;
		
		try {
			s = spaceDao.getOne(spaceId);
		} catch (Exception e) {
			
		}
		
		model.addAttribute("space", s == null ? new Space() : s);
		
		model.addAttribute("hostAccess", false);
		
		if( s == null ) {
			return "space/view";
		}
		
		
		try {
			Host h = sessionManager.getHost(httpSession);
			if(s.getHost().getId() == h.getId()) {
				model.addAttribute("hostAccess", true);
			}
		} catch (Exception e) {
			
		}
		
		return "space/view";
	}
	
}
