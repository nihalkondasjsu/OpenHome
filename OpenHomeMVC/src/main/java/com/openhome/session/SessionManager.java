package com.openhome.session;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.dao.GuestDAO;
import com.openhome.dao.HostDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.UserDetails;

@Component
public class SessionManager {

	@Autowired(required=true)
	HostDAO hostDao;

	@Autowired(required=true)
	GuestDAO guestDao;

	public Host getHost(HttpSession httpSession) {
		try {
			if(httpSession.getAttribute("hostId") == null)
				return null;
			return hostDao.getOne(getHostId(httpSession));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public Guest getGuest(HttpSession httpSession) {
		try {
			if(httpSession.getAttribute("guestId") == null)
				return null;
			return guestDao.getOne(getGuestId(httpSession));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public void setHost(HttpSession httpSession,Long id) {
		httpSession.setAttribute("hostId", id);
		httpSession.setAttribute("guestId", null);
		System.out.println("hostId: "+id);
	}
	
	public void setGuest(HttpSession httpSession,Long id) {
		httpSession.setAttribute("hostId", null);
		httpSession.setAttribute("guestId", id);
		System.out.println("guestId: "+id);
	}
	
	public void logoutUser(HttpSession httpSession) {
		httpSession.setAttribute("hostId", null);
		httpSession.setAttribute("guestId", null);
	}
	
	public Long getHostId(HttpSession httpSession) {
		return (Long)httpSession.getAttribute("hostId");
	}
	
	public Long getGuestId(HttpSession httpSession) {
		return (Long)httpSession.getAttribute("guestId");
	}
	
	public UserDetails getSessionUserDetails(HttpSession httpSession) {
		if(getHostId(httpSession) != null) {
			return getHost(httpSession).getUserDetails();
		}
		if(getGuestId(httpSession) != null) {
			return getGuest(httpSession).getUserDetails();
		}
		return null;
	}

	public boolean hasUserLogin(HttpSession httpSession) {
		return getGuestId(httpSession) != null || getHostId(httpSession) != null;
	}
	
}
