package com.openhome.data;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.openhome.OpenHomeMvcApplication;
import com.openhome.exception.CustomException;
import com.openhome.security.Encryption;
import com.openhome.tam.TimeAdvancementManagement;

@Entity
public class UserDetails {

	public enum UserRegistrationType{
		OpenHome,Facebook,Google
	}
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(unique=true,nullable=false)
	private String email;
	
	@Column(nullable=false)
	private String password;
	
	@Transient
	private String newPassword;
	
	private String phoneNumber = "";

	@Enumerated(EnumType.STRING)
	private UserRegistrationType userRegistrationType = UserRegistrationType.OpenHome;
	
	@OneToOne(fetch=FetchType.EAGER,
			orphanRemoval=true,
			cascade=CascadeType.ALL)
	private Image displayPictureId;
	
	@OneToOne(fetch=FetchType.EAGER,
			orphanRemoval=true,
			cascade=CascadeType.ALL)
	private CreditCard creditCard;
	
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

	private Integer totalReviewsCount = 0;
	
	private Double averageRating = 0.0;
	
	public UserDetails() {
		registeredDate = new Date();
		verifiedDetails = new UserVerifiedDetails();
		userRegistrationType = UserRegistrationType.OpenHome;
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
	
	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
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
	
	public Integer getTotalReviewsCount() {
		return totalReviewsCount;
	}

	public void setTotalReviewsCount(Integer totalReviewsCount) {
		this.totalReviewsCount = totalReviewsCount;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}
	
	public void addRating(Rating rating) {
		this.averageRating = ((this.totalReviewsCount * this.averageRating)+(rating.getRating())) 
				/ (this.totalReviewsCount + 1) ;
		this.totalReviewsCount++;
	}
	
	public void removeRating(Rating rating) {
		this.averageRating = (((this.totalReviewsCount*this.averageRating)-rating.getRating())/(this.totalReviewsCount-1));
		this.totalReviewsCount--;
	}
	
	public void prepareForRegistration(Date registeredDate) {
		this.registeredDate = registeredDate;
		encryptPassword();
		setVerifiedDetails(new UserVerifiedDetails());
		
		if(OpenHomeMvcApplication.automaticUserVerified) {
			getVerifiedDetails().setVerifiedEmail(getEmail());
			getVerifiedDetails().setVerifiedPhoneNumber(getPhoneNumber());
		}
		
	}
	
	public boolean checkPassword(String plainPassword) {
		return Encryption.checkPassword(plainPassword, getPassword());
	}
	
	public void updateDetails(UserDetails userDetailsOld) throws CustomException {
		
		if(userRegistrationType == UserRegistrationType.OpenHome)
		if(getNewPassword().equals("") == false) {
			setPassword(getNewPassword());
			encryptPassword();
		}

		setId(userDetailsOld.getId());
		setEmail(userDetailsOld.getEmail());
		setPassword(userDetailsOld.getPassword());
		setRegisteredDate(userDetailsOld.getRegisteredDate());
		setVerifiedDetails(userDetailsOld.getVerifiedDetails());
	}

	public Image getDisplayPictureId() {
		return displayPictureId;
	}

	public void setDisplayPictureId(Image displayPictureId) {
		this.displayPictureId = displayPictureId;
	}

	public void encryptPassword() {
		setPassword(Encryption.encryptPassword(getPassword()));
	}

	public UserRegistrationType getUserRegistrationType() {
		return userRegistrationType;
	}

	public void setUserRegistrationType(UserRegistrationType userRegistrationType) {
		this.userRegistrationType = userRegistrationType;
	}
}
