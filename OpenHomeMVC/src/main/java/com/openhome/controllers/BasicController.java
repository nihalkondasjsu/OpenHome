package com.openhome.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.openhome.FileSystem;
import com.openhome.dao.HostDAO;
import com.openhome.data.Host;

@Controller
public class BasicController {

	@Autowired
	HostDAO hostDAO;
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	@Autowired
	FileSystem fileSystem;
	
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
	
	@RequestMapping(value="/image",method=RequestMethod.GET)
	public String image(Model model) {
		ArrayList<String> imageUrls = new ArrayList<String>();
		
//		File[] list = FileSystem.imagesDirectory.listFiles();
//		
//		if(list != null)
//		for (File file : list) {
//			imageUrls.add(file.getParentFile().getName()+"/"+file.getName());
//		}
		
		model.addAttribute("images", imageUrls);
		return "image";
	}
	
	@RequestMapping(value="/imageFile",method=RequestMethod.POST)
	public String image(Model model , @RequestParam("image") MultipartFile image) {
		
		fileSystem.saveImage(image);
		
		return image(model);
	}
	
	@RequestMapping(value="/imageUrl",method=RequestMethod.POST)
	public String imageUrl(Model model , @RequestParam("image") String image) throws MalformedURLException, IOException {
		
		fileSystem.saveImage(image);
		
		return image(model);
	}
	
	@RequestMapping(value="/images/{fileName}",method=RequestMethod.GET,produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] imageDelivery(@PathVariable("fileName") String fileName,Model model) {
//		try {
//			return new FileInputStream(new File(FileSystem.imagesDirectory,fileName)).readAllBytes();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			try {
//				return new FileInputStream(new File(FileSystem.imagesDirectory,"image_unavailable.jpg")).readAllBytes();
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		return new byte[] {};
	}
	
}
