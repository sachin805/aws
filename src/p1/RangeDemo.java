package p1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RangeDemo {

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date start = new Date(116,0,1);
		Calendar c = Calendar.getInstance();
		c.setTime(start); // Now use today date.
		c.add(Calendar.DATE, 5); // Adding 5 days
		String output = sdf.format(c.getTime());
		System.out.println(output);
		
		
		//System.out.println(start);
		
		
		while (true) {
			c.setTime(start);
			
		}

	}

}
