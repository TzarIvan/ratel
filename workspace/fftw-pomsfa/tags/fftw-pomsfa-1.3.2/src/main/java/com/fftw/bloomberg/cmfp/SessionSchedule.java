package com.fftw.bloomberg.cmfp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import quickfix.ConfigError;
import quickfix.DayConverter;
import quickfix.FieldConvertError;
import quickfix.SystemTime;

/**
 * Corresponds to SessionTime in C++ code
 */
class SessionSchedule
{
    private static final int NOT_SET = -1;

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2})(.*)");

    private final TimeEndPoint startTime;

    private final TimeEndPoint endTime;

    SessionSchedule (CmfSessionSettings settings, CmfSessionID sessionID) throws ConfigError,
        FieldConvertError
    {
        boolean startDayPresent = settings.isSetting(sessionID, CmfSession.SETTING_START_DAY);
        boolean endDayPresent = settings.isSetting(sessionID, CmfSession.SETTING_END_DAY);

        if (startDayPresent && !endDayPresent)
        {
            throw new ConfigError("Session " + sessionID + ": StartDay used without EndDay");
        }

        if (endDayPresent && !startDayPresent)
        {
            throw new ConfigError("Session " + sessionID + ": EndDay used without StartDay");
        }

        TimeZone defaultTimeZone = getDefaultTimeZone(settings, sessionID);

        // boolean weeklySession = startDayPresent && endDayPresent;
        //
        //
        // String startTimeString = settings.getString(sessionID,
        // Session.SETTING_START_TIME);
        // Matcher matcher = TIME_PATTERN.matcher(startTimeString);
        // if (!matcher.find()) {
        // throw new ConfigError("Session " + sessionID + ": could not parse
        // start time '"
        // + startTimeString + "'.");
        // }
        // Calendar localTime = SystemTime.getUtcCalendar();
        // TimeZone startTimeZone = getTimeZone(matcher.group(4),
        // defaultTimeZone);
        // localTime.setTimeZone(startTimeZone);
        // localTime.set(Calendar.MILLISECOND, 0);
        // localTime.set(Calendar.HOUR_OF_DAY,
        // Integer.parseInt(matcher.group(1)));
        // localTime.set(Calendar.MINUTE, Integer.parseInt(matcher.group(2)));
        // localTime.set(Calendar.SECOND, Integer.parseInt(matcher.group(3)));
        // Calendar startTime = SystemTime.getUtcCalendar();
        // startTime.setTime(localTime.getTime());
        // int startDay = NOT_SET;
        // if (weeklySession) {
        // startDay = getDay(settings, sessionID, Session.SETTING_START_DAY,
        // NOT_SET);
        // }

        startTime = getTimeEndPoint(settings, sessionID, defaultTimeZone,
            CmfSession.SETTING_START_TIME, CmfSession.SETTING_START_DAY);
        endTime = getTimeEndPoint(settings, sessionID, defaultTimeZone,
            CmfSession.SETTING_END_TIME, CmfSession.SETTING_END_DAY);
    }

    private TimeEndPoint getTimeEndPoint (CmfSessionSettings settings, CmfSessionID sessionID,
        TimeZone defaultTimeZone, String timeSetting, String daySetting) throws ConfigError,
        FieldConvertError
    {

        Matcher matcher = TIME_PATTERN.matcher(settings.getString(sessionID, timeSetting));
        if (!matcher.find())
        {
            throw new ConfigError("Session " + sessionID + ": could not parse time '"
                + settings.getString(sessionID, timeSetting) + "'.");
        }

        TimeZone timeZone = getTimeZone(matcher.group(4), defaultTimeZone);

        Calendar localTime = SystemTime.getUtcCalendar();
        localTime.setTimeZone(timeZone);

        localTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(1)));
        localTime.set(Calendar.MINUTE, Integer.parseInt(matcher.group(2)));
        localTime.set(Calendar.SECOND, Integer.parseInt(matcher.group(3)));

        int scheduleDay = getDay(settings, sessionID, daySetting, NOT_SET);
        if (scheduleDay != NOT_SET)
        {
            localTime.set(Calendar.DAY_OF_WEEK, scheduleDay);
        }

        Calendar utcTime = SystemTime.getUtcCalendar();
        utcTime.setTime(localTime.getTime());

        return new TimeEndPoint(scheduleDay == NOT_SET ? NOT_SET : utcTime
            .get(Calendar.DAY_OF_WEEK), utcTime, timeZone);
    }

    private TimeZone getDefaultTimeZone (CmfSessionSettings settings, CmfSessionID sessionID)
        throws ConfigError, FieldConvertError
    {
        TimeZone sessionTimeZone;
        if (settings.isSetting(sessionID, CmfSession.SETTING_TIMEZONE))
        {
            String sessionTimeZoneID = settings.getString(sessionID, CmfSession.SETTING_TIMEZONE);
            sessionTimeZone = TimeZone.getTimeZone(sessionTimeZoneID);
            if ("GMT".equals(sessionTimeZone.getID()) && !"GMT".equals(sessionTimeZoneID))
            {
                throw new ConfigError("Unrecognized time zone '" + sessionTimeZoneID
                    + "' for session " + sessionID);
            }
        }
        else
        {
            sessionTimeZone = TimeZone.getTimeZone("UTC");
        }
        return sessionTimeZone;
    }

    private TimeZone getTimeZone (String tz, TimeZone defaultZone)
    {
        return "".equals(tz) ? defaultZone : TimeZone.getTimeZone(tz.trim());
    }

    private class TimeEndPoint
    {
        private final int weekDay;

        private final int hour;

        private final int minute;

        private final int second;

        private final int timeInSeconds;

        private final TimeZone tz;

        public TimeEndPoint (int day, Calendar c, TimeZone tz)
        {
            this(day, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND),
                tz);
        }

        public TimeEndPoint (int day, int hour, int minute, int second, TimeZone tz)
        {
            weekDay = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
            this.tz = tz;
            timeInSeconds = timeInSeconds(hour, minute, second);
        }

        public int getHour ()
        {
            return hour;
        }

        public int getMinute ()
        {
            return minute;
        }

        public int getSecond ()
        {
            return second;
        }

        public String toString ()
        {
            try
            {
                return (isSet(weekDay) ? "d=" + DayConverter.toString(weekDay) + "," : "") + hour
                    + ":" + minute + ":" + second + " " + tz;
            }
            catch (ConfigError e)
            {
                return "ERROR: " + e.getMessage();
            }
        }

        public int getDay ()
        {
            return weekDay;
        }

        public int getTimeInSeconds ()
        {
            return timeInSeconds;
        }

        public boolean equals (Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o instanceof TimeEndPoint)
            {
                TimeEndPoint otherTime = (TimeEndPoint)o;
                return timeInSeconds == otherTime.timeInSeconds;
            }
            return false;
        }

        public int hashCode ()
        {
            assert false : "hashCode not supported";
            return 0;
        }

        public TimeZone getTimeZone ()
        {
            return tz;
        }
    }

    private TimeInterval theMostRecentIntervalBefore (Calendar t)
    {
        TimeInterval timeInterval = new TimeInterval();
        Calendar intervalStart = timeInterval.getStart();

        intervalStart.setTimeInMillis(t.getTimeInMillis());
        intervalStart.set(Calendar.HOUR_OF_DAY, startTime.getHour());
        intervalStart.set(Calendar.MINUTE, startTime.getMinute());
        intervalStart.set(Calendar.SECOND, startTime.getSecond());
        intervalStart.set(Calendar.MILLISECOND, 0);
        if (isSet(startTime.getDay()))
        {
            intervalStart.set(Calendar.DAY_OF_WEEK, startTime.getDay());
            if (intervalStart.getTimeInMillis() > t.getTimeInMillis())
            {
                intervalStart.add(Calendar.WEEK_OF_YEAR, -1);
            }
        }
        else if (intervalStart.getTimeInMillis() > t.getTimeInMillis())
        {
            intervalStart.add(Calendar.DAY_OF_YEAR, -1);
        }

        Calendar intervalEnd = timeInterval.getEnd();
        intervalEnd.setTimeInMillis(intervalStart.getTimeInMillis());
        intervalEnd.set(Calendar.HOUR_OF_DAY, endTime.getHour());
        intervalEnd.set(Calendar.MINUTE, endTime.getMinute());
        intervalEnd.set(Calendar.SECOND, endTime.getSecond());
        intervalEnd.set(Calendar.MILLISECOND, 0);
        if (isSet(endTime.getDay()))
        {
            intervalEnd.set(Calendar.DAY_OF_WEEK, endTime.getDay());
            if (intervalEnd.getTimeInMillis() <= intervalStart.getTimeInMillis())
            {
                intervalEnd.add(Calendar.WEEK_OF_MONTH, 1);
            }
        }
        else if (intervalEnd.getTimeInMillis() <= intervalStart.getTimeInMillis())
        {
            intervalEnd.add(Calendar.DAY_OF_WEEK, 1);
        }

        return timeInterval;

    }

    private static class TimeInterval
    {
        private Calendar start = SystemTime.getUtcCalendar();

        private Calendar end = SystemTime.getUtcCalendar();

        public boolean isContainingTime (Calendar t)
        {
            return t.equals(start) || t.equals(end) || (t.after(start) && t.before(end));
        }

        public String toString ()
        {
            return start.getTime() + " --> " + end.getTime();
        }

        public boolean equals (Object other)
        {
            if (this == other)
            {
                return true;
            }
            if (other instanceof TimeInterval == false)
            {
                return false;
            }
            TimeInterval otherInterval = (TimeInterval)other;
            return start.equals(otherInterval.start) && end.equals(otherInterval.end);
        }

        public int hashCode ()
        {
            assert false : "hashCode not supported";
            return 0;
        }

        public Calendar getStart ()
        {
            return start;
        }

        public Calendar getEnd ()
        {
            return end;
        }

    }

    public boolean isSameSession (Calendar time1, Calendar time2)
    {
        TimeInterval interval1 = theMostRecentIntervalBefore(time1);
        if (!interval1.isContainingTime(time1))
        {
            return false;
        }
        TimeInterval interval2 = theMostRecentIntervalBefore(time2);
        if (!interval2.isContainingTime(time2))
        {
            return false;
        }
        return interval1.equals(interval2);
    }

    private boolean isDailySession ()
    {
        return !isSet(startTime.getDay()) && !isSet(endTime.getDay());
    }

    public boolean isSessionTime ()
    {
        Calendar now = SystemTime.getUtcCalendar();
        TimeInterval interval = theMostRecentIntervalBefore(now);
        return interval.isContainingTime(now);
    }

    public String toString ()
    {
        StringBuffer buf = new StringBuffer();

        SimpleDateFormat dowFormat = new SimpleDateFormat("EEEE");
        dowFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss z");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        TimeInterval ti = theMostRecentIntervalBefore(SystemTime.getUtcCalendar());

        formatTimeInterval(buf, ti, timeFormat, false);

        // Now the localized equivalents, if necessary
        if (startTime.getTimeZone() != SystemTime.UTC_TIMEZONE
            || endTime.getTimeZone() != SystemTime.UTC_TIMEZONE)
        {
            buf.append(" (");
            formatTimeInterval(buf, ti, timeFormat, true);
            buf.append(")");
        }

        return buf.toString();
    }

    private void formatTimeInterval (StringBuffer buf, TimeInterval timeInterval,
        SimpleDateFormat timeFormat, boolean local)
    {
        if (!isDailySession())
        {
            buf.append("weekly, ");
            formatDayOfWeek(buf, startTime.getDay());
            buf.append(" ");
        }
        else
        {
            buf.append("daily, ");
        }

        if (local)
        {
            timeFormat.setTimeZone(startTime.getTimeZone());
        }
        buf.append(timeFormat.format(timeInterval.getStart().getTime()));

        buf.append(" - ");

        if (!isDailySession())
        {
            formatDayOfWeek(buf, endTime.getDay());
            buf.append(" ");
        }
        if (local)
        {
            timeFormat.setTimeZone(endTime.getTimeZone());
        }
        buf.append(timeFormat.format(timeInterval.getEnd().getTime()));
    }

    private void formatDayOfWeek (StringBuffer buf, int dayOfWeek)
    {
        try
        {
            String dayName = DayConverter.toString(dayOfWeek).toUpperCase();
            if (dayName.length() > 3)
            {
                dayName = dayName.substring(0, 3);
            }
            buf.append(dayName);
        }
        catch (ConfigError e)
        {
            buf.append("[Error: unknown day " + dayOfWeek + "]");
        }
    }

    private int getDay (CmfSessionSettings settings, CmfSessionID sessionID, String key,
        int defaultValue) throws ConfigError, FieldConvertError
    {
        return settings.isSetting(sessionID, key) ? DayConverter.toInteger(settings.getString(
            sessionID, key)) : defaultValue;
    }

    private boolean isSet (int value)
    {
        return value != NOT_SET;
    }

    private int timeInSeconds (int hour, int minute, int second)
    {
        return (hour * 3600) + (minute * 60) + second;
    }
}