package com.openhome.controllers.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.openhome.aop.helper.annotation.UserLoginRequired;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.session.SessionManager;

@Controller
public class UserLogoutController {
	
	@Autowired(required=true)
	SessionManager sessionManager;

	@RequestMapping("/{userRole}/logout")
	@UserLoginRequired
	public String logout(@PathVariable("userRole") String userRole, Model model, HttpSession httpSession) {
		
		System.out.println("UserLogoutController");

		sessionManager.logoutUser(httpSession);
		
		model.addAttribute("successLink", "");
		
		return ControllerHelper.popupMessageAndRedirect("", "");
		
	}
	
}
