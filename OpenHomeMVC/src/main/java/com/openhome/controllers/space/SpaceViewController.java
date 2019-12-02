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
	@ValidSpaceId
	public String viewForm( @RequestParam(value="spaceId",required=false) Long spaceId , @RequestParam(value="preview",required=false) Boolean preview , Model model , HttpSession httpSession ) {
		System.out.println("SpaceViewController");
		
		Space s = spaceDao.getOne(spaceId);
		
		model.addAttribute("space", s );
		
		model.addAttribute("hostAccess", false);
		
		if(sessionManager.getHostId(httpSession) != null) {
			model.addAttribute("hostAccess", sessionManager.getHostId(httpSession) == s.getHost().getId());
		}
		
		return preview == null ? "space/view" : "space/preview";
	}
	
}
