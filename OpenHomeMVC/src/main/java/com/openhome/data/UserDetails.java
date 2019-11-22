package com.openhome.data;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.openhome.security.Encryption;

@Entity
public class UserDetails {

	@Transient
	public static final boolean canEmailBeChanged = true;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(unique=true,nullable=false)
	private String email;
	
	@Column(nullable=false)
	private String password;
	
	@Transient
	private String newEmail;
	
	@Transient
	private String newPassword;
	
	private String phoneNumber = "";
	
	@JoinColumn(updatable=false)
	@OneToOne(fetch=FetchType.EAGER,
			cascade=CascadeType.ALL,
			orphanRemoval=true)
	private UserVerifiedDetails verifiedDetails;
	
	@Column(nullable=false)
	private String firstName;
	@Column(nullable=false)
	private String lastName;
	

	@Column(nullable=false,updatable=false)
	private Date registeredDate;

	
	public UserDetails() {
		registeredDate = new Date();
		verifiedDetails = new UserVerifiedDetails();
	}
	
	public UserDetails(String email, String password, String phoneNumber, UserVerifiedDetails verifiedDetails,
			String firstName, String lastName) {
		this();
		this.email = email;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.verifiedDetails = verifiedDetails;
		this.firstName = firstName;
		this.lastName = lastName;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getNewEmail() {
		return newEmail;
	}

	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public UserVerifiedDetails getVerifiedDetails() {
		return verifiedDetails;
	}

	public void setVerifiedDetails(UserVerifiedDetails verifiedDetails) {
		this.verifiedDetails = verifiedDetails;
	}
	
	public boolean verifiedEmail() {
		try {
			return this.verifiedDetails.getVerifiedEmail().equals(this.email);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public boolean verifiedPhone() {
		try {
			return this.verifiedDetails.getVerifiedPhoneNumber().equals(this.phoneNumber);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	public Date getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public void encryptPassword() {
		setPassword(Encryption.encryptPassword(getPassword()));
	}
	
	public boolean checkPassword(String plainPassword) {
		return Encryption.checkPassword(plainPassword, getPassword());
	}
	
	public void updateDetails(String sessionEmail,String sessionPassword, UserVerifiedDetails userVerifiedDetails) throws IllegalAccessException {
		if(sessionEmail.equals(getEmail()) == false || Encryption.checkPassword(getPassword(), sessionPassword) == false) {
			throw new IllegalAccessException("Mismatched credentials with the user in Session.");
		}
		
		if(canEmailBeChanged)
		if(getNewEmail().equals("") == false) {
			setEmail(getNewEmail());
		}
		
		if(getNewPassword().equals("") == false) {
			setPassword(getNewPassword());
		}
		encryptPassword();
		setVerifiedDetails(userVerifiedDetails);
	}
}
