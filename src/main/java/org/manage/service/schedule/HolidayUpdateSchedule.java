package org.manage.service.schedule;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import org.jboss.logging.Logger;
import org.manage.service.HolidayUpdater;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Calendar;

@ApplicationScoped
public class HolidayUpdateSchedule {

    @Inject
    HolidayUpdater holidayUpdater;

    @Inject
    Logger log;

    @Scheduled(cron = "{consultant.holiday.cron}")
    void increment() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2014; i <= currentYear; i++) {
            holidayUpdater.updateForYear(i);
            log.info("holiday calendar updated for year=" + i);
        }
    }

    void onStart(@Observes StartupEvent ev) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        holidayUpdater.updateForYear(currentYear);
    }
}
