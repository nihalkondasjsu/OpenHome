package com.openhome;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.openhome.dao.ImageDAO;
import com.openhome.data.Image;

@Component
public class FileSystem {
	
	public Cloudinary cloudinary = new Cloudinary("cloudinary://583486971878688:blMBpueRrlP3YjT-a0WwuQaR9mk@dpvlqzprd/");
	
	@Autowired(required=true)
	private ImageDAO imageDao;
	

	public Image saveImage(MultipartFile image) {

		try {
			
			return saveImage(cloudinary.uploader().upload(image.getBytes(), null));
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
		
	}
	
	public Image saveImage(String imageLink) throws MalformedURLException, IOException {
		try {
			
			return saveImage(cloudinary.uploader().upload(imageLink, null));
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}
	
	public Image saveImage(Map<String,Object> response) {
		System.out.println(response);
		Image image = new Image();
		
		image.setPublicId(response.get("public_id").toString());
		image.setUrl(response.get("url").toString());
		
		image = imageDao.save(image);
		
		return image;
	}
	
	public void deleteImage(Image oldDpId) {
		try {
			if(oldDpId.getPublicId().equals(""))
				return;
			cloudinary.uploader().destroy(oldDpId.getPublicId(), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
