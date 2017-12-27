package com.kite.aws.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.kite.aws.inflation.InflationOrder;

public class Util {
	
	public static InflationOrder createInflationOrder(String data) {
	    InflationOrder order = null;
	    
		if (data != null) {
			String[] tokens = data.split("-");
			//if ((tokens != null) && (tokens.length == 6)) {
				if ((tokens != null) && (tokens.length == 7)) {
				DecimalFormat df = new DecimalFormat("#.##");
				double sum =  Double.valueOf(df.format(Double.parseDouble(tokens[5])));
				String operator = tokens[6];
				//order = new InflationOrder(tokens[0], tokens[1], tokens[2], Boolean.valueOf(tokens[3]), tokens[4],sum,">=");
				order = new InflationOrder(tokens[0], tokens[1], tokens[2], Boolean.valueOf(tokens[3]), tokens[4],sum,operator);
			} 
		}
		return order;
	}
	
	
	public static String getTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		//System.out.println(dateFormat.format( cal.getTime()));
		return dateFormat.format(cal.getTime());
	}
	
	public static GregorianCalendar getTime(boolean robo) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		//System.out.println(dateFormat.format( cal.getTime()));
		//return dateFormat.format(cal.getTime());
		return cal;
	}
	
	

}
