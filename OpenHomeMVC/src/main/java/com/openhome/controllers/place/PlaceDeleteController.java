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
import com.openhome.aop.helper.annotation.PlaceHostLoginRequired;
import com.openhome.aop.helper.annotation.ValidAlivePlaceId;
import com.openhome.dao.PlaceDAO;
import com.openhome.data.Reservation;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/place/delete")
public class PlaceDeleteController {

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;

	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidAlivePlaceId
	@PlaceHostLoginRequired
	public String deleteForm( @RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ) {
		System.out.println("PlaceDeleteController");
		
		model.addAttribute("successLink", "");

		Place s = placeDao.getOne(placeId);
				
		Host h = sessionManager.getHost(httpSession);
		
		Date current = timeAdvancementManagement.getCurrentDate();
		
		List<Reservation> reservations = s.getReservations();
		
		for (Reservation reservation : reservations) {
			if(reservation.dateOfCheckOut().after(current)) {
				model.addAttribute("Message", "Cannot delete Place.Place has future reservation.");
				return ControllerHelper.popupMessageAndRedirect("Cannot delete Place.Place has future reservation.", "/place/view?placeId="+placeId);
			}
		}
		
		s.setPermanentlyUnavailable(true);
		
		placeDao.save(s);
		
		model.addAttribute("Message", "Deleted Place Successfully.");

		return ControllerHelper.popupMessageAndRedirect("Deleted Place Successfully.", "/host/dashboard");
	}
	
}
