package com.openhome.controllers.reservation;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.aop.helper.annotation.ReservationAssociatedUserLoginRequired;
import com.openhome.aop.helper.annotation.ValidReservationId;
import com.openhome.dao.ReservationDAO;
import com.openhome.session.SessionManager;

@Controller
@RequestMapping("/reservation/view")
public class ReservationViewController {

	@Autowired(required=true)
	ReservationDAO reservationDao;

	@Autowired(required=true)
	SessionManager sessionManager;
	
	@RequestMapping(method=RequestMethod.GET)
	@ValidReservationId
	@ReservationAssociatedUserLoginRequired
	public String getReservationCreatePage(@RequestParam(value="reservationId",required=false) Long reservationId, Model model , HttpSession httpSession ) {
		model.addAttribute("reservation", reservationDao.getOne(reservationId));
		model.addAttribute("hostAccess", sessionManager.getHostId(httpSession) != null );
		model.addAttribute("guestAccess", sessionManager.getGuestId(httpSession) != null );
		System.out.println(model.getAttribute("hostAccess") +" | "+model.getAttribute("guestAccess"));
		return "reservation/view";
	}
	
}
