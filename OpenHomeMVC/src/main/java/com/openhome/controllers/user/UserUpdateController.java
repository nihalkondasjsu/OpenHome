package com.openhome.controllers.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.openhome.FileSystem;
import com.openhome.Json;
import com.openhome.aop.helper.annotation.UserLoginRequired;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.HostDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.Image;
import com.openhome.data.UserDetails;
import com.openhome.session.SessionManager;
import com.openhome.tam.TimeAdvancementManagement;

@Controller
@RequestMapping("/{userRole}/update")
public class UserUpdateController {

	@Autowired
	HostDAO hostDao;
	
	@Autowired
	UserDetailsDAO userDetailsDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@Autowired
	FileSystem fileSystem;

	@Autowired
	TimeAdvancementManagement timeAdvancementManagement;
	
	@RequestMapping(method=RequestMethod.GET)
	@UserLoginRequired
	public String updateForm( @PathVariable("userRole") String userRole, Model model , HttpSession httpSession ) {
		System.out.println("UpdateController");
		
		if(userRole.equals("host")==false)
			userRole = "guest";
		
		if(userRole.equals("host")) {
			Host h = sessionManager.getHost(httpSession);
			model.addAttribute(userRole, h == null ? new Host() : h);
		}else {
			Guest g = sessionManager.getGuest(httpSession);
			model.addAttribute(userRole, g == null ? new Guest() : g);
		}
		
		return userRole+"/update";
	}

	@RequestMapping(method=RequestMethod.POST)
	@UserLoginRequired
	public String updateFormSubmission(@PathVariable("userRole") String userRole, UserDetails userDetails , Model model , HttpSession httpSession , @RequestParam(value="image",required=false) MultipartFile image, @RequestParam(value="imageUrl",required=false) String imageUrl) {
		Json.printObject(userDetails);
		
		try {
			
			UserDetails userDetailsDB = userDetailsDao.getUserByEmail(sessionManager.getSessionUserDetails(httpSession).getEmail());
			
			System.out.println("userDetailsDB.email : "+userDetailsDB.getEmail());
			
			try {
				System.out.println("DisplayPictureId : "+userDetails.getDisplayPictureId());
				
				if(userDetails.getCreditCard() == null) {
					throw new IllegalArgumentException("Invalid Credit Card");
				}
				
				userDetails.getCreditCard().validateCard(timeAdvancementManagement.getCurrentDate());
				
				Image imageObj=null;
				if(image == null || image.getSize()<1000) {
					if(imageUrl == null || imageUrl.equals("")) {
						System.out.println("No Image Change");
					}else
						imageObj = fileSystem.saveImage(imageUrl);
				}else {
					imageObj = fileSystem.saveImage(image);
				}
				
				if(imageObj != null) {
					System.out.println("Image Provided");
					userDetails.setDisplayPictureId(imageObj);
				}
				
				userDetails.updateDetails(userDetailsDB);
				System.out.println("DisplayPictureId : "+userDetails.getDisplayPictureId());
			} catch (Exception e) {
				e.printStackTrace();
				return ControllerHelper.popupMessageAndRedirect("Invalid Credentials.", userRole+"/update");
			}
			
			userDetailsDao.save(userDetails);
			
			return ControllerHelper.popupMessageAndRedirect(userRole + " details updated.", userRole+"/dashboard");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		
		return ControllerHelper.popupMessageAndRedirect("", userRole + "/update");
	}
	
}
