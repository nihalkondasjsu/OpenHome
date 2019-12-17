package com.openhome.controllers.helper;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.mailer.Mailer;

@Controller
@RequestMapping("/messageRedirect")
public class ControllerHelper {

	private static HashMap<Integer,MessageRedirect> map = new HashMap<Integer, MessageRedirect>();
	
	private static Mailer mailer;
	
	public static String popupMessageAndRedirect(String message,String redirect,Mail... mails) {
		if(mailer == null)
			mailer = new Mailer();
		MessageRedirect messageRedirect = new MessageRedirect(message, redirect);
		map.put(messageRedirect.hashCode(), messageRedirect);
		System.out.println(map);
		if(mails != null) {
			for (Mail mail : mails) {
				mailer.sendMail(mail);
			}
		}
		return "redirect:/messageRedirect?mrId="+messageRedirect.hashCode();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String getMessageRedirect(@RequestParam(value="mrId",required=true) Integer mrId, HttpSession httpSession , Model model) {
		model.addAttribute("messageRedirect", map.get(mrId));
		System.out.println(map);
		System.out.println(map.remove(mrId));
		return "messageRedirect";
	}
	
}
