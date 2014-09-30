package com.lvl6.server.controller.utils;

import java.util.Date;
import java.util.TimeZone;

//import org.elasticsearch.common.joda.time.Days;
import org.joda.time.Days;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
//import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TimeUtils {
  
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	private static String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY",
		"SATURDAY", "SUNDAY", };

  public static int NUM_MINUTES_LEEWAY_FOR_CLIENT_TIME = 5;
  public static DateTimeZone PST = DateTimeZone
  		.forTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
  
  public boolean isSynchronizedWithServerTime(Date maybeNow) {
    if (null == maybeNow) {
      return false;
    }
    DateTime possiblyNow = new DateTime(maybeNow); 
    DateTime now = new DateTime();
    Period interim = new Period(possiblyNow, now);
    
    int minutesApart = interim.getMinutes();
    if (minutesApart > NUM_MINUTES_LEEWAY_FOR_CLIENT_TIME) {
      //client time is unsynchronized with server time
      return false;
    } else {
      return true;
    }
  }
  
  public int numMinutesDifference(Date d1, Date d2) {
//	  log.info("numMinutesDifference() d1=" + d1);
//	  log.info("numMinutesDifference() d2=" + d2);
	  
	  MutableDateTime mdtOne = new DateTime(d1).toMutableDateTime();
	  mdtOne.setSecondOfMinute(0);
	  DateTime dOne = mdtOne.toDateTime();

	  MutableDateTime mdtTwo = new DateTime(d2).toMutableDateTime();
	  mdtTwo.setSecondOfMinute(0);
	  DateTime dTwo = mdtTwo.toDateTime();

	  Period interim = new Period(dOne, dTwo);

	  return interim.getMinutes();
  }
  
  public int numDaysDifference(Date d1, Date d2) {
//	  log.info("numMinutesDifference() d1=" + d1);
//	  log.info("numMinutesDifference() d2=" + d2);
	  /*
	  MutableDateTime mdtOne = new DateTime(d1).toMutableDateTime();
	  mdtOne.setSecondOfMinute(0);
	  mdtOne.setMinuteOfHour(0);
	  mdtOne.setHourOfDay(0);
	  DateTime dOne = mdtOne.toDateTime();

	  MutableDateTime mdtTwo = new DateTime(d2).toMutableDateTime();
	  mdtTwo.setSecondOfMinute(0);
	  mdtTwo.setMinuteOfHour(0);
	  mdtTwo.setHourOfDay(0);
	  DateTime dTwo = mdtTwo.toDateTime();
	  */
	  
//	  Period interim = new Period(new DateTime(d1), new DateTime(d2));
//	  return interim.getDays();
	  
	  /* http://stackoverflow.com/questions/3802893/number-of-days-between-two-dates-in-joda-time
	   	
	   	Annoyingly, the withTimeAtStartOfDay answer is wrong, but only occasionally. You want:

		Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays()
		It turns out that "midnight/start of day" sometimes means 1am (daylight savings happen this way in some places), which Days.daysBetween doesn't handle properly.
		
		// 5am on the 20th to 1pm on the 21st, October 2013, Brazil
		DateTimeZone BRAZIL = DateTimeZone.forID("America/Sao_Paulo");
		DateTime start = new DateTime(2013, 10, 20, 5, 0, 0, BRAZIL);
		DateTime end = new DateTime(2013, 10, 21, 13, 0, 0, BRAZIL);
		System.out.println(daysBetween(start.withTimeAtStartOfDay(), end.withTimeAtStartOfDay()).getDays());
		// prints 0
		System.out.println(daysBetween(start.toLocalDate(), end.toLocalDate()).getDays());
		// prints 1
		 
	   */
	  Days days = Days.daysBetween(
		  (new DateTime(d1)).toLocalDate(),
		  (new DateTime(d2)).toLocalDate() );
	  
	  return days.getDays();
  }
  
  //not sure if DateTime works, as well.
  public boolean isFirstEarlierThanSecond(Date one, Date two) {
	  if (null == one && null == two) {
		  log.info("both dates null");
		  return false;
	  } else if (null == one) {
		  log.info("first date null");
		  return true; //TODO: why TRUE??
	  } else if (null == two) {
		  log.info("second date null");
		  return false;
	  }
	  
	  
	  LocalDateTime ldOne = new LocalDateTime(one);
	  LocalDateTime ldTwo = new LocalDateTime(two);
	  
//	  log.info("ldOne=" + ldOne);
//	  log.info("ldTwo=" + ldTwo);
	  
	  return ldOne.isBefore(ldTwo);
  }
  
  public int getDayOfWeek(String dayOfWeekName) {
  	for (int i = 0; i < days.length; i++) {
  		
  		String dow = days[i];
  		if (dow.equals(dayOfWeekName)) {
  			return i + 1; 
  		}
  	}
  	return 0;
  }
  
  public int getDayOfWeekPst(Date d) {
  	DateTime dt = new DateTime(d, PST);
  	return dt.getDayOfWeek();
  }
  
  public int getDayOfMonthPst(Date d) {
  	DateTime dt = new DateTime(d, PST);
  	return dt.getDayOfMonth();
  }
  
  //dayOffset is most likely negative (called from ClanEventPersistentRetrieveUtils.java)
  public Date createPstDate(Date curDate, int dayOffset, int hour, int minutesAddend) {
  	DateTime dt = new DateTime(curDate, PST);
//  	log.info("nowish in pst (Date form) " + dt.toDate() + "\t (DateTime form) " + dt);
  	MutableDateTime mdt = dt.withTimeAtStartOfDay().toMutableDateTime();
  	mdt.addDays(dayOffset);
  	mdt.setHourOfDay(hour);
  	mdt.addMinutes(minutesAddend);
  	
//  	log.info("pstDate created: " + mdt.toDateTime());
  	Date createdDate = mdt.toDate();
//  	log.info("date with hour set: (Date form) " + createdDate);
  	return createdDate;
  }
  
  /**
   * 
   * @param curDate
   * @param minutesAddend Can be negative.
   * @return
   */
  public Date createPstDateAddMinutes(Date curDate, int minutesAddend) {
  	DateTime dt = new DateTime(curDate, PST);
//  	log.info("nowish in pst (Date form) " + dt.toDate() + "\t (DateTime form) " + dt +
//  			"\t originally=" + curDate);
  	
  	MutableDateTime mdt = dt.toMutableDateTime();
  	mdt.addMinutes(minutesAddend);
//  	log.info("pstDate created2: " + mdt.toDateTime());
  	Date createdDate = mdt.toDate();
//  	log.info("date advanced " + minutesAddend + " minutes. date=" + createdDate);
  	return createdDate;
  }
  
  /**
   * 
   * @param curDate
   * @param daysAddend Can be negative.
   * @return
   */
  public Date createDateAddDays(Date curDate, int daysAddend) {
  	DateTime dt = new DateTime(curDate);
  	
  	MutableDateTime mdt = dt.toMutableDateTime();
  	mdt.addDays(daysAddend);
  	Date createdDate = mdt.toDate();
  	
  	return createdDate;
  }
  
  /**
   * 
   * @param curDate
   * @param hoursAddend Can be negative.
   * @return
   */
  public Date createDateAddHours(Date curDate, int hoursAddend) {
  	DateTime dt = new DateTime(curDate);
  	
  	MutableDateTime mdt = dt.toMutableDateTime();
  	mdt.addHours(hoursAddend);
  	Date createdDate = mdt.toDate();
  	
  	return createdDate;
  }
  
  public Date createDateTruncateMillis(Date curDate) {
	  DateTime dt = new DateTime(curDate);
	  
	  MutableDateTime mdt = dt.toMutableDateTime();
	  mdt.setMillisOfSecond(0);
	  Date createdDate = mdt.toDate();
	  
	  return createdDate;
  }
}