package com.openhome.mailer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.openhome.Json;
import com.openhome.OpenHomeMvcApplication;
import com.openhome.controllers.helper.Mail;

import okhttp3.Call;
import okhttp3.MultipartBody;
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
//			SimpleMailMessage msg = new SimpleMailMessage();
//			msg.setFrom("openhomedksv@gmail.com");
//	        msg.setTo(email);
//	        
//	        if(OpenHomeMvcApplication.debugMailBody) {
//	        	msg.setCc("openhomedksv@gmail.com");
//	        	subject = "To: "+email+" | " + subject;
//	        }
//	        msg.setSubject(subject);
//	        
//	        msg.setText(body);
//	        
//	        javaMailSender.send(msg);
			sendMail(new Mail(email,subject,body));
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
			
			List<String> links = extractUrls(mail.getBody());
			
			if(links.size() != 0) {
				mail.setBody(mail.getBody().replace(links.get(0), "<a href='"+links.get(0)+"'>Click Here</a>"));
			}
			
			RequestBody reqBody = new MultipartBody.Builder()
			        .setType(MultipartBody.FORM)
			        .addFormDataPart("data", Json.base64(mail))
			        .build();
			
					Request request = new Request.Builder()
				      .url("http://nihalkonda.com/mail/mail.php")
				      .post(reqBody)
				      .build();
				 
				    OkHttpClient client = new OkHttpClient();
					Call call = client.newCall(request);
				    Response response = call.execute();
				    System.out.println(response.message());
				    response.close();
		//	System.out.println(WebClient.create().post().uri("http://nihalkonda.com/mail/mail.php").bodyValue(mail).retrieve().bodyToMono(String.class).block());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static List<String> extractUrls(String text)
	{
	    List<String> containedUrls = new ArrayList<String>();
	    String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	    Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
	    Matcher urlMatcher = pattern.matcher(text);

	    while (urlMatcher.find())
	    {
	        containedUrls.add(text.substring(urlMatcher.start(0),
	                urlMatcher.end(0)));
	    }

	    return containedUrls;
	}
	
}
