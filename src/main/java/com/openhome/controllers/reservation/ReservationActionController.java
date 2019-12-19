package com.openhome.controllers.reservation;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.OpenHomeMvcApplication;
import com.openhome.aop.helper.annotation.ReservationAssociatedGuestLoginRequired;
import com.openhome.aop.helper.annotation.ReservationAssociatedUserLoginRequired;
import com.openhome.aop.helper.annotation.ValidReservationId;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.controllers.helper.Mail;
import com.openhome.dao.ReservationDAO;
import com.openhome.data.Reservation;
import com.openhome.data.helper.ReservationProcessor;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
public class ReservationActionController {

	@Autowired(required=true)
	ReservationDAO reservationDao;

	@Autowired(required=true)
	ReservationProcessor reservationProcessor;
	
	@Autowired(required=true)
	SessionManager sessionManager;
	
	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(value = "/reservation/checkIn" ,method=RequestMethod.GET)
	@ValidReservationId
	@ReservationAssociatedGuestLoginRequired
	public String guestCheckIn(@RequestParam(value="reservationId",required=false) Long reservationId, Model model , HttpSession httpSession ) {
		Reservation reservation = reservationDao.getOne(reservationId);
		try {
			reservationProcessor.setReservation(reservation);
			reservationProcessor.performGuestCheckIn(timeAdvancementManagement.getCurrentDate());
			reservationDao.save(reservation);
		} catch (Exception e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(),  "/reservation/view?reservationId="+reservationId);
		}
		return ControllerHelper.popupMessageAndRedirect("Check In Successful.", "/reservation/view?reservationId="+reservationId,
				new Mail(reservation.getPlace().getHost().getUserDetails().getEmail(),"OpenHome: Check In Successful '"+reservation.getPlace().getPlaceDetails().getName()+"'","Link to view reservation : "+OpenHomeMvcApplication.baseUrl+"/reservation/view?reservationId="+reservation.getId()),
				new Mail(reservation.getGuest().getUserDetails().getEmail(),"OpenHome: Check In Successful '"+reservation.getPlace().getPlaceDetails().getName()+"'","Link to view reservation : "+OpenHomeMvcApplication.baseUrl+"/reservation/view?reservationId="+reservation.getId())
				);
	}
	
	@RequestMapping(value = "/reservation/checkOut" ,method=RequestMethod.GET)
	@ValidReservationId
	@ReservationAssociatedGuestLoginRequired
	public String guestCheckOut(@RequestParam(value="reservationId",required=false) Long reservationId, Model model , HttpSession httpSession ) {
		Reservation reservation = reservationDao.getOne(reservationId);
		try {
			reservationProcessor.setReservation(reservation);
			reservationProcessor.performGuestCheckOut(timeAdvancementManagement.getCurrentDate());
			reservationDao.save(reservation);
		} catch (Exception e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(),  "/reservation/view?reservationId="+reservationId);
		}
		return ControllerHelper.popupMessageAndRedirect("Check Out Successful.", "/reservation/view?reservationId="+reservationId,
				new Mail(reservation.getPlace().getHost().getUserDetails().getEmail(),"OpenHome: Check Out Successful. '"+reservation.getPlace().getPlaceDetails().getName()+"'","Link to view reservation : "+OpenHomeMvcApplication.baseUrl+"/reservation/view?reservationId="+reservation.getId()),
				new Mail(reservation.getGuest().getUserDetails().getEmail(),"OpenHome: Check Out Successful. '"+reservation.getPlace().getPlaceDetails().getName()+"'","Link to view reservation : "+OpenHomeMvcApplication.baseUrl+"/reservation/view?reservationId="+reservation.getId()))
				;
	}	
	
	@RequestMapping(value = "/reservation/cancel" ,method=RequestMethod.GET)
	@ValidReservationId
	@ReservationAssociatedUserLoginRequired
	public String cancelReservation(@RequestParam(value="reservationId",required=false) Long reservationId, Model model , HttpSession httpSession ) {
		Reservation reservation = reservationDao.getOne(reservationId);
		try {
			reservationProcessor.setReservation(reservation);
			
			if(sessionManager.getGuestId(httpSession) != null)
				reservationProcessor.performGuestCancel(timeAdvancementManagement.getCurrentDate());
			else
				reservationProcessor.performHostCancel(timeAdvancementManagement.getCurrentDate());
			reservationDao.save(reservation);
		} catch (Exception e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(),  "/reservation/view?reservationId="+reservationId);
		}
		return ControllerHelper.popupMessageAndRedirect("Cancellation Successful.", "/reservation/view?reservationId="+reservationId,
				new Mail(reservation.getPlace().getHost().getUserDetails().getEmail(),"OpenHome: Cancellation Successful. '"+reservation.getPlace().getPlaceDetails().getName()+"'","Link to view reservation : "+OpenHomeMvcApplication.baseUrl+"/reservation/view?reservationId="+reservation.getId()),
				new Mail(reservation.getGuest().getUserDetails().getEmail(),"OpenHome: Cancellation Successful. '"+reservation.getPlace().getPlaceDetails().getName()+"'","Link to view reservation : "+OpenHomeMvcApplication.baseUrl+"/reservation/view?reservationId="+reservation.getId())
				);
	}
	
}
