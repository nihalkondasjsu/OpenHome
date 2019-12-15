package com.openhome.controllers.place;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.aop.helper.annotation.PlaceHostLoginRequired;
import com.openhome.aop.helper.annotation.ValidPlaceId;
import com.openhome.dao.PlaceDAO;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/place/reservation/list")
public class PlaceReservationListController {

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidPlaceId
	@PlaceHostLoginRequired
	public String reservationList(@RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ) {
		model.addAttribute("reservations", placeDao.getOne(placeId).getReservations());
		return "reservation/list";
	}
	
}
