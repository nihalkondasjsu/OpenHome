package com.openhome.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.UserDetails;
import com.openhome.mailer.Mailer;
import com.openhome.security.Encryption;
import com.openhome.session.SessionManager;

@RestController
public class BasicRestController {

	@Autowired
	SessionManager sessionManager;
	
	@Autowired
	Mailer mailer;
	
	@RequestMapping(value="/sendVerificationToken" , method = RequestMethod.GET)
	public String sendVerificationToken(@RequestParam("email") String email) {
		
		try {
			if(mailer.sendMail(email, "Email Verification Token", generateEmailVerificationToken(email))) {
				return "success";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "failed";
	}

	private String generateEmailVerificationToken(String email) {
		return Encryption.tokenGenerator(email);
	}
	
}
