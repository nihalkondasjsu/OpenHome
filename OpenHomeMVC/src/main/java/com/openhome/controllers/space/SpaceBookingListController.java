package com.openhome.controllers.space;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.aop.helper.annotation.SpaceHostLoginRequired;
import com.openhome.aop.helper.annotation.ValidSpaceId;
import com.openhome.dao.SpaceDAO;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/space/booking/list")
public class SpaceBookingListController {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidSpaceId
	@SpaceHostLoginRequired
	public String bookingList(@RequestParam(value="spaceId",required=false) Long spaceId, Model model , HttpSession httpSession ) {
		model.addAttribute("bookings", spaceDao.getOne(spaceId).getBookings());
		return "booking/list";
	}
	
}
