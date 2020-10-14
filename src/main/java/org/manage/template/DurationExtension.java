package org.manage.template;

import io.quarkus.qute.TemplateExtension;

import java.text.DecimalFormat;
import java.time.Duration;

@TemplateExtension
public class DurationExtension {

    private final static DecimalFormat f = new DecimalFormat("##.00");

    public static String hours(Duration duration){
        return round(duration.getSeconds()/60.0/60.0);
    }

    public static String round(Double totalHours){
        return String.format("%.2f",totalHours);
    }
}
