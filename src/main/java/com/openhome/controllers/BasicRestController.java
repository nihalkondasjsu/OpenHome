package com.openhome.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openhome.cronjob.ApplicationCronJobManager;
import com.openhome.mailer.Mailer;
import com.openhome.security.Encryption;
import com.openhome.session.SessionManager;

@RestController
public class BasicRestController {

	@Autowired(required=true)
	SessionManager sessionManager;
	
	@Autowired(required=true)
	Mailer mailer;
	
	@Autowired(required=true)
	ApplicationCronJobManager acjm;

	
	@RequestMapping(value="/sendVerificationToken" , method = RequestMethod.GET)
	public String sendVerificationToken(@RequestParam("email") String email) {
		try {
			if(mailer.sendMail(email, "Email Verification Token", Encryption.tokenGenerator(email))) {
				return "success";
			}
		} catch (Exception e) {
		}

		return "failed";
	}
	
	@RequestMapping(value="/setup" , method = RequestMethod.GET)
	public String setup() {
		acjm.addJob(new Date(3*60*60*1000l));
		acjm.addJob(new Date(11*60*60*1000l));
		acjm.keepRunning();
		return "good";
	}

}
