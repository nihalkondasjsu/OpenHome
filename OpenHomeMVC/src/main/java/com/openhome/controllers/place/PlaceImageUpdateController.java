package com.openhome.controllers.place;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.openhome.FileSystem;
import com.openhome.aop.helper.annotation.ValidPlaceId;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.aop.helper.annotation.PlaceHostLoginRequired;
import com.openhome.dao.ImageDAO;
import com.openhome.dao.PlaceDAO;
import com.openhome.dao.PlaceDetailsDAO;
import com.openhome.data.Host;
import com.openhome.data.Image;
import com.openhome.data.Place;
import com.openhome.data.PlaceDetails;
import com.openhome.session.SessionManager;

@Controller
public class PlaceImageUpdateController {

	@Autowired
	PlaceDAO placeDao;
	
	@Autowired
	PlaceDetailsDAO placeDetailsDao;
	
	@Autowired
	SessionManager sessionManager;
	
	@Autowired
	public ImageDAO imageDao;
	
	@Autowired
	FileSystem fileSystem;
	
	@RequestMapping(value="/place/updatePlaceImages",method=RequestMethod.GET)
	@ValidPlaceId
	@PlaceHostLoginRequired
	public String updateForm(@RequestParam(value="placeId",required=false) Long placeId, @RequestParam(value="op") String op, Model model , HttpSession httpSession ) {
		System.out.println("PlaceImageUpdateController");
		Place s = null;
		
		if(op.equals("add")) {
			
			return "place/imagesUpdateAdd";
			
		} else if(op.equals("delete")) {

			return "place/imagesUpdateDelete";
			
		} else if(op.equals("rearrange")) {

			return "place/imagesUpdateRearrange";
			
		}
		
		return "place/imagesUpdate";
		
	}
	
	@RequestMapping(value="/place/updatePlaceImagesAdd",method=RequestMethod.POST)
	@ValidPlaceId
	@PlaceHostLoginRequired
	public String updateFormAdd(@RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ,@RequestParam(value="image",required=false) MultipartFile image, @RequestParam(value="imageUrl",required=false) String imageUrl) {
		System.out.println("PlaceImageUpdateController");
		Place s = null;
		
		try {
			s = placeDao.getOne(placeId);
			Image imageObj = null;
			
			if(image == null || image.getSize()<1000) {
				if(imageUrl == null || imageUrl.equals("")) {
					System.out.println("No Image Change");
				}else
					imageObj = fileSystem.saveImage(imageUrl);
			}else {
				imageObj = fileSystem.saveImage(image);
			}
			
			if(imageObj == null)
				throw new IllegalArgumentException("No Image Provided");
			
			PlaceDetails sd = s.getPlaceDetails();
			
			sd.addImage(imageObj);
			
			placeDetailsDao.save(sd);
			
			model.addAttribute("place", s);
			model.addAttribute("close", "yes");
			return "place/imagesUpdateAdd";
		} catch (Exception e ) {
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
	}
	
	@RequestMapping(value="/place/updatePlaceImagesDelete",method=RequestMethod.POST)
	@ValidPlaceId
	@PlaceHostLoginRequired
	public String updateFormAdd(@RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ,@RequestParam(value="deleteImageId",required=false) Long deleteImageId) {
		System.out.println("PlaceImageUpdateController");
		Place s = null;
		
		try {
			s = placeDao.getOne(placeId);
			
			if(deleteImageId == null) {
				throw new IllegalArgumentException("No Image Provided");
			}
			
			Image image = imageDao.getOne(deleteImageId);
			
			PlaceDetails sd = s.getPlaceDetails();
			
			if(sd.deleteImage(image)) {
				fileSystem.deleteImage(image);
				placeDetailsDao.save(sd);
			}

			model.addAttribute("place", s);
			model.addAttribute("close", "yes");
			return "place/imagesUpdateDelete";
		} catch (Exception e ) {
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
	}
	
	@RequestMapping(value="/place/updatePlaceImagesRearrange",method=RequestMethod.POST)
	@ValidPlaceId
	@PlaceHostLoginRequired
	public String updateFormAdd(@RequestParam(value="placeId",required=false) Long placeId, Model model , HttpSession httpSession ,@RequestParam(value="images[]",required=false) List<Long> images) {
		System.out.println("PlaceImageUpdateController");
		Place s = null;
		
		try {
			s = placeDao.getOne(placeId);
			
			PlaceDetails sd = s.getPlaceDetails();
			
			System.out.println(images);
			
			if(images != null) {
				List<Image> prevImgList = sd.getImages();
				List<Image> newImgList = new ArrayList<Image>();
				for (Long imageId : images) {
					System.out.println("Looking for "+imageId);
					for (Image image : prevImgList) {
						System.out.println("Checking with "+image.getId());
						if(image.getId().equals(imageId)) {
							System.out.println("Image Found");
							newImgList.add(image);
							break;
						}
					}
				}
				
				for (int i = 0; i < prevImgList.size(); i++) {
					imageDao.updateImage(prevImgList.get(i).getId(),newImgList.get(i).getPublicId(),newImgList.get(i).getUrl());
				}
			}
			
			model.addAttribute("place", s);
			model.addAttribute("close", "yes");
			return "place/imagesUpdateRearrange";
		} catch (Exception e ) {
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
	}

	
}
