package com.openhome.controllers.space;

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
import com.openhome.dao.SpaceDAO;
import com.openhome.data.Host;
import com.openhome.data.Space;
import com.openhome.data.SpaceDetails;
import com.openhome.data.SpaceSearchQuery;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/space/searchresults")
public class SpaceSearchController {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String viewForm( @RequestParam(value="spaceId",required=false) Long spaceId , @RequestParam(value="preview",required=false) Boolean preview , Model model , HttpSession httpSession ) {
		System.out.println("SpaceViewController");
		model.addAttribute("spaceSearchQuery", new SpaceSearchQuery());
		model.addAttribute("spaces", spaceDao.findAll());
		return "space/searchresults";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String searchQuery(Model model,SpaceSearchQuery spaceSearchQuery) {
		
		System.out.println(spaceSearchQuery);
		
		List<Space> spaces ;
		
		try {
			if(spaceSearchQuery.isCityQuery()) {
				spaces = spaceDao.getSpacesByCityAndDatesAndPrice(spaceSearchQuery.getCityOrZip(), spaceSearchQuery.getBookingStartDateTimeObj().getTime(), spaceSearchQuery.getBookingEndDateTimeObj().getTime(), spaceSearchQuery.getMinPrice(), spaceSearchQuery.getMaxPrice(), spaceSearchQuery.getWeekDays());
			}else {
				spaces = spaceDao.getSpacesByZipAndDatesAndPrice(spaceSearchQuery.getCityOrZip(), spaceSearchQuery.getBookingStartDateTimeObj().getTime(), spaceSearchQuery.getBookingEndDateTimeObj().getTime(), spaceSearchQuery.getMinPrice(), spaceSearchQuery.getMaxPrice(), spaceSearchQuery.getWeekDays());	
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "index";
		}

		List<Space> aList = new ArrayList<Space>();
		List<Space> bList = new ArrayList<Space>();
		
		for (Space space : spaces) {
			boolean temp = spaceSearchQuery.suitableMatch(space.getSpaceDetails());
			space.setBestSuitedSearchResult(temp);
			if(temp)
				aList.add(space);
			else
				bList.add(space);
		}
		
		aList.addAll(bList);
		
		System.out.println(aList);
		
		model.addAttribute("spaceSearchQuery", spaceSearchQuery);
		
		model.addAttribute("spaces", aList);
		
		return "space/searchresults";
	}
	
}
