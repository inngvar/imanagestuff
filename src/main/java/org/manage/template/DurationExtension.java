package org.manage.template;

import io.quarkus.qute.TemplateExtension;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.time.Duration;

@TemplateExtension
public class DurationExtension {

    private final static DecimalFormat f = new DecimalFormat("##.00");

    private static String decOfNum(int number, String[] titles) {
        int [] cases = {2, 0, 1, 1, 1, 2};
        return titles[(number % 100 > 4 && number % 100 < 20) ? 2 : cases[(number % 10 < 5) ? number % 10 : 5]];
    }

    public static String hours(Duration duration){
        return round(duration.getSeconds()/60.0/60.0);
    }

    public static String round(Double totalHours){
        int hours = (int) Math.floor(totalHours);
        int minutes = (int) Math.round(60 * (totalHours - hours));
        String result = hours > 0 ? hours + " " + decOfNum(hours, new String[]{"час", "часа", "часов"}) : "";
        result += minutes > 0 ? " " + minutes + " " + decOfNum(minutes, new String[]{"минута", "минуты", "минут"}) : "";
        return result.trim();
    }

    public static String roundWithSeconds(Double totalHours){
        String result = round(totalHours);
        if (StringUtils.isBlank(result)) {
            int seconds = (int) Math.round(60 * 60 * totalHours);
            String unitString = seconds > 0 ? decOfNum(seconds, new String[]{"секунда", "секунды", "секунд"}) : "";
            result += seconds + " " + unitString;
            return result.trim();
        }
        return result;
    }

}
