package malbec.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Utility class to handle manipulation of Date/Time.
 * 
 */
public class DateTimeUtil {

    private static final long MILLIS_PER_HOUR = 1000 * 60 * 60;
    private static final long MILLIS_PER_MINUTE = 1000 * 60;

    // Time
    private static final String TIME_SECOND = "HH:mm:ss";
    private static final String TIME_MINUE = "HH:mm";

    // Date
    private static final String UTC_DATE = "yyyyMMdd";
    static final String BB_DATE = "MM/dd/yy";
    static final String EXCEL_DATE = "M/dd/yy";
    static final String LOCALDATE_DATE = "yyyy-MM-dd";

    // Date and Time
    private static final String UTC_TIMESTAMP = "yyyyMMdd-HH:mm:ss.SSS";
    private static final String UTC_DATETIME = "yyyyMMdd-HH:mm:ss";

    private static final String BB_CMF_TIMESTAMP = "yyyyMMddHHmmss";

    static final String CSHARP_DATETIME = "M/d/yyyy hh:mm:ss aa";
    static final String JAVA_DATETIME = "EEE MMM dd HH:mm:ss zzz yyyy";
    static final String EXCEL_DATETIME = "M/dd/yy/hh/mm/ss";
    static final String AMERICAN_DATETIME = "yyyy/MM/dd HH:mm:ss";
    static final String AMERICAN_TIMESTAMP_TIMEZONE = "yyyy/MM/dd HH:mm:ss.SSS ZZ";
    // Since we are calling toString on the variant, the time from Redi is just Java Date
    static final String REDI_DATETIME = JAVA_DATETIME;
    //                                       
    // Mon Jan 01 09:09:22 EST 1990

    private static final String[] GUESS_ORDER = { AMERICAN_DATETIME, CSHARP_DATETIME, EXCEL_DATETIME,
        EXCEL_DATE, LOCALDATE_DATE, UTC_DATETIME, AMERICAN_TIMESTAMP_TIMEZONE };

    private static final String[] GUESS_TIME_ORDER = { TIME_SECOND, TIME_MINUE };

    private static final String[] GUESS_DATE_ORDER = { LOCALDATE_DATE, UTC_DATE, BB_DATE, EXCEL_DATE };

    public static void freezeTimeNow() {
        freezeTime(System.currentTimeMillis());
    }

    public static void freezeTime(long freezeTime) {
        DateTimeUtils.setCurrentMillisFixed(freezeTime);
    }

    public static void freezeTime(DateTime freezeTime) {
        freezeTime(freezeTime.getMillis());
    }

    public static void freezeTime(String freezeTime) {
        freezeTime(guessDateTime(freezeTime).getMillis());
    }

    public static void thawTime() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    public static int minutesToWaitFor(int interval) {
        DateTime now = new DateTime();
        int minutes = now.getMinuteOfHour();
        int normalizedMinutes = minutes % interval;
        int minutesToWait = interval - normalizedMinutes;

        return minutesToWait;
    }

    /**
     * Return the current date as a string in 'yyyyMMdd' format.
     * 
     * @return
     */
    public static String getDateAsString() {
        return getDateAsString(System.currentTimeMillis());
    }

