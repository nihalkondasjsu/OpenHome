package com.openhome.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.mailer.Mailer;

@Controller
public class BasicController {

	@Autowired(required=true)
	Mailer mailer;
	
	@GetMapping("/")
	public String index() {
		mailer.sendMail("openhomedksv@gmail.com", "Openhome Home", "Chill");
		return "index";
	}
	
}
