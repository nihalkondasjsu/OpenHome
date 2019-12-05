package com.openhome.tam;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.openhome.dao.ReservationDAO;
import com.openhome.dao.TimeManagementDAO;
import com.openhome.data.Reservation;
import com.openhome.data.TimeManagement;
import com.openhome.data.manager.ReservationManager;

@Component
@Configurable
public class TimeAdvancementManagement {

	@Autowired(required=true)
	private TimeManagementDAO timeManagementDao;
	
	@Autowired(required=true)
	private ReservationDAO reservationDao;

	@Autowired
	ReservationManager reservationManager;
	
	private static Long timeDelta;
	
	private static Boolean CAN_GO_BACK = true;
	
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
		if(getCurrentDate().after(date) && !CAN_GO_BACK)
			return;
		TimeManagement timeManagement = new TimeManagement(date.getTime() - new Date().getTime());
		timeManagementDao.deleteAll();
		timeManagementDao.save(timeManagement);
		TimeAdvancementManagement.timeDelta = timeManagement.getTimeDelta();
		processAllReservations();
	}

	private void processAllReservations() {
		List<Reservation> reservations = reservationDao.getAllRunningReservations();
		for (Reservation reservation : reservations) {
			reservationManager.setReservation(reservation);
			reservationManager.processReservation(getCurrentDate());
		}
	}
	
}
