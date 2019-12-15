package com.openhome.controllers.user;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.openhome.Json;
import com.openhome.aop.helper.annotation.UserLoginRequired;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.HostDAO;
import com.openhome.dao.PlaceDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.Reservation;
import com.openhome.data.UserDetails;
import com.openhome.exception.CustomException;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
//@RequestMapping("/{userRole}/delete")
public class UserDeleteController {

	@Autowired(required=true)
	GuestDAO guestDao;

	@Autowired(required=true)
	HostDAO hostDao;

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	UserDetailsDAO userDetailsDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;
	

	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@UserLoginRequired
	public String loginForm( @PathVariable("userRole") String userRole, Model model , HttpSession httpSession ) {
		System.out.println("DeleteController");
		if(userRole.equals("host")==false)
			userRole = "guest";
		return userRole+"/delete";
	}

	@RequestMapping(method=RequestMethod.POST)
	@UserLoginRequired
	public String loginFormSubmission(@PathVariable("userRole") String userRole,  UserDetails userDetails , Model model , HttpSession httpSession ) {
		Json.printObject(userDetails);
		
		if(sessionManager.getHostId(httpSession) != null) {
			return deleteHost(userRole, userDetails, model, httpSession);
		}else {
			return deleteGuest(userRole, userDetails, model, httpSession);
		}
		
	}

	public String deleteHost(String userRole,UserDetails userDetails , Model model , HttpSession httpSession) {
		try {
			
			Host host = hostDao.findHostByEmail(userDetails.getEmail());
			
			Json.printObject(host);
			
			host.canAccess(userDetails);
			
			if(placeDao.getPlaceCountOfHost(host.getId()) > 0) {
				return ControllerHelper.popupMessageAndRedirect("Delete all your places before unregistering.", "host/dashboard");
			}else {
				sessionManager.logoutUser(httpSession);
				
				userDetailsDao.deleteById(host.getUserDetails().getId());
				
				hostDao.deleteById(host.getId());
				
				return ControllerHelper.popupMessageAndRedirect("Host Unregistered Successfully.", "");
			}
			
		} catch (CustomException e) {
			System.out.println(e.toString());
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		} catch (Exception e) {
			System.out.println(e.toString());
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
	}
	
	public String deleteGuest(String userRole,UserDetails userDetails , Model model , HttpSession httpSession) {
		try {
			
			Guest guest = guestDao.findGuestByEmail(userDetails.getEmail());
			
			Json.printObject(guest);
			
			guest.canAccess(userDetails);
			
			Date current = timeAdvancementManagement.getCurrentDate();
			
			List<Reservation> reservations = guest.getReservations();
			
			for (Reservation reservation : reservations) {
				if(reservation.dateOfCheckOut().after(current)) {
					return ControllerHelper.popupMessageAndRedirect("Cannot delete Guest.Guest has future reservation.", "guest/dashboard");
				}
			}
			
			sessionManager.logoutUser(httpSession);
			
			userDetailsDao.deleteById(guest.getUserDetails().getId());
			
			guestDao.deleteById(guest.getId());

			return ControllerHelper.popupMessageAndRedirect("Guest Unregistered Successfully.", "");
		} catch (CustomException e) {
			System.out.println(e.toString());
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		} catch (Exception e) {
			System.out.println(e.toString());
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
	}
}
