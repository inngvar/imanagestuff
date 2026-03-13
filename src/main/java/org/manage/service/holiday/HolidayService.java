package org.manage.service.holiday;

import org.manage.domain.DayInfo;
import org.manage.domain.enums.DayType;

import javax.enterprise.context.ApplicationScoped;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.manage.service.holiday.HolidayUpdater.CONSULTANT_SOURCE_TYPE;

@ApplicationScoped
public class HolidayService {

    /**
     * Checks if the given date is a working day.
     * 
     * @param date the date to check
     * @return true if it's a working day, false otherwise
     */
    public boolean isWorkingDay(LocalDate date) {
        return DayInfo.getByDate(date, CONSULTANT_SOURCE_TYPE)
            .map(this::isWorkingDayInfo)
            .orElseGet(() -> isDefaultWorkingDay(date));
    }

    private boolean isWorkingDayInfo(DayInfo dayInfo) {
        return dayInfo.dayType != DayType.HOLIDAY && dayInfo.dayType != DayType.WEEKEND;
    }

    private boolean isDefaultWorkingDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }
}
