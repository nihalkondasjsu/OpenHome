package com.openhome.controllers.user.host;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.aop.helper.annotation.HostLoginRequired;
import com.openhome.data.Place;
import com.openhome.data.Reservation;
import com.openhome.data.Transaction;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/host/monthlysummary")
public class HostMonthlyBillingSummary {

	@Autowired(required=true)
	SessionManager sessionManager;
	
	@Autowired(required=true)
	TimeAdvancementManagement tam;
	
	@RequestMapping(method=RequestMethod.GET)
	@HostLoginRequired
	public String getReservationCreatePage( Model model , HttpSession httpSession ) {
		
		List<Place> places = sessionManager.getHost(httpSession).getRentingPlaces();
		
		Date date = tam.getCurrentDate();
		
		List<Transaction> transactions = new ArrayList<Transaction>() ;
		
		
		for (Place place : places) {
			List<Reservation> reservations = place.getReservations();
			
			
			for (Reservation reservation : reservations) {
				List<Transaction> tempList = reservation.getTransactions();
				for (Transaction transaction : tempList) {
					if(transaction.getDayToChargeFor().getTime() < date.getTime() - 365 * 24 * 60 * 60 * 1000l) {
						// too old
					}else {
						if(transaction.getTransactionUser() == Transaction.TransactionUser.Host)
							transactions.add(transaction);
					}
				}
			}
		}
		
		
		
		model.addAttribute("transactions", transactions);
		return "transaction/list";
	}
	
	
}
