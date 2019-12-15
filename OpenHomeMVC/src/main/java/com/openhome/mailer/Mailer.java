package com.openhome.mailer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.openhome.OpenHomeMvcApplication;

@Component
public class Mailer {

	@Autowired(required=true)
    private JavaMailSender javaMailSender;
	
	public boolean sendMail(String email,String subject,String body) {
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
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
	
}
