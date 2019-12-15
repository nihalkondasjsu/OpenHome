package com.openhome;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.mailer.Mailer;

@SpringBootApplication
public class OpenHomeMvcApplication {

	public static boolean automaticUserVerified = true;
	public static boolean debugMailBody = true;
	public static boolean reportOnlyUnexpectedExceptions = false;
	
	public static String baseUrl = "http://0b5dc0b3.ngrok.io/";
	
	public static void main(String[] args) {
		SpringApplication.run(OpenHomeMvcApplication.class, args);
		System.out.println("Happy");
		try {
		
			FileInputStream serviceAccount =
					  new FileInputStream("src/main/resources/openhome-3a5c8-firebase-adminsdk-h0qym-e6d9b5f535.json");

			FirebaseOptions options = new FirebaseOptions.Builder()
			  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
			  .setDatabaseUrl("https://openhome-3a5c8.firebaseio.com")
			  .build();

			FirebaseApp.initializeApp(options);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
