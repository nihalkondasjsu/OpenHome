package com.openhome.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.openhome.dao.HostDAO;
import com.openhome.data.Host;

@Controller
public class BasicController {

	@Autowired
	HostDAO hostDAO;
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	@GetMapping("/happy")
	public String happy(Model model) {
		model.addAttribute("time", System.currentTimeMillis());
		Host h = new Host();
		//h.setFirstName(" happy ");
		hostDAO.save(h);
		model.addAttribute("hosts", hostDAO.findAll());
		return "happy";
	}
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/testMail")
	public String sendTestMail() {
		
		SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(
        		//"konda.nihal5@gmail.com", "bsaitejagoud@gmail.com", 
        		"abhivadnala@gmail.com"
        		//,"sumanthreddy2012@gmail.com"
        		);

        msg.setSubject("Important Message");
        msg.setText("Cup Mukhyam Bigileee....");

        javaMailSender.send(msg);
        
        return "index";
		
	}
	
	@GetMapping("/firebase")
	public String firebase() {
		for (FirebaseApp f : FirebaseApp.getApps()) {
			System.out.println(f.getName());
		}
		return "index";
	}
	
}
