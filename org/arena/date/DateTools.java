package org.arena.date;

import java.text.DateFormat.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public interface DateTools {
	static long millisecond = 1;
	static long second = millisecond*1000;
	static long minute = second*60;
	static long hour = minute*60;
	static long day = hour*24;
	static long year = day*365;
	
	static SimpleDateFormat SQL_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat SQL_DATE = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getMonthToMM(String month) {
		String compare = month.toLowerCase();
		if (compare.equals("dec") || compare.equals("december")) return "12";
		if (compare.equals("nov") || compare.equals("november")) return "11";
		if (compare.equals("oct") || compare.equals("october")) return "10";
		if (compare.equals("sep") || compare.equals("september")) return "09";
		if (compare.equals("aug") || compare.equals("august")) return "08";
		if (compare.equals("jul") || compare.equals("july")) return "07";
		if (compare.equals("jun") || compare.equals("june")) return "06";
		if (compare.equals("may") || compare.equals("may")) return "05";
		if (compare.equals("apr") || compare.equals("april")) return "04";
		if (compare.equals("mar") || compare.equals("march")) return "03";
		if (compare.equals("feb") || compare.equals("february")) return "02";
		if (compare.equals("jan") || compare.equals("january")) return "01";
		return null;	
	}
	
	public static String getMMToMonth(String MM) {
		String compare = MM.replaceAll("[^0-9]", "").trim();
		if (compare.isEmpty()) return null;
		
		if (MM.equals("12")) return "Dec";
		if (MM.equals("11")) return "Nov";
		if (MM.equals("10")) return "Oct";
		if (MM.equals("09") || MM.equals("9")) return "Sep";
		if (MM.equals("08") || MM.equals("8")) return "Aug";
		if (MM.equals("07") || MM.equals("7")) return "Jul";
		if (MM.equals("06") || MM.equals("6")) return "Jun"; 
		if (MM.equals("05") || MM.equals("5")) return "May";
		if (MM.equals("04") || MM.equals("4")) return "Apr";
		if (MM.equals("03") || MM.equals("3")) return "Mar";
		if (MM.equals("02") || MM.equals("2")) return "Feb";
		if (MM.equals("01") || MM.equals("1")) return "Jan";
		return null;
		
	}
	
	public static String getDateString() {
		return getDateString(Calendar.getInstance());
	}
	
	public static String getDateString(Calendar cal) {
		return getDateString(cal, "-");
	}
	
	public static String getDateString(Calendar cal, String delimeter) {
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int mo = cal.get(Calendar.MONTH)+1;
		
		String y = String.valueOf(year);
		if (y.length() == 2) y = "20"+y;
		String d = String.valueOf(day);
		if (d.length() < 2) d = "0"+d;
		String m = String.valueOf(mo);
		if (m.length() < 2) m = "0"+m;
		return y + delimeter + m + delimeter + d;
	}

	public static String getTimeString(Calendar cal, String delimeter) {
		String HOUR = ""+cal.get(Calendar.HOUR_OF_DAY);
		String MIN =  ""+cal.get(Calendar.MINUTE);
		
		HOUR = HOUR.length() > 1 ? HOUR : "0"+HOUR;
		MIN = MIN.length() > 1 ? MIN : "0"+MIN;
		return ""+HOUR+delimeter+MIN+delimeter+"00";
	}

	public static Calendar getCalendar(TimeZone timezone) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(timezone);
		return makeCalendar(cal.get(Calendar.YEAR),
							cal.get(Calendar.MONTH)+1,
							cal.get(Calendar.DATE),
							cal.get(Calendar.HOUR_OF_DAY),
							cal.get(Calendar.MINUTE),
							cal.get(Calendar.SECOND));
	}
	
	public static Calendar makeCalendar(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Field.YEAR.getCalendarField(), year);
		c.set(Field.MONTH.getCalendarField(), month-1);
		c.set(Field.DAY_OF_MONTH.getCalendarField(), day);
		return c;
	}
	
	public static Calendar makeCalendar(int year, int month, int day, int hourOfDay, int min, int sec) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Field.YEAR.getCalendarField(), year);
		c.set(Field.MONTH.getCalendarField(), month-1);
		c.set(Field.DAY_OF_MONTH.getCalendarField(), day);
		c.set(Field.HOUR_OF_DAY0.getCalendarField(), hourOfDay);
		c.set(Field.MINUTE.getCalendarField(), min);
		c.set(Field.SECOND.getCalendarField(), sec);
		return c;
	}
	
	public static Calendar makeCalendar(int year, int month, int day, int hour, int min, String ampm, TimeZone zone) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTimeZone(zone);
		c.set(Field.YEAR.getCalendarField(), year);
		c.set(Field.MONTH.getCalendarField(), month-1);
		c.set(Field.DAY_OF_MONTH.getCalendarField(), day);
		c.set(Field.HOUR0.getCalendarField(), hour == 12 ? 0 : hour);
		c.set(Field.MINUTE.getCalendarField(), min);
		c.set(Field.AM_PM.getCalendarField(), ampm.toLowerCase().equals("am") ? Calendar.AM : Calendar.PM);
		
		return c;
	}
	
	public static Calendar makeCalendar(String yyyymmdd, String delimeter) {
		String[] vals = yyyymmdd.split(delimeter);
		return makeCalendar(Integer.valueOf(vals[0]), Integer.valueOf(vals[1]), Integer.valueOf(vals[2]));
	}
	
	
	public static void setTime(Calendar cal, String hhmmss) {
		String[] fields = hhmmss.split(":");
		cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(fields[0].replace(":", "")));
		cal.set(Calendar.MINUTE, Integer.valueOf(fields[1].replace(":", "")));
		cal.set(Calendar.SECOND, Integer.valueOf(fields[2].replace(":", "")));
		cal.set(Calendar.MILLISECOND, 0);
	}
	
	public static Calendar inc(Calendar cal) {
		return inc(cal, 1);
	}
	
	public static Calendar dec(Calendar cal) {
		return dec(cal, 1);
	}
	
	public static Calendar inc(Calendar cal, int numOfDays) {
		Calendar original = (Calendar)cal.clone();
		
		Calendar clone = Calendar.getInstance();
		clone.setTimeInMillis(original.getTimeInMillis());
		clone.add(Calendar.DAY_OF_MONTH, numOfDays);
		
		cal.setTimeInMillis(cal.getTimeInMillis() + numOfDays*day);
		cal.set(clone.get(Calendar.YEAR), clone.get(Calendar.MONTH), clone.get(Calendar.DATE));
		
		return cal;
	}
	
	
	public static Calendar dec(Calendar cal, int numOfDays) {
		return inc(cal, -numOfDays);
	}
	
	
	public static Calendar incYear(Calendar cal) {
		return incYear(cal, 1);
	}
	
	public static Calendar decYear(Calendar cal) {
		return decYear(cal, 1);
	}
	
	public static Calendar incYear(Calendar cal, int numOfYears) {
		Calendar clone = Calendar.getInstance();
		clone.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
		clone.setTimeInMillis(cal.getTimeInMillis());
		clone.add(Calendar.YEAR, numOfYears);
		cal.setTimeInMillis(clone.getTimeInMillis());
		cal.set(clone.get(Calendar.YEAR), clone.get(Calendar.MONTH), clone.get(Calendar.DATE));

		return cal;
	}
	
	public static Calendar decYear(Calendar cal, int numOfYears) {
		return incYear(cal, -numOfYears);
	}

	public static Calendar inc365(Calendar cal) {
		return incYear(cal, 1);
	}
	
	public static Calendar dec365(Calendar cal) {
		return decYear(cal, 1);
	}
	
	public static Calendar inc365(Calendar cal, int numOfYears) {
		cal.set(Field.DAY_OF_MONTH.getCalendarField(), 
				cal.get(Field.DAY_OF_MONTH.getCalendarField())+(365*numOfYears));
		return cal;
	}
	
	public static Calendar dec365(Calendar cal, int numOfYears) {
		cal.set(Field.DAY_OF_MONTH.getCalendarField(), 
				cal.get(Field.DAY_OF_MONTH.getCalendarField())-(365*numOfYears));
		return cal;
	}
	
	public static Boolean sameDay(Calendar cal1, Calendar cal2) {
		int day1 = cal1.get(Field.DAY_OF_MONTH.getCalendarField());
		int day2 = cal2.get(Field.DAY_OF_MONTH.getCalendarField());
		
		int mo1 = cal1.get(Field.MONTH.getCalendarField());
		int mo2 = cal2.get(Field.MONTH.getCalendarField());
		
		int yr1 = cal1.get(Field.YEAR.getCalendarField());
		int yr2 = cal2.get(Field.YEAR.getCalendarField());
		
		if (day1 == day2 && mo1 == mo2 && yr1 == yr2) return true;
		return false;
	}
	
	

	public static boolean onOrAfter(Calendar comp, Calendar onOrAfter) {
		return sameDay(comp, onOrAfter) || comp.after(onOrAfter);
		
	}
	
	
	public static boolean onOrBefore(Calendar comp, Calendar onOrBefore) {
		return sameDay(comp, onOrBefore) || comp.before(onOrBefore);
	}
	
	
	/**
	 * <p> A method to determine if a Calendar day falls on a weekend. </p>
	 * 
	 * @param cal Calendar to testf
	 * @return True if DAY_OF_WEEK is Saturday or Sunday, False otherwise
	 */
	public static boolean isWeekend(Calendar cal) {
		return (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
			 || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
	}
	
	public static String getSqlDateTimeString() {
		return SQL_DATE_TIME.format(LocalDateTime.now());
	}
	
	public static String getSqlDateTimeString(Calendar cal) {
		return SQL_DATE_TIME.format(cal.getTime());
	}
	
	public static String getSqlDateString() {
		return SQL_DATE.format(LocalDateTime.now());
	}
	
	public static String getSqlTimeStamp(TimeZone timeZone) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(timeZone);
		String year = ""+cal.get(Calendar.YEAR);
		String month = ""+(cal.get(Calendar.MONTH)+1);
		String date = ""+cal.get(Calendar.DATE);
		String hour = ""+cal.get(Calendar.HOUR_OF_DAY);
		if (hour.length() < 2) hour = "0"+hour;
		String min = ""+cal.get(Calendar.MINUTE);
		if (min.length() < 2) min = "0"+min;
		String sec = ""+cal.get(Calendar.SECOND);
		if (sec.length() < 2) sec = "0"+sec;
		String milli = ""+cal.get(Calendar.MILLISECOND);
		
		String timeStamp = year+"-"+month+"-"+date;
		timeStamp += "-"+hour+":"+min+":"+sec+"."+milli;
		return timeStamp;
	}

	public static Calendar clone(Calendar start) {
		return (Calendar)start.clone();
		
	}
}
