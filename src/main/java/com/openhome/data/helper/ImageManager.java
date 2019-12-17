package com.openhome.data.helper;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.openhome.FileSystem;
import com.openhome.data.Image;

@Component
public class ImageManager {

	@Autowired(required=true)
	FileSystem fileSystem;
	
	public Image getImage(MultipartFile image , String imageUrl) {
		Image imageObj = null;
		
		if(image == null || image.getSize()<1000) {
			if(imageUrl == null || imageUrl.equals("")) {
				System.out.println("No Image Change");
			} else
				try {
					imageObj = fileSystem.saveImage(imageUrl);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}else {
			imageObj = fileSystem.saveImage(image);
		}
		
		return imageObj;
	}

	public void deleteImage(Image image) {
		fileSystem.deleteImage(image);
	}
	
}
