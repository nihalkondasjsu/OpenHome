package com.openhome.controllers.place;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.aop.helper.annotation.ValidPlaceId;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.controllers.helper.Mail;
import com.openhome.OpenHomeMvcApplication;
import com.openhome.aop.helper.annotation.PlaceHostLoginRequired;
import com.openhome.aop.helper.annotation.ValidAlivePlaceId;
import com.openhome.dao.PlaceDAO;
import com.openhome.data.Reservation;
import com.openhome.data.helper.PlaceManager;
import com.openhome.exception.CustomException;
import com.openhome.exception.ExceptionManager;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/place/delete")
public class PlaceDeleteController {

	@Autowired(required=true)
	PlaceManager placeManager;

	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@Autowired(required=true)
	ExceptionManager exceptionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidAlivePlaceId
	@PlaceHostLoginRequired
	public String deleteForm( @RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ) {
		System.out.println("PlaceDeleteController");
		
		try {
			Place s = placeManager.deletePlace(timeAdvancementManagement.getCurrentDate(), placeId);
			return ControllerHelper.popupMessageAndRedirect("Deleted Place Successfully.", "/place/view?placeId="+s.getId(),new Mail(
					s.getHost().getUserDetails().getEmail(),
					"OpenHome: Place Deleted Successfully",
					"Link to your deleted place : "+OpenHomeMvcApplication.baseUrl+"/place/view?placeId="+s.getId()
					));
		} catch (Exception e) {
			exceptionManager.reportException(e);
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "/place/view?placeId="+placeId);
		}

		
	}
	
}
