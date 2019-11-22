package com.openhome.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Cancellation {
	@Id
	@GeneratedValue
	private Long id;
	
	private Date createdDate;
	
	@OneToOne(
			orphanRemoval=true)
//	@Column(nullable=false,updatable=false)
	private Payment payment;
	
	public Cancellation() {
		createdDate = new Date();
	}
}
