package org.manage.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.manage.web.rest.CalendarResource;

import java.time.LocalDate;
import java.util.List;

@RegisterForReflection
public class YearInfo {

    public Integer year;

    public Stat info = new Stat();

    public List<LocalDate> weekends;

    public List<LocalDate> preHolidays;

    public List<LocalDate> holidays;
}
