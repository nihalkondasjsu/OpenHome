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
import com.openhome.dao.PlaceDAO;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.data.PlaceDetails;
import com.openhome.data.PlaceSearchQuery;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/place/searchresults")
public class PlaceSearchController {

	@Autowired
	PlaceDAO placeDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String viewForm( @RequestParam(value="placeId",required=false) Long placeId , @RequestParam(value="preview",required=false) Boolean preview , Model model , HttpSession httpSession ) {
		System.out.println("PlaceViewController");
		model.addAttribute("placeSearchQuery", new PlaceSearchQuery());
		model.addAttribute("places", placeDao.findAll());
		return "place/searchresults";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String searchQuery(Model model,PlaceSearchQuery placeSearchQuery) {
		
		System.out.println(placeSearchQuery);
		
		List<Place> places ;
		
		try {
			if(placeSearchQuery.isCityQuery()) {
				places = placeDao.getPlacesByCityAndDatesAndPrice(placeSearchQuery.getCityOrZip(), placeSearchQuery.getReservationStartDateTimeObj().getTime(), placeSearchQuery.getReservationEndDateTimeObj().getTime(), placeSearchQuery.getMinPrice(), placeSearchQuery.getMaxPrice(), placeSearchQuery.getWeekDays());
			}else {
				places = placeDao.getPlacesByZipAndDatesAndPrice(placeSearchQuery.getCityOrZip(), placeSearchQuery.getReservationStartDateTimeObj().getTime(), placeSearchQuery.getReservationEndDateTimeObj().getTime(), placeSearchQuery.getMinPrice(), placeSearchQuery.getMaxPrice(), placeSearchQuery.getWeekDays());	
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "index";
		}

		List<Place> aList = new ArrayList<Place>();
		List<Place> bList = new ArrayList<Place>();
		
		for (Place place : places) {
			boolean temp = placeSearchQuery.suitableMatch(place.getPlaceDetails());
			place.setBestSuitedSearchResult(temp);
			if(temp)
				aList.add(place);
			else
				bList.add(place);
		}
		
		aList.addAll(bList);
		
		System.out.println(aList);
		
		model.addAttribute("placeSearchQuery", placeSearchQuery);
		
		model.addAttribute("places", aList);
		
		return "place/searchresults";
	}
	
}
