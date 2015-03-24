package cn.com.janssen.dsr;

import org.joda.time.DateTime;

public class Utils {
    public static DateTime startPointOfLastWeek(DateTime timePoint) {
        int numberOfDaysBetweenLastMonday = timePoint.getDayOfWeek() - 1 + 7;
        DateTime lastMonday = timePoint.minusDays(numberOfDaysBetweenLastMonday);
        return new DateTime(lastMonday.getYear(), lastMonday.getMonthOfYear(), lastMonday.getDayOfMonth(), 0, 0, 0);
    }

    public static DateTime endPointOfLastWeek(DateTime timePoint) {
        int numberOfDaysBetweenLastSunday = timePoint.getDayOfWeek();
        DateTime lastSunday = timePoint.minusDays(numberOfDaysBetweenLastSunday);
        return new DateTime(lastSunday.getYear(), lastSunday.getMonthOfYear(), lastSunday.getDayOfMonth(), 23, 59, 59);
    }
}
