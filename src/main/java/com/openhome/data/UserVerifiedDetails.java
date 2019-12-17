package com.openhome.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserVerifiedDetails {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String verifiedEmail = "";
	
	private String verifiedPhoneNumber = "";
	
	public UserVerifiedDetails(String verifiedEmail, String verifiedPhoneNumber) {
		super();
		this.verifiedEmail = verifiedEmail;
		this.verifiedPhoneNumber = verifiedPhoneNumber;
	}

	public UserVerifiedDetails() {
		// TODO Auto-generated constructor stub
	}

	public String getVerifiedEmail() {
		return verifiedEmail;
	}

	public void setVerifiedEmail(String verifiedEmail) {
		this.verifiedEmail = verifiedEmail;
	}

	public String getVerifiedPhoneNumber() {
		return verifiedPhoneNumber;
	}

	public void setVerifiedPhoneNumber(String verifiedPhoneNumber) {
		this.verifiedPhoneNumber = verifiedPhoneNumber;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
