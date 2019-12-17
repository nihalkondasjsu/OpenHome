package com.openhome.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TimeManagement {

	@Id
	@GeneratedValue
	private Integer id;
	
	private Long timeDelta;

	public TimeManagement() {
	}
	
	public TimeManagement(Long timeDelta) {
		this();
		this.timeDelta = timeDelta;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getTimeDelta() {
		return timeDelta;
	}

	public void setTimeDelta(Long timeDelta) {
		this.timeDelta = timeDelta;
	}
	
}
