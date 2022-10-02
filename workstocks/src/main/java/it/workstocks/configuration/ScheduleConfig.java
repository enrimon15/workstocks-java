package it.workstocks.configuration;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.service.JobAlertService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
public class ScheduleConfig {
	
	@Autowired
	private JobAlertService jobAlertService;
	
	//@Scheduled(cron = "${workstocks.job-alert.cron-eleven}") /*EVERY DAY ELEVEN*/
	@Scheduled(cron = "${workstocks.job-alert.cron-five-minutes}") /*EVERY FIVE MINUTES FROM MINUTE 0*/
	private void run() throws WorkstocksBusinessException { 
		try {
			jobAlertService.runJobAlert();
		} catch (DataAccessException e) {
			log.error("Scheduler job alert " + LocalDateTime.now() + " failed: " + e.getMessage());
		}
		
	}

}
