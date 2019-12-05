package com.openhome.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BasicController {

	@GetMapping("/")
	public String index() {
		return "index";
	}
	
}
