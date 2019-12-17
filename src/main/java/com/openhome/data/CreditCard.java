package com.openhome.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.openhome.exception.CustomException;

@Entity
public class CreditCard {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String number;
	
	@Column(nullable = false)
	private String cvv;
	
	@Column(nullable = false)
	private Integer expiryMonth;
	
	@Column(nullable = false)
	private Integer expiryYear;
	
	private Date expiryDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public Integer getExpiryMonth() {
		return expiryMonth;
	}

	public void setExpiryMonth(Integer expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public Integer getExpiryYear() {
		return expiryYear;
	}

	public void setExpiryYear(Integer expiryYear) {
		this.expiryYear = expiryYear;
	}
	
	public void validateCard(Date currentDate) throws CustomException {
		if(this.number == null || this.cvv == null || this.expiryMonth == null || this.expiryYear == null)
			throw new CustomException("Incomplete Credit Card Provided");
		
		this.number = number.replace(" ", "").replace("-", "");
		
		if(this.number.length() != 16) {
			throw new CustomException("Invalid Credit Card Number");
		}
		if(this.cvv.length() != 3) {
			throw new CustomException("Invalid Credit Card Cvv");
		}else {
			try {
				int temp = Integer.parseInt(cvv);
			} catch (Exception e) {
				// TODO: handle exception
				throw new CustomException("Invalid Credit Card Cvv");
			}
		}
		if(this.expiryMonth < 1 || this.expiryMonth > 12) {
			throw new CustomException("Invalid Credit Card Expiry Month");
		}
		if(this.expiryYear < 1 || this.expiryYear > 99) {
			throw new CustomException("Invalid Credit Card Expiry Year");
		}
		this.expiryDate = new Date( (2000+this.expiryYear) -1900 + (this.expiryMonth == 12 ? 1 : 0) ,this.expiryMonth%12,1);
		
		System.out.println(expiryDate);
		
		if(!cardValidOn(currentDate)) {
			throw new CustomException("Credit Card Expired");
		}
	}

	public boolean cardValidOn(Date currentDate) {
		return currentDate.before(expiryDate);
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
}
