package com.openhome.tam;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.openhome.dao.TimeManagementDAO;
import com.openhome.data.TimeManagement;

@Component
@Configurable
public class TimeAdvancementManagement {

	@Autowired(required=true)
	private TimeManagementDAO timeManagementDao;
	
	private static Long timeDelta;
	
	public Date getCurrentDate() {
		if(TimeAdvancementManagement.timeDelta != null) {
			return new Date(new Date().getTime()+TimeAdvancementManagement.timeDelta);
		}
		try {
			TimeManagement tm = timeManagementDao.findAll().get(0);
			Long timeDelta = 0l;
			
			if(tm == null) {
				timeManagementDao.save(new TimeManagement(0l));
			}else {
				timeDelta = tm.getTimeDelta();
			}
			
			TimeAdvancementManagement.timeDelta = timeDelta;
			
			return getCurrentDate();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new Date();
	}
	
	public void setCurrentDate(Date date) {
		if(getCurrentDate().after(date))
			return;
		TimeManagement timeManagement = new TimeManagement(date.getTime() - new Date().getTime());
		//timeManagementDao.deleteById(101);
		timeManagementDao.deleteAll();
		timeManagementDao.save(timeManagement);
		TimeAdvancementManagement.timeDelta = timeManagement.getTimeDelta();
	}
	
}
