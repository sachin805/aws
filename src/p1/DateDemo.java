package p1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateDemo {

	public static void main(String[] args) {
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		//System.out.println("Current Date Time : " + dateFormat.format(cal.getTime()));
		
		cal.set(2007, 0, 1);
		System.out.println("Date Time : " + dateFormat.format(cal.getTime()));
		cal.add(Calendar.DATE, 40);
		System.out.println("Date Time : " + dateFormat.format(cal.getTime()));
		System.out.println(cal.get(Calendar.DATE));
		/*
		while (true) {
			cal.add(Calendar.DATE, 10);
			System.out.println("Date Time : " + dateFormat.format(cal.getTime()));
			if (cal.get(Calendar.YEAR) < 2007) {
				continue;
			}
		}*/
		
		int startYear = 2016;
		int startMonth = 0;
		int startDate = 1;
		int step = 20;
		
		cal.set(startYear, startMonth, startDate);
		
		while (true) {
			
			int year = cal.get(Calendar.YEAR);
			if (year > 2016)
				break;
			
		}
	}

}
