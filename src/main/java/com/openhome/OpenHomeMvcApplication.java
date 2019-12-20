package com.openhome;

import java.io.FileInputStream;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.openhome.cronjob.ApplicationCronJobManager;

@SpringBootApplication
public class OpenHomeMvcApplication {

	public static boolean automaticUserVerified = false;
	public static boolean debugMailBody = true;
	public static boolean reportOnlyUnexpectedExceptions = false;
	//public static boolean continueLoginThroughRestart = true;
	public static String baseUrl = "https://open-home-dksv.herokuapp.com/";
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(OpenHomeMvcApplication.class, args);
		System.out.println("Happy");
		try {
		
			FileInputStream serviceAccount =
					  new FileInputStream("src/main/resources/openhome-3a5c8-firebase-adminsdk-h0qym-e6d9b5f535.json");

			FirebaseOptions options = new FirebaseOptions.Builder()
			  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
			  .setDatabaseUrl("https://openhome-3a5c8.firebaseio.com")
			  .build();

			FirebaseApp.initializeApp(options);
			
			//new OpenHomeMvcApplication().runCron();
			
			ApplicationCronJobManager acjm = context.getBean(ApplicationCronJobManager.class);
			
			acjm.addJob(new Date(3*60*60*1000l + 60*1000l));
			acjm.addJob(new Date(11*60*60*1000l +  60*1000l));
			
			acjm.keepRunning();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
