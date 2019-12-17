package com.openhome.mailer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.openhome.OpenHomeMvcApplication;
import com.openhome.controllers.helper.Mail;

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
			RestTemplate rt = new RestTemplate();
			System.out.println(
			rt.postForObject("http://nihalkonda.com/mail/mail.php",
					mail,
					String.class));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
