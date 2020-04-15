package common.manager.domain.service.schedule.impl;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import common.manager.domain.service.schedule.ScheduleService;

@EnableScheduling
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Override
    @Scheduled(cron = "0 17 23 31 * *")
    public void updateUsersStatus() {
        //Not implemented yet
    }

}
