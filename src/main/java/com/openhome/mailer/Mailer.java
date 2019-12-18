package com.openhome.mailer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.openhome.Json;
import com.openhome.OpenHomeMvcApplication;
import com.openhome.controllers.helper.Mail;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class Mailer {

	@Autowired(required=true)
    private JavaMailSender javaMailSender;
	
	public boolean sendMail(String email,String subject,String body) {
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom("openhomedksv@gmail.com");
	        msg.setTo(email);
	        
	        if(OpenHomeMvcApplication.debugMailBody) {
	        	msg.setCc("openhomedksv@gmail.com");
	        	subject = "To: "+email+" | " + subject;
	        }
	        msg.setSubject(subject);
	        
	        msg.setText(body);
	        
	        javaMailSender.send(msg);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void sendMail(Mail mail) {
		// TODO Auto-generated method stub
		try {
			if(OpenHomeMvcApplication.debugMailBody) {
				mail.setSubject("To: "+mail.getEmail()+" | " + mail.getSubject());
	        }
					Request request = new Request.Builder()
				      .url("http://nihalkonda.com/mail/mail.php?data="+Json.base64(mail))
				      //.post(formBody)
				      .get()
				      .build();
				 
				    OkHttpClient client = new OkHttpClient();
					Call call = client.newCall(request);
				    Response response = call.execute();
				    System.out.println(response.message());
		//	System.out.println(WebClient.create().post().uri("http://nihalkonda.com/mail/mail.php").bodyValue(mail).retrieve().bodyToMono(String.class).block());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
