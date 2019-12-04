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
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.aop.helper.annotation.PlaceHostLoginRequired;
import com.openhome.dao.PlaceDAO;
import com.openhome.dao.PlaceDetailsDAO;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.data.PlaceDetails;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/place/update")
public class PlaceUpdateController {

	@Autowired
	PlaceDAO placeDao;
	
	@Autowired
	PlaceDetailsDAO placeDetailsDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidPlaceId
	@PlaceHostLoginRequired
	public String updateForm(@RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ) {
		System.out.println("PlaceUpdateController");
		
		Place s = null;
		
		s = placeDao.getOne(placeId);
			
		Host h = sessionManager.getHost(httpSession);
		
		model.addAttribute("place", s);
			
		return "place/update";
		
	}

	@RequestMapping(method=RequestMethod.POST)
	@ValidPlaceId
	@PlaceHostLoginRequired
	public String updateFormSubmission(@RequestParam(value="placeId",required=false) Long placeId, PlaceDetails placeDetails , Model model , HttpSession httpSession ) {
		System.out.println("HaPPY");
		try {
			Place s = placeDao.getOne(placeId);
			model.addAttribute("place", s);
			
			Host host = sessionManager.getHost(httpSession);
			
			Place temp =null;
			
			try {
				temp = placeDao.getOne(placeDao.getPlaceByHostAndName(host.getId(), placeDetails.getName()));
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			
			if(temp.getPlaceDetails().getId().equals( s.getPlaceDetails().getId() ) == false) {
				return ControllerHelper.popupMessageAndRedirect("A place with this name is already existing in your account.", "place/update");
			}
			
			placeDetails.setRegisteredDate(s.getPlaceDetails().getRegisteredDate());
			
			placeDetails.setImages(s.getPlaceDetails().getImages());
			
			placeDetails.setAddress(s.getPlaceDetails().getAddress());
			
			placeDetails.setId(s.getPlaceDetails().getId());
			
			placeDetailsDao.save(placeDetails);
			
			model.addAttribute("successLink", "place/view?placeId="+s.getId());
			
			return ControllerHelper.popupMessageAndRedirect("Place Updated Successfully.", "place/view?placeId="+s.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.toString(), "place/update");
		}
		
	}
	
}
