package org.manage.service.holiday;

import org.manage.domain.DayInfo;
import org.manage.domain.enums.DayType;
import org.manage.service.dto.YearInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;

@ApplicationScoped
public class HolidayUpdater {

    public static final String CONSULTANT_SOURCE_TYPE = "consultant";

    @Inject
    ConsultantHolidayService holidayService;

    @Transactional
    public YearInfo updateForYear(int year) {
        YearInfo yearInfo = holidayService.getByYear(year);
        for(var info : yearInfo.holidays){
            persistDay(info, DayType.HOLIDAY);
        }

        for( var info :yearInfo.weekends){
            persistDay(info, DayType.WEEKEND);
        }

        for( var info :yearInfo.preHolidays){
            persistDay(info, DayType.PRE_HOLIDAY);
        }
        return yearInfo;
    }

    private void persistDay(LocalDate info, DayType holiday) {
        DayInfo dayInfo = DayInfo.getByDate(info, CONSULTANT_SOURCE_TYPE).orElse(new DayInfo());
        dayInfo.day = info;
        dayInfo.dayType = holiday;
        dayInfo.sourceType = CONSULTANT_SOURCE_TYPE;
        DayInfo.persist(dayInfo);
    }
}
