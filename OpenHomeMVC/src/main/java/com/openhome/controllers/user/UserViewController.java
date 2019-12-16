package com.openhome.controllers.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.aop.helper.annotation.UserLoginRequired;
import com.openhome.aop.helper.annotation.ValidHostId;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.HostDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.session.SessionManager;

@Controller
public class UserViewController {

	@Autowired(required=true)
	HostDAO hostDao;
	
	@Autowired(required=true)
	GuestDAO guestDao;
	
	@Autowired(required=true)
	UserDetailsDAO userDetailsDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;

	@RequestMapping(value="/host/view",method=RequestMethod.GET)
	@ValidHostId
	public String viewHost(@RequestParam(value="hostId",required=false) Long hostId,Model model , HttpSession httpSession ) {
		System.out.println("DashboardController");
		model.addAttribute("host", hostDao.getOne(hostId));
		return "host/view";
	}
	
	@RequestMapping(value="/guest/view",method=RequestMethod.GET)
	@ValidHostId
	public String viewGuest(@RequestParam(value="guestId",required=false) Long guestId,Model model , HttpSession httpSession ) {
		System.out.println("DashboardController");
		model.addAttribute("guest", guestDao.getOne(guestId));
		return "guest/view";
	}
	
}
