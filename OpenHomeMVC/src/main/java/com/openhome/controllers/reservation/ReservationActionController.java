package com.openhome.controllers.reservation;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.aop.helper.annotation.ReservationAssociatedGuestLoginRequired;
import com.openhome.aop.helper.annotation.ValidReservationId;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.ReservationDAO;
import com.openhome.data.Reservation;
import com.openhome.data.manager.ReservationManager;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
public class ReservationActionController {

	@Autowired
	ReservationDAO reservationDao;

	@Autowired
	ReservationManager reservationProcessor;
	
	@Autowired
	SessionManager sessionManager;
	
	@Autowired
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(value = "/reservation/checkIn" ,method=RequestMethod.GET)
	@ValidReservationId
	@ReservationAssociatedGuestLoginRequired
	public String guestCheckIn(@RequestParam(value="reservationId",required=false) Long reservationId, Model model , HttpSession httpSession ) {
		Reservation reservation = reservationDao.getOne(reservationId);
		try {
			reservationProcessor.setReservation(reservation);
			reservationProcessor.performGuestCheckIn(timeAdvancementManagement.getCurrentDate());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(),  "/reservation/view?reservationId="+reservationId);
		}
		reservationDao.save(reservation);
		return ControllerHelper.popupMessageAndRedirect("Check In Successful.", "/reservation/view?reservationId="+reservationId);
	}
	
}
