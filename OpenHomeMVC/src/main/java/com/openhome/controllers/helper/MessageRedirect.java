package com.openhome.controllers.helper;

public class MessageRedirect{
	
	String message;
	String redirect;
	
	public MessageRedirect(String message, String redirect) {
		super();
		this.message = message;
		this.redirect = redirect;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getRedirect() {
		return redirect;
	}
	
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	
}