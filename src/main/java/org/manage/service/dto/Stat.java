package org.manage.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Stat {
    public Integer totalWeekends;
    public Integer totalHolidays;
    public Integer totalPreHolidays;
}
