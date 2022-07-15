package org.manage.web.rest;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import com.google.common.collect.Lists;
import io.quarkus.tika.TikaContent;
import io.quarkus.tika.TikaParser;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.manage.service.restclient.ConsultantClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Path("/calendar/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CalendarResource {

    @RestClient
    ConsultantClient consultantClient;

    @GET()
    @Path("{year}")
    public Response calendar(@PathParam("year") int year) {

        final String calendar = consultantClient.calendar(String.valueOf(year));
        Document parsed = Jsoup.parse(calendar);
        Elements weekendsElements =  parsed.select("td.weekend:not(.holiday)");
        Elements preholidaysElements = parsed.select("td.preholiday");
        Elements holidaysElements = parsed.select("td.holiday.weekend");
        assert weekendsElements.size() > 100;
        assert weekendsElements.size() < 140;

        assert preholidaysElements.size() > 1;
        assert preholidaysElements.size() < 20;

        YearInfo yearInfo = new YearInfo();
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


        return Response.ok(yearInfo).build();
    }

    public class YearInfo{

        public Stat info = new Stat();

        public List<LocalDate> weekends;

        public List<LocalDate> preHolidays;

        public List<LocalDate> holidays;

    }

    public class Stat {
        public Integer totalWeekends;
        public Integer totalHolidays;
        public Integer totalPreHolidays;
    }

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


    static Integer extractMonth(Element element) {
        return MONTHS.indexOf(element.parent().parent().parent().selectFirst(".month").text()) + 1;
    }

    static Integer extractDay(Element element) {
        return Integer.valueOf(element.ownText());
    }
}
