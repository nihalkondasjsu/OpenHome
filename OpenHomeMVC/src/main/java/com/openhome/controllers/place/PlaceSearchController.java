package com.openhome.controllers.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.Json;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.PlaceDAO;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.data.PlaceDetails;
import com.openhome.data.PlaceSearchQuery;
import com.openhome.data.helper.PlaceManager;
import com.openhome.exception.ExceptionManager;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/place/searchresults")
public class PlaceSearchController {

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;

	@Autowired(required=true)
	PlaceManager placeManager;
	
	@Autowired(required=true)
	ExceptionManager exceptionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String viewForm( Model model , HttpSession httpSession ) {
		System.out.println("PlaceViewController");
		model.addAttribute("placeSearchQuery", new PlaceSearchQuery());
		model.addAttribute("places", placeManager.listAllPlaces());
		return "place/searchresults";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String searchQuery(Model model, HttpSession httpSession ,PlaceSearchQuery placeSearchQuery) {
		try {
			model.addAttribute("placeSearchQuery", placeSearchQuery);
			model.addAttribute("places", placeManager.searchPlaces(placeSearchQuery));
			return "place/searchresults";
		} catch (Exception e) {
			exceptionManager.reportException(e);
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
	}
	
}
