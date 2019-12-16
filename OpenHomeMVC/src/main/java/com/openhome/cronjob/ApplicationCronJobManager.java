package com.openhome.cronjob;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.tam.TimeAdvancementManagement;

@Component
public class ApplicationCronJobManager {

	ArrayList<ApplicationCronJob> cronJobs ;

	@Autowired(required=true)
	TimeAdvancementManagement tam;
	
	static TimeAdvancementManagement tamS;
	
	public ApplicationCronJobManager() {
		cronJobs = new ArrayList<ApplicationCronJob>();
	}
	
	public void keepRunning() {
		tamS = tam;
		if(tam == null)
			System.out.println("Big problem");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					for (ApplicationCronJob applicationCronJob : cronJobs) {
						applicationCronJob.runTask(tamS);
					}
					try {Thread.sleep(10*1000);}catch(Exception e) {}
				}
			}
		}).start();
	}

	public void addJob(Date runAt) {
		cronJobs.add(new ApplicationCronJob(runAt));
	}
	
}
