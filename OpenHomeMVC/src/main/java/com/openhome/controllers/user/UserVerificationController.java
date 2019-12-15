package com.openhome.controllers.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.openhome.aop.helper.annotation.UserLoginRequired;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.dao.UserVerifiedDetailsDAO;
import com.openhome.data.UserDetails;
import com.openhome.data.UserVerifiedDetails;
import com.openhome.mailer.Mailer;
import com.openhome.security.Encryption;
import com.openhome.session.SessionManager;

@Controller
public class UserVerificationController {

	@Autowired(required=true)
	SessionManager sessionManager;
	
	@Autowired(required=true)
	Mailer mailer;

	@Autowired(required=true)
	UserDetailsDAO userDetailsDao;
	
	@Autowired(required=true)
	UserVerifiedDetailsDAO userVerifiedDetailsDao;
	
	@RequestMapping(value="/verify/{credential}" , method = RequestMethod.GET)
	@UserLoginRequired
	public String loadVerificationPage( @PathVariable("credential") String credential , Model model, HttpSession httpSession ) {
		
		UserDetails ud = sessionManager.getSessionUserDetails(httpSession);
		
		model.addAttribute("userDetails", ud);
		
		if(credential.equals("email") || credential.equals("phone")) {
			return "user/verify/"+credential;
		}
		
		return "index";
	}
	
	@RequestMapping(value="/verify/{credential}/token" , method = RequestMethod.GET)
	@UserLoginRequired
	public String verifyToken(@PathVariable("credential") String credential ,@RequestParam("verificationToken") String verificationToken ,@RequestParam(value="userId",required=false) String userId , Model model, HttpSession httpSession ) {
		UserDetails ud = sessionManager.getSessionUserDetails(httpSession);
		
		if(credential.equals("email")) {
			if(Encryption.verifyToken(ud.getEmail(), verificationToken)) {
				UserVerifiedDetails temp = new UserVerifiedDetails(ud.getEmail(), ud.getVerifiedDetails().getVerifiedPhoneNumber());
				temp.setId(ud.getVerifiedDetails().getId());
				userVerifiedDetailsDao.save(temp);
				return "index";
			}
		}
		
		
		if(credential.equals("phone")) {
			System.out.println(verificationToken);
			try {
				
				FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(verificationToken);
				
				if(decodedToken.getUid().equals(userId)) {
					UserRecord userRecord = FirebaseAuth.getInstance().getUser(userId);
					
					UserVerifiedDetails temp = new UserVerifiedDetails(ud.getVerifiedDetails().getVerifiedEmail(), userRecord.getPhoneNumber());
					temp.setId(ud.getVerifiedDetails().getId());
					userVerifiedDetailsDao.save(temp);
					return "index";
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		model.addAttribute("errorMessage", "Verification Failed");
		
		return loadVerificationPage(credential, model, httpSession);
	}

	@RequestMapping(value="/forgotpassword" , method = RequestMethod.GET)
	public String forgotpassword(Model model, HttpSession httpSession) {
		return "user/forgotpassword";
	}
	
	@RequestMapping(value="/forgotpassword" , method = RequestMethod.POST)
	public String forgotpassword(@RequestParam("email") String email ,
			@RequestParam("verificationToken") String verificationToken ,
			@RequestParam("newPassword") String newPassword ,
			Model model, HttpSession httpSession ) {
		
		UserDetails ud = userDetailsDao.getUserByEmail(email);
		
		if(ud == null) {
			model.addAttribute("errorMessage", "No User by this email id.");
			return "user/forgotpassword";
		}
		
		if(Encryption.verifyToken(ud.getEmail(), verificationToken)) {
			UserVerifiedDetails temp = new UserVerifiedDetails(ud.getEmail(), ud.getVerifiedDetails().getVerifiedPhoneNumber());
			temp.setId(ud.getVerifiedDetails().getId());
			userVerifiedDetailsDao.save(temp);
			if(ud.getUserRegistrationType() == UserDetails.UserRegistrationType.OpenHome) {
				ud.setPassword(newPassword);
				ud.encryptPassword();
			}
			userDetailsDao.save(ud);
			return "index";
		}
		
		
		model.addAttribute("errorMessage", "Verification Failed");
		
		return "user/forgotpassword";
	}
	
}

















