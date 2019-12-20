package com.openhome.cronjob;

import java.util.Date;

import com.openhome.tam.TimeAdvancementManagement;

public class ApplicationCronJob {

	Date runAt;
	
	public ApplicationCronJob(Date runAt) {
		this.runAt = runAt;
	}
	
	public void runTask(TimeAdvancementManagement tam) {
		long ONE_DAY = (24*60*60*1000l);
		if(tam == null)
			System.out.println("Big problem");
		Date currentDate = tam.getCurrentDate();
		if(currentDate.after(runAt)) {
			runAt = new Date(( currentDate.getTime()-(currentDate.getTime()%ONE_DAY) )+ONE_DAY + (runAt.getTime()%ONE_DAY));
			tam.processAllReservations();
		}else {
			System.out.println((runAt.getTime()-currentDate.getTime())+" ms more to run ");
		}
	}
	
}
