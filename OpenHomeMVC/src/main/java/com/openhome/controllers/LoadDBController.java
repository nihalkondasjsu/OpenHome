package com.openhome.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.HostDAO;
import com.openhome.dao.SpaceDAO;
import com.openhome.data.Address;
import com.openhome.data.Host;
import com.openhome.data.Space;
import com.openhome.data.SpaceDetails;
import com.openhome.data.UserDetails;
import com.openhome.data.UserVerifiedDetails;

@Controller
public class LoadDBController extends SampleDBData{

	@Autowired
	HostDAO hostDAO;
	
	@Autowired
	GuestDAO guestDAO;

	@Autowired
	SpaceDAO spaceDAO;

	@GetMapping("/resetDB")
	public String resetDB() {
		
		hostDAO.deleteAll();
		guestDAO.deleteAll();
		spaceDAO.deleteAll();
		
		return "index";
	}

	@GetMapping("/test")
	public String test() {

		List<Space> spaces = spaceDAO.getSpacesByLocationAndDates(37.336259-0.000010, 37.336259+0.000010, -121.887067-0.000010, -121.887067+0.000010,(long)3,(long)7);
		
		for (Space space : spaces) {
			System.out.printf("%s %s\n", space.getSpaceDetails().getAddress().getAddressLine1() ,space.getSpaceDetails().getAddress().getAddressLine2() );
		}
		
		spaces = spaceDAO.getSpacesByZipAndDates("95112",(long)3,(long)7);
		
		for (Space space : spaces) {
			System.out.printf("%s %s\n", space.getSpaceDetails().getAddress().getAddressLine1() ,space.getSpaceDetails().getAddress().getAddressLine2() );
		}
		
		return "index";
	}
	
	@GetMapping("/reloadDB")
	public String reloadDB() {
		
		hostDAO.deleteAll();
		guestDAO.deleteAll();
		spaceDAO.deleteAll();
		
		reloadDB1();
		
		return "index";
	}

	private void reloadDB1() {
		
		int k = 100;
		
		for(int i = 0 ; i < 10 ; i++ ) {
			List<Space> spaces = new ArrayList<Space>();
			for (int j = 0; j < 5; j++) {
				Space s = new Space();
				
				SpaceDetails sd = getRandomSpaceDetails();
				
				sd.setName("place "+k);
				
				Address a = new Address();
				
				a.setLatitude(gps[i][0]);
				a.setLongitude(gps[i][1]);
				
				a.setAddressLine1(addressLine1[i]);
				a.setAddressLine2("Apt#"+(++k));
				a.setCity(city[i]);
				a.setState(state[i]);
				a.setZip(zip[i]);
				a.setCountry("United States");
				
				sd.setAddress(a);
				
				s.setSpaceDetails(sd);
				
				spaces.add(s);
			}
			UserDetails ud = new UserDetails("a"+i+"@b.c","ab@c*"+i,"00"+i,new UserVerifiedDetails(),"a"+i,"bc"+i);
			if(i != 2) {
				ud.setVerifiedDetails(new UserVerifiedDetails(ud.getEmail(),ud.getPhoneNumber()));
			}
			createHost(ud,spaces);
		}
		//createHost(new UserDetails("", password, phoneNumber, verifiedDetails, firstName, lastName));
		
	}

	private void createHost(UserDetails userDetails, List<Space> rentingSpaces) {
		Host h = new Host();
		h.setUserDetails(userDetails);
		for (Space space : rentingSpaces) {
			h.addSpace(space);
		}
		hostDAO.save(h);
	}
	
}

