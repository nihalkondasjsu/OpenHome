package com.openhome.controllers.user.guest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.aop.helper.annotation.GuestLoginRequired;
import com.openhome.data.Reservation;
import com.openhome.data.Transaction;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/guest/monthlysummary")
public class GuestMonthlyBillingSummary {

	@Autowired(required=true)
	SessionManager sessionManager;
	
	@Autowired(required=true)
	TimeAdvancementManagement tam;
	
	@RequestMapping(method=RequestMethod.GET)
	@GuestLoginRequired
	public String getReservationCreatePage( Model model , HttpSession httpSession ) {
		List<Reservation> reservations = sessionManager.getGuest(httpSession).getReservations();
		
		Date date = tam.getCurrentDate();
		
		List<Transaction> transactions = new ArrayList<Transaction>() ;
		
		for (Reservation reservation : reservations) {
			List<Transaction> tempList = reservation.getTransactions();
			for (Transaction transaction : tempList) {
				if(transaction.getDayToChargeFor().getTime() < date.getTime() - 365 * 24 * 60 * 60 * 1000l) {
					// too old
				}else {
					if(transaction.getTransactionUser() == Transaction.TransactionUser.Guest)
						transactions.add(transaction);
				}
			}
		}
		
		model.addAttribute("transactions", transactions);
		return "transaction/list";
	}
	
	
}
