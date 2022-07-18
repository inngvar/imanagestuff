package org.manage.service;

import com.google.common.collect.Lists;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.manage.service.dto.YearInfo;
import org.manage.service.restclient.ConsultantClient;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConsultantHolidayService {

    static List<String> MONTHS = Lists.newArrayList(
        "Январь",
        "Февраль",
        "Март",
        "Апрель",
        "Май",
        "Июнь",
        "Июль",
        "Август",
        "Сентябрь",
        "Октябрь",
        "Ноябрь",
        "Декабрь"
    );

    @RestClient
    ConsultantClient consultantClient;

    public YearInfo getByYear(int year){
        if(year <2014){
            throw new RuntimeException("Parser for year before 2014 not imlemented");
        }
        final String calendar = consultantClient.calendar(String.valueOf(year));
        Document parsed = Jsoup.parse(calendar);
        Elements weekendsElements =  parsed.select("td.weekend:not(.holiday)");
        Elements preholidaysElements = parsed.select("td.preholiday");
        Elements holidaysElements = parsed.select("td.holiday.weekend");

        YearInfo yearInfo = new YearInfo();
        yearInfo.year = year;
        yearInfo.weekends = weekendsElements.stream()
            .map(e -> LocalDate.of(year, extractMonth(e), extractDay(e)))
            .collect(Collectors.toList());

        yearInfo.holidays = holidaysElements.stream()
            .map(e -> LocalDate.of(year, extractMonth(e), extractDay(e)))
            .collect(Collectors.toList());

        yearInfo.preHolidays = preholidaysElements.stream()
            .map(e -> LocalDate.of(year, extractMonth(e), extractDay(e)))
            .collect(Collectors.toList());
        yearInfo.info.totalHolidays = yearInfo.holidays.size();
        yearInfo.info.totalPreHolidays = yearInfo.preHolidays.size();
        yearInfo.info.totalWeekends = yearInfo.weekends.size();
        return  yearInfo;
    }

    static Integer extractMonth(Element element) {
        return MONTHS.indexOf(element.parent().parent().parent().selectFirst(".month").text()) + 1;
    }

    static Integer extractDay(Element element) {
        return Integer.valueOf(element.ownText());
    }

}
