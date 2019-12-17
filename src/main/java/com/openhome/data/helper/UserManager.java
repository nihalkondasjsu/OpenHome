package com.openhome.data.helper;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.openhome.dao.GuestDAO;
import com.openhome.dao.HostDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.Image;
import com.openhome.data.UserDetails;
import com.openhome.exception.CustomException;

@Component
public class UserManager {

	@Autowired(required=true)
	HostDAO hostDao;
	
	@Autowired(required=true)
	GuestDAO guestDao;
	
	@Autowired(required=true)
	UserDetailsDAO userDetailsDao;
	
	@Autowired(required=true)
	ImageManager imageManager;

	public void  updateUserDetails(String email,Date currentDate,UserDetails userDetails,MultipartFile imageFile,String imageUrl) throws CustomException {
		
		if(userDetails.getCreditCard() == null) {
			throw new CustomException("Invalid Credit Card");
		}

		userDetails.getCreditCard().validateCard(currentDate);
		
		UserDetails userDetailsDB = userDetailsDao.getUserByEmail(email);
		
		if(userDetailsDB == null) {
			throw new CustomException("Invalid Credentials");
		}
		
		Image imageObj = imageManager.getImage(imageFile, imageUrl);
		
		if(imageObj != null) {
			System.out.println("Image Provided");
			userDetails.setDisplayPictureId(imageObj);
		}else {
			userDetails.setDisplayPictureId(userDetailsDB.getDisplayPictureId());
		}
		
		userDetails.updateDetails(userDetailsDB);
		
		userDetailsDao.save(userDetails);
		
	}
	
	public Host loginHost(UserDetails userDetails) throws CustomException {
		Host host = hostDao.findHostByEmail(userDetails.getEmail());
		if(host == null || host.getUserDetails().checkPassword(userDetails.getPassword()) == false)
			throw new CustomException("Invalid Credentials");
		return host;
	}
	
	public Guest loginGuest(UserDetails userDetails) throws CustomException {
		Guest guest = guestDao.findGuestByEmail(userDetails.getEmail());
		if(guest == null || guest.getUserDetails().checkPassword(userDetails.getPassword()) == false)
			throw new CustomException("Invalid Credentials");
		return guest;
	}
	
	public Host registerHost(Date currentDate,UserDetails userDetails,MultipartFile imageFile,String imageUrl) throws CustomException {
		
		Host h = new Host();
			
		h.setUserDetails(createUserDetails(currentDate, userDetails, imageFile, imageUrl));
		
		return hostDao.save(h);
	}
	
	public Guest registerGuest(Date currentDate,UserDetails userDetails,MultipartFile imageFile,String imageUrl) throws CustomException {
		
		Guest g = new Guest();
			
		g.setUserDetails(createUserDetails(currentDate, userDetails, imageFile, imageUrl));
		
		return guestDao.save(g);
	}
	
	private UserDetails createUserDetails(Date currentDate,UserDetails userDetails,MultipartFile imageFile,String imageUrl) throws CustomException {
		if(userDetails.getCreditCard() == null) {
			throw new CustomException("Invalid Credit Card");
		}
		userDetails.getCreditCard().validateCard(currentDate);
		
		userDetails.prepareForRegistration(currentDate);
		
		UserDetails userDetailsDB = userDetailsDao.getUserByEmail(userDetails.getEmail());
		
		if(userDetailsDB != null) {
			throw new CustomException("Email id being used by a existing User.");
		}
		
		Image imageObj = imageManager.getImage(imageFile, imageUrl);
		
		if(imageObj != null) {
			System.out.println("Image Provided");
			userDetails.setDisplayPictureId(imageObj);
		}
		
		return userDetails;
	}
	
}
