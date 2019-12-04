package com.openhome.controllers.space;

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

import com.openhome.aop.helper.annotation.ValidSpaceId;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.aop.helper.annotation.SpaceHostLoginRequired;
import com.openhome.dao.SpaceDAO;
import com.openhome.data.Booking;
import com.openhome.data.Host;
import com.openhome.data.Space;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/space/delete")
public class SpaceDeleteController {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	SessionManager sessionManager;

	@Autowired
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidSpaceId
	@SpaceHostLoginRequired
	public String deleteForm( @RequestParam(value="spaceId",required=false) Long spaceId, Model model , HttpSession httpSession ) {
		System.out.println("SpaceDeleteController");
		
		model.addAttribute("successLink", "");

		Space s = spaceDao.getOne(spaceId);
				
		Host h = sessionManager.getHost(httpSession);
		
		Date current = timeAdvancementManagement.getCurrentDate();
		
		List<Booking> bookings = s.getBookings();
		
		for (Booking booking : bookings) {
			if(booking.dateOfCheckOut().after(current)) {
				model.addAttribute("Message", "Cannot delete Space.Space has future booking.");
				return ControllerHelper.popupMessageAndRedirect("Cannot delete Space.Space has future booking.", "/space/view?spaceId="+spaceId);
			}
		}
		
		spaceDao.deleteById(spaceId);
		
		model.addAttribute("Message", "Deleted Space Successfully.");

		return ControllerHelper.popupMessageAndRedirect("Deleted Space Successfully.", "/host/dashboard");
	}
	
}
