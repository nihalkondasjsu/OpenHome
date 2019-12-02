package com.openhome.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.openhome.FileSystem;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.HostDAO;
import com.openhome.dao.ImageDAO;
import com.openhome.dao.SpaceDAO;
import com.openhome.data.Address;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.Image;
import com.openhome.data.Space;
import com.openhome.data.SpaceDetails;
import com.openhome.data.UserDetails;
import com.openhome.data.UserVerifiedDetails;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
public class LoadDBController extends SampleDBData{

	@Autowired
	HostDAO hostDAO;
	
	@Autowired
	GuestDAO guestDAO;

	@Autowired
	SpaceDAO spaceDAO;
	
	@Autowired
	ImageDAO imageDAO;
	
	@Autowired
	FileSystem fileSystem;

	@Autowired
	TimeAdvancementManagement timeAdvancementManagement;
	
	@GetMapping("/resetDB")
	public String resetDB() {
		
		hostDAO.deleteAll();
		guestDAO.deleteAll();
		spaceDAO.deleteAll();
		
		return "index";
	}

	@GetMapping("/test")
	public String test() {

//		List<Space> spaces = spaceDAO.getSpacesByLocationAndDates(37.336259-0.000010, 37.336259+0.000010, -121.887067-0.000010, -121.887067+0.000010,(long)3,(long)7);
//		
//		for (Space space : spaces) {
//			System.out.printf("%s %s\n", space.getSpaceDetails().getAddress().getAddressLine1() ,space.getSpaceDetails().getAddress().getAddressLine2() );
//		}
//		
//		spaces = spaceDAO.getSpacesByZipAndDates("95112",(long)3,(long)7);
//		
//		for (Space space : spaces) {
//			System.out.printf("%s %s\n", space.getSpaceDetails().getAddress().getAddressLine1() ,space.getSpaceDetails().getAddress().getAddressLine2() );
//		}
		
		return "index";
	}
	
	@GetMapping("/reloadDB")
	public String reloadDB() throws IOException {
		
		deleteAllImages();
		
		hostDAO.deleteAll();
		guestDAO.deleteAll();
		spaceDAO.deleteAll();
		
		reloadDB1();
		
		return "index";
	}

	private void deleteAllImages() {
		List<Image> images = imageDAO.findAll();
		for (Image image : images) {
			fileSystem.deleteImage(image);
		}
	}

	private void reloadDB1() throws IOException {
		
		int k = 100,k1=0;
		
		File f = new File("ImageUrl.txt");
		
		FileInputStream fis = new FileInputStream(f);
		
		String[] urls = new String(fis.readAllBytes()).split("\n");
		
		
		
		for(int i = 0 ; i < 10 ; i++ ) {
			List<Space> spaces = new ArrayList<Space>();
			for (int j = 0; j < 5; j++) {
				Space s = new Space();
				
				SpaceDetails sd = getRandomSpaceDetails();
				
				sd.setName("place "+k);
				sd.setDescription(sd.getName()+" is a great place.");
				for (int l = 0; l < 3; l++) {
					Image img = new Image();
					img.setPublicId("");
					img.setUrl(urls[k1++]);
					sd.addImage(img);
				}
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
			//if(i != 2) {
				ud.setVerifiedDetails(new UserVerifiedDetails(ud.getEmail(),ud.getPhoneNumber()));
			//}
			createHost(ud,spaces);
			
			UserDetails udg = new UserDetails("i"+i+"@j.k","ij@k*"+i,"10"+i,new UserVerifiedDetails(),"i"+i,"jk"+i);
			//if(i != 2) {
				udg.setVerifiedDetails(new UserVerifiedDetails(udg.getEmail(),udg.getPhoneNumber()));
			//}
			createGuest(udg);
		}
		//createHost(new UserDetails("", password, phoneNumber, verifiedDetails, firstName, lastName));
		
	}

	private void createHost(UserDetails userDetails, List<Space> rentingSpaces) {
		Host h = new Host();
		UserVerifiedDetails userVerifiedDetails = userDetails.getVerifiedDetails();
		userDetails.prepareForRegistration(timeAdvancementManagement.getCurrentDate());
		userDetails.setVerifiedDetails(userVerifiedDetails);
		h.setUserDetails(userDetails);
		for (Space space : rentingSpaces) {
			h.addSpace(space);
		}
		hostDAO.save(h);
		System.out.println("=========> host saved"+h.getId());
		System.out.println("=========> host spaces"+rentingSpaces.size());
	}
	
	private void createGuest(UserDetails userDetails) {
		Guest g = new Guest();
		UserVerifiedDetails userVerifiedDetails = userDetails.getVerifiedDetails();
		userDetails.prepareForRegistration(timeAdvancementManagement.getCurrentDate());
		userDetails.setVerifiedDetails(userVerifiedDetails);
		g.setUserDetails(userDetails);
		guestDAO.save(g);
	}
	
}

