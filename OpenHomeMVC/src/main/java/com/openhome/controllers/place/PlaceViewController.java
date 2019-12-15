package com.openhome.controllers.place;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.Json;
import com.openhome.aop.helper.annotation.ValidPlaceId;
import com.openhome.dao.PlaceDAO;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.data.PlaceDetails;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/place/view")
public class PlaceViewController {

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidPlaceId
	public String viewForm( @RequestParam(value="placeId",required=false) Long placeId , @RequestParam(value="preview",required=false) Boolean preview , Model model , HttpSession httpSession ) {
		System.out.println("PlaceViewController");
		
		Place s = placeDao.getOne(placeId);
		
		model.addAttribute("place", s );
		
		model.addAttribute("hostAccess", false);
		
		if(sessionManager.getHostId(httpSession) != null) {
			model.addAttribute("hostAccess", sessionManager.getHostId(httpSession).equals(s.getHost().getId())  );
		}
		
		return preview == null ? "place/view" : "place/preview";
	}
	
}
