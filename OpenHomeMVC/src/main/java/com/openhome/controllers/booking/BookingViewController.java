package com.openhome.controllers.booking;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.aop.helper.annotation.BookingAssociatedUserLoginRequired;
import com.openhome.aop.helper.annotation.ValidBookingId;
import com.openhome.dao.BookingDAO;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/booking/view")
public class BookingViewController {

	@Autowired
	BookingDAO bookingDao;

	@Autowired
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidBookingId
	@BookingAssociatedUserLoginRequired
	public String getBookingCreatePage(@RequestParam(value="bookingId",required=false) Long bookingId, Model model , HttpSession httpSession ) {
		model.addAttribute("booking", bookingDao.getOne(bookingId));
		model.addAttribute("hostAccess", sessionManager.getHostId(httpSession) != null );
		return "booking/view";
	}
	
}
