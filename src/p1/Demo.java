package p1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
public class Demo {

	public static void main(String[] args) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
		cal.add(Calendar.DATE, 0);

		System.out.println(dateFormat.format( cal.getTime()));
		
		String[] availableIDs = TimeZone.getAvailableIDs();
		
		/*for (String s : availableIDs)
			System.out.println(s);*/
	}

}
