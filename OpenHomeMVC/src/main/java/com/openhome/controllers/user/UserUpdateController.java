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
			
			UserDetails userDetailsDB = userDetailsDao.getUserByEmail(userDetails.getEmail());
		
			try {
				System.out.println("DisplayPictureId : "+userDetails.getDisplayPictureId());
				
				Image imageObj=null;
				if(image == null) {
					if(imageUrl == null) {
						System.out.println("No Image Change");
					}else
					imageObj = fileSystem.saveImage(imageUrl);
				}else {
					if(image.getSize()<1000) {
						System.out.println("No Image Change");
					}else
					imageObj = fileSystem.saveImage(image);
				}
				
				if(imageObj == null) {
					userDetails.setDisplayPictureId(userDetailsDB.getDisplayPictureId());
				}else {
					System.out.println("Image Change");
					Image oldDpId = userDetailsDB.getDisplayPictureId();
					if(oldDpId != null) {
						fileSystem.deleteImage(oldDpId);
					}
					userDetails.setDisplayPictureId(imageObj);
				}
				
				userDetails.updateDetails(userDetailsDB.getEmail(), userDetailsDB.getPassword(),userDetailsDB.getVerifiedDetails());
				System.out.println("DisplayPictureId : "+userDetails.getDisplayPictureId());
			} catch (Exception e) {
				model.addAttribute("errorMessage", "Invalid Credentials.");
				return "host/update";
			}
			
			userDetails.setRegisteredDate(userDetailsDB.getRegisteredDate());
			
			userDetails.setId(userDetailsDB.getId());
			
			userDetailsDao.save(userDetails);
			
			model.addAttribute("successLink", userRole+"/dashboard");
			
			return "redirect";
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return updateForm(userRole, model, httpSession);
	}
	
}
