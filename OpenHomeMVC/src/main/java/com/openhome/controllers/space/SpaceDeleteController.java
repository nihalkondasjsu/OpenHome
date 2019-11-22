package com.openhome.controllers.space;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.dao.SpaceDAO;
import com.openhome.data.Booking;
import com.openhome.data.Host;
import com.openhome.data.Space;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/space/delete")
public class SpaceDeleteController {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String deleteForm( @RequestParam(value="spaceId",required=false) Long spaceId, Model model , HttpSession httpSession ) {
		System.out.println("SpaceDeleteController");
		
		model.addAttribute("successLink", "");
		
		if(spaceId == null) {
			model.addAttribute("Message", "Invalid Space Id.");
			return "redirect";
		}
		
		Space s = spaceDao.getOne(spaceId);
		
		if(s == null) {
			model.addAttribute("Message", "Invalid Space Id.");
			return "redirect";
		}
		
		Host h = sessionManager.getHost(httpSession);
		
		if(h == null) {
			model.addAttribute("Message", "Invalid Host Login.");
			return "redirect";
		}
		
		if(s.getHost().getId() != h.getId()) {
			model.addAttribute("Message", "Invalid Host Login.");
			return "redirect";
		}
		
		Date current = sessionManager.getSessionDate(httpSession);
		
		List<Booking> bookings = s.getBookings();
		
		for (Booking booking : bookings) {
			if(booking.dateOfCheckOut().after(current)) {
				model.addAttribute("Message", "Cannot delete Space.Space has future booking.");
				return "redirect";
			}
		}
		
		spaceDao.deleteById(spaceId);
		
		model.addAttribute("Message", "Deleted Space Successfully.");
		
		return "redirect";
	}
	
}