    /**
     * Return the specified date in 'yyyyMMdd' format.
     * 
     * @param date
     * @return
     */
    public static String getDateAsString(LocalDate localDate) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(UTC_DATE);
        return fmt.print(localDate);
    }

    /**
     * Return the specified date in 'yyyyMMdd' format.
     * 
     * @param date
     * @return
     */
    public static String getDateAsString(Date date) {
        return getDateAsString(date.getTime());
    }

    /**
     * Return the specified date in 'yyyyMMdd' format.
     * 
     * @param milliDate
     * @return
     */
    public static String getDateAsString(long milliDate) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(UTC_DATE);
        return fmt.print(milliDate);
    }

    public static String formatTimestampUTZ(Date jdkDate) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(UTC_TIMESTAMP);
        fmt = fmt.withZone(DateTimeZone.UTC);
        return fmt.print(jdkDate.getTime());
    }

    public static String formatCSharp(DateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(CSHARP_DATETIME);
        return fmt.print(dateTime);
    }

    public static String format(DateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(AMERICAN_DATETIME);
        return fmt.print(dateTime);
    }

    public static String formatTimestampTimeZone(DateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(AMERICAN_TIMESTAMP_TIMEZONE);
        return fmt.print(dateTime);
    }

    public static String formatNow() {
        return format(new DateTime());
    }

    public static long nowMillis() {
        return DateTimeUtils.currentTimeMillis();
    }

    /**
     * Format the date in the Bloomberg MM/DD/YY format.
     * 
     * @param date
     * @return
     */
    public static String formatBBDate(LocalDate date) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(BB_DATE);
        return fmt.print(date);
    }

    public static String formatCSharp(LocalDate localDate) {
        return formatCSharp(localDate.toDateTimeAtStartOfDay());
    }

    /**
     * Return a <code>LocalDate</code> from a String in the format of 'yyyyMMdd'.
     * 
     * @param strDate
     * @return
     */
    public static LocalDate getLocalDate(String strDate) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(UTC_DATE);
        DateTime dt = fmt.parseDateTime(strDate);
        return dt.toLocalDate();
    }

    public static DateTime getDateTime(String strDateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(UTC_TIMESTAMP);
        fmt = fmt.withZone(DateTimeZone.UTC);

        return fmt.parseDateTime(strDateTime);
    }

    /**
     * From a formatted Redi date (Java Date) create a date using the passed in time and the current date.
     * 
     * @param strDateTime
     * @return
     */
    public static DateTime getCurrentFromTime(String strDateTime) {
        Date javaDate = parseDate(strDateTime, REDI_DATETIME);
        if (javaDate == null) {
            return null;
        }
        DateTime dt = new DateTime(javaDate);

        LocalDate ld = new LocalDate();

        return new DateTime(ld.getYear(), ld.getMonthOfYear(), ld.getDayOfMonth(), dt.getHourOfDay(), dt
            .getMinuteOfHour(), dt.getSecondOfMinute(), 0);
    }

    /**
     * From a formatted Redi date (Java Date) create a date using the passed in time and the current date.
     * 
     * @param strDateTime
     * @return
     */
    public static LocalDate getDateFromJavaString(String strDateTime) {
        Date javaDate = parseDate(strDateTime, REDI_DATETIME);
        if (javaDate == null) {
            return null;
        }
        DateTime dt = new DateTime(javaDate);

        return new LocalDate(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth());
    }

    /**
     * Parse the date string using UTC format (yyyyMMdd-HH:mm:ss).
     * 
     * @param strDateTime
     * @return a java.util.Date
     */
    public static Date getDate(String strDateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(UTC_DATETIME);
        fmt = fmt.withZone(DateTimeZone.UTC);

        DateTime dt = fmt.parseDateTime(strDateTime.substring(0, 17));

        return new Date(dt.getMillis());
    }

    /**
     * Parse the string using the Bloomberg MM/DD/YY format.
     * 
     * @param dateStr
     * @return
     */
    public static LocalDate parseBBDate(String dateStr) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(BB_DATE);

        DateTime dt = fmt.parseDateTime(dateStr);
        return dt.toLocalDate();
    }

    public static Date parseBFTimestamp(String dateStr) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(BB_CMF_TIMESTAMP);

        DateTime dt = fmt.parseDateTime(dateStr);

        return new Date(dt.getMillis());
    }

    public static DateTime parseTimestampTimeZone(String strDateTime) {
        return parseDateTime(strDateTime, AMERICAN_TIMESTAMP_TIMEZONE);
    }

    public static Date getDate(LocalDate localDate) {
        return new Date(localDate.toDateTimeAtStartOfDay().getMillis());
    }

    /**
     * Parse the date using the default JDK format. 'EEE MMM dd HH:mm:ss zzz yyyy'
     * 
     * @param strDateTime
     * @return
     */
    public static Date getJdkDate(String strDateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATETIME);

            return sdf.parse(strDateTime);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static DateTime combineDateTime(LocalDate d, DateTime t, DateTimeZone tz) {
        DateTime combined = new DateTime(d.getYear(), d.getMonthOfYear(), d.getDayOfMonth(),
            t.getHourOfDay(), t.getMinuteOfHour(), t.getSecondOfMinute(), t.getMillisOfSecond(), tz);

        return combined;
    }

    public static DateTime guessDateTime(String strDateTime) {
        if (strDateTime == null) {
            return null;
        }
        // Fall back to using standard Java parser as Joda-Time cannot handle this
        // DateTime dt = parseDateTime(strDateTime,JAVA_DATETIME);
        DateTime dt = null;

        for (int i = 0; i < GUESS_ORDER.length && dt == null; i++) {
            dt = parseDateTime(strDateTime, GUESS_ORDER[i]);
        }

        if (dt == null) {
            // try the default jdk parser since Joda-Time cannot handle short timezones
            Date d = parseDate(strDateTime, JAVA_DATETIME);
            if (d != null) {
                return new DateTime(d);
            }
        }

        return dt;
    }

    public static LocalTime guessTime(String strTime) {
        if (strTime == null) {
            return null;
        }
        DateTime dt = null;

        for (int i = 0; i < GUESS_TIME_ORDER.length && dt == null; i++) {
            dt = parseDateTime(strTime, GUESS_TIME_ORDER[i]);
        }

        if (dt != null) {
            return dt.toLocalTime();
        }

        return null;
    }

    public static LocalDate guessDate(String strDate) {
        if (strDate == null) {
            return null;
        }
        DateTime dt = null;

        for (int i = 0; i < GUESS_DATE_ORDER.length && dt == null; i++) {
            dt = parseDateTime(strDate, GUESS_DATE_ORDER[i]);
        }

        if (dt != null) {
            return dt.toLocalDate();
        }

        return null;
    }

    static DateTime parseDateTime(String strDateTime, String pattern) {
        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
            DateTime dt = fmt.parseDateTime(strDateTime);
            return dt;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Date parseDate(String strDateTime, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);

            return sdf.parse(strDateTime);
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean isWeekend(LocalDate ld) {
        int dayOfWeek = ld.dayOfWeek().get();

        return (DateTimeConstants.SATURDAY == dayOfWeek || DateTimeConstants.SUNDAY == dayOfWeek);
    }

    public static DateTime[] determineWeeklyRange(String startDay, String endDay) {
        // Parse the days
        LocalDate today = new LocalDate();
        LocalDate.Property day = today.property(DateTimeFieldType.dayOfWeek());
        LocalDate startDate = day.setCopy(startDay);
        LocalDate endDate = day.setCopy(endDay);

        // Make sure that the start and end are in the correct order
        if (startDate.isAfter(endDate)) {
            startDate = startDate.minusWeeks(1);
        }

        return new DateTime[] { startDate.toDateTimeAtStartOfDay(), toEndOfDay(endDate) };
    }

    private static DateTime toEndOfDay(LocalDate endDate) {
        DateTime eod = endDate.toDateTimeAtStartOfDay();
        eod = eod.plusHours(23).plusMinutes(59).plusSeconds(59);

        return eod;
    }

    public static DateTime[] determineWeeklyRange(String startDay, String startTime, String endDay,
        String endTime) {

        LocalTime startTimeGuess = guessTime(startTime);
        LocalTime endTimeGuess = guessTime(endTime);

        return determineWeeklyRange(startDay, startTimeGuess, endDay, endTimeGuess);
    }

    public static DateTime[] determineWeeklyRange(String startDay, LocalTime startTime, String endDay,
        LocalTime endTime) {
        DateTime[] defaultRange = determineWeeklyRange(startDay, endDay);

        DateTime startDateTime = combineDateTime(defaultRange[0].toLocalDate(), startTime.toDateTimeToday(),
            defaultRange[0].getZone());
        DateTime endDateTime = combineDateTime(defaultRange[1].toLocalDate(), endTime.toDateTimeToday(),
            defaultRange[1].getZone());

        defaultRange[0] = startDateTime;
        defaultRange[1] = endDateTime;

        return defaultRange;
    }

    public static ExecutorConfig scheduleEvery(int every, String startingAt, TimeUnit timeUnit) {
        return scheduleEvery(every, startingAt, timeUnit, TimeUnit.MILLISECONDS);
    }

    public static ExecutorConfig scheduleEvery(int every, String startingAt, TimeUnit timeUnit,
        TimeUnit precision) {
        LocalTime startTime = guessTime(startingAt);
        DateTime now = new DateTime();

        DateTime fullStartTime = startTime.toDateTimeToday();
        long period = 0;

        switch (timeUnit) {
            case HOURS:
                period = convertHoursToMillis(every);
                break;
            case MINUTES:
                period = convertMinutesToMillis(every);
                break;

            default:
                throw new IllegalArgumentException("Cannot handle " + timeUnit + " time unit");
        }

        // determine initial time
        long initialTime = 0;
        if (fullStartTime.isAfter(now)) {
            initialTime = fullStartTime.getMillis() - now.getMillis();
        } else {
            // we are past the start time, load after next period
            long diff = Math.abs(fullStartTime.getMillis() - now.getMillis());
            long amountToAdd = (diff / period * period) + period;

            fullStartTime = fullStartTime.plusMillis((int) amountToAdd);
            initialTime = fullStartTime.getMillis() - now.getMillis();
        }

        switch (precision) {
            case MILLISECONDS:
                break;
            case SECONDS:
                initialTime = initialTime / 1000 * 1000;
                period = period / 1000 * 1000;
                break;
            case MINUTES:
                initialTime = initialTime / (1000 * 60) * (1000 * 60);
                period = period / (1000 * 60) * (1000 * 60);
                break;
        }

        return new ExecutorConfig(initialTime, period, TimeUnit.MILLISECONDS);
    }

    private static long convertHoursToMillis(int everyHour) {
        return everyHour * MILLIS_PER_HOUR;
    }

    private static long convertMinutesToMillis(int everyMinute) {
        return everyMinute * MILLIS_PER_MINUTE;
    }
}
