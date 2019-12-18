package com.openhome.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.OpenHomeMvcApplication;
import com.openhome.mailer.Mailer;
import com.openhome.session.SessionManager;

@Component
public class ExceptionManager {

	@Autowired(required = true)
	Mailer mailer;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private SessionManager sessionManager;
	
	public void reportException(Exception e) {
		e.printStackTrace();
		
		if(e.getClass() == CustomException.class && OpenHomeMvcApplication.reportOnlyUnexpectedExceptions)
			return;
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String sStackTrace = sw.toString();
		
		sStackTrace = e.toString()+"\n"+e.getStackTrace()[0].toString();
		
		String email = "anonymous";
		
		try {
			email = sessionManager.getSessionUserDetails(request.getSession()).getEmail();
		} catch (Exception e2) {
			
		}
		
		mailer.sendMail("openhomedksv@gmail.com", "Exception Report : "+e.toString(), 
				"User Email : "+email+"\n\n"+
				"Exception Stack Trace : \n\n"+sStackTrace
				);
	}
	
}
