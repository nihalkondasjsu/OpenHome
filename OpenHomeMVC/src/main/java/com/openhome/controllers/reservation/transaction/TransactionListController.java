package com.openhome.controllers.reservation.transaction;

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
import com.openhome.data.Reservation;

@Controller
public class TransactionListController {

	@Autowired(required=true)
	ReservationDAO reservationDao;
	
	@RequestMapping(value = "/transaction/list" ,method=RequestMethod.GET)
	@ValidReservationId
	@ReservationAssociatedUserLoginRequired
	public String guestCheckOut(@RequestParam(value="reservationId",required=false) Long reservationId, Model model , HttpSession httpSession ) {
		Reservation reservation = reservationDao.getOne(reservationId);
		model.addAttribute("transactions", reservation.getTransactions());
		return "transaction/list";
	}	
	
}
