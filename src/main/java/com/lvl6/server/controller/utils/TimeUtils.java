package com.lvl6.server.controller.utils;

import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TimeUtils {
  
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

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
    DateTime dOne = new DateTime(d1);
    DateTime dTwo = new DateTime(d2);
    Period interim = new Period(dOne, dTwo);
    
    return interim.getMinutes();
  }
  
  //not sure if DateTime works, as well.
  public boolean isFirstEarlierThanSecond(Date one, Date two) {
	  LocalDate ldOne = new LocalDate(one);
	  LocalDate ldTwo = new LocalDate(two);
	  
	  return ldOne.isBefore(ldTwo);
  }
  
  public int getDayOfWeekPst(Date d) {
  	DateTime dt = new DateTime(d, PST);
  	return dt.getDayOfWeek();
  }
  
  public int getDayOfMonthPst(Date d) {
  	DateTime dt = new DateTime(d, PST);
  	return dt.getDayOfMonth();
  }
  
  public Date createPstDateSetHour(int hour, int minutesAddend) {
  	DateTime dt = new DateTime(PST);
  	log.info("nowish in pst (Date form) " + dt.toDate() + "\t (DateTime form) " + dt);
  	MutableDateTime mdt = dt.withTimeAtStartOfDay().toMutableDateTime();
  	mdt.setHourOfDay(hour);
  	mdt.addMinutes(minutesAddend);
  	Date createdDate = mdt.toDate();
  	log.info("date with hour set: (Date form) " + createdDate);
  	return createdDate;
  }
  
}