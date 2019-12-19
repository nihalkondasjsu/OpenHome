package com.openhome.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.controllers.helper.Mail;
import com.openhome.mailer.Mailer;
import com.openhome.session.SessionManager;

@Controller
public class BasicController {

	@Autowired(required=true)
	Mailer mailer;
	
	@Autowired(required=true)
	SessionManager sessionManager;
	
	@GetMapping("/")
	public String index() {
		mailer.sendMail(new Mail("openhomedksv@gmail.com", "Openhome Home", "Chill"));
		return "index";
	}
	
	@GetMapping("/home")
	public String home(HttpSession httpSession) {
		try {
			if(sessionManager.getHostId(httpSession) != null) {
				return ControllerHelper.popupMessageAndRedirect("", "/host/dashboard");
			}else if(sessionManager.getGuestId(httpSession) != null) {
				return ControllerHelper.popupMessageAndRedirect("", "/guest/dashboard");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "index";
	}
	
}
