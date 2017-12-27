package p1;

import java.time.LocalTime;
import java.time.ZoneId;

public class TimeDemo {
	
	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Calcutta");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalTime now = LocalTime.now(ZONE_ID);
		
		LocalTime two = now.plusSeconds(120l);
		
		
		System.out.println("two.isBefore(now) :: " +two.isBefore(now));
		System.out.println("now.isBefore(two) :: " +now.isBefore(two));
	}

}
