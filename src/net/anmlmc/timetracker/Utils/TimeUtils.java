package net.anmlmc.timetracker.Utils;

import com.google.common.collect.Lists;

import java.util.List;

/*******************
 * Created by Anml *
 *******************/
public class TimeUtils {

    public static String simplyTime(int minutes) {

        if (minutes == 0)
            return "0 minutes";

        int years = minutes / 525600;
        minutes = minutes - (years * 52600);

        int months = minutes / 43200;
        minutes = minutes - (months * 43200);

        int weeks = minutes / 10080;
        minutes = minutes - (weeks * 10080);

        int days = minutes / 1440;
        minutes = minutes - (days * 1440);

        int hours = minutes / 60;
        minutes = minutes - (hours * 60);

        List<String> time = Lists.newArrayList();

        if (years != 0)
            time.add(years == 1 ? "1 year" : years + " years");

        if (months != 0)
            time.add(months == 1 ? "1 month" : months + " months");

        if (weeks != 0)
            time.add(weeks == 1 ? "1 week" : weeks + " weeks");

        if (days != 0)
            time.add(days == 1 ? "1 day" : days + " days");

        if (hours != 0)
            time.add(hours == 1 ? "1 hour" : hours + " hours");

        if (minutes != 0)
            time.add(minutes == 1 ? "1 minute" : minutes + " minutes");

        if (time.size() == 1)
            return time.get(0);

        String timeValue = "";
        for (int count = 0; count < time.size(); count++) {
            if (count == 0) {
                timeValue = time.get(0);
            } else if (time.size() - 1 != count) {
                timeValue += ", " + time.get(count);
            } else {
                timeValue += ", and " + time.get(count);
            }
        }

        return timeValue;
    }
}
