package com.openhome.controllers.place;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.Json;
import com.openhome.OpenHomeMvcApplication;
import com.openhome.aop.helper.annotation.ValidPlaceId;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.controllers.helper.Mail;
import com.openhome.aop.helper.annotation.PlaceHostLoginRequired;
import com.openhome.aop.helper.annotation.ValidAlivePlaceId;
import com.openhome.dao.PlaceDAO;
import com.openhome.dao.PlaceDetailsDAO;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.data.PlaceDetails;
import com.openhome.data.helper.PlaceManager;
import com.openhome.exception.ExceptionManager;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/place/update")
public class PlaceUpdateController {

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;

	@Autowired(required=true)
	PlaceManager placeManager;
	
	@Autowired(required=true)
	ExceptionManager exceptionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidAlivePlaceId
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
	@ValidAlivePlaceId
	@PlaceHostLoginRequired
	public String updateFormSubmission(@RequestParam(value="placeId",required=false) Long placeId, PlaceDetails placeDetails , Model model , HttpSession httpSession ) {
		System.out.println("HaPPY");
		try {
			Place p = placeManager.updatePlace(placeId,placeDetails);
			
			return ControllerHelper.popupMessageAndRedirect("Place Updated Successfully.", "place/view?placeId="+p.getId(),
					new Mail(
							p.getHost().getUserDetails().getEmail(),
							"OpenHome: Place Updated Successfully",
							"Link to your updated place : "+OpenHomeMvcApplication.baseUrl+"/place/view?placeId="+p.getId()
							));
		} catch (Exception e) {
			exceptionManager.reportException(e);
			return ControllerHelper.popupMessageAndRedirect(e.toString(), "place/update");
		}
		
	}
	
}
