package com.openhome.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/timeDelta")
public class TimeAdvancementManagementController {

	@Autowired(required=true)
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	public String getTimeDelta(Model model) {
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String date = simpleDateFormat.format(timeAdvancementManagement.getCurrentDate());
		model.addAttribute("dateString",date.replace(" ", "T"));
		return "timedelta";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String postTimeDelta(Model model , @RequestParam(value="datetime") String jumpToDate) throws ParseException {
		System.out.println(jumpToDate);
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		jumpToDate = jumpToDate.replace("T", " ");
		Date date = simpleDateFormat.parse(jumpToDate);
		timeAdvancementManagement.setCurrentDate(date);
		return getTimeDelta(model);
	}
	
}
