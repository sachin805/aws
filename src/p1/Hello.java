package p1;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class Hello {

	public static void main(String[] args) {
	/*	String s = "nifty10200ce-3-1-nifty9900pe-3-1-750-sell-mis";
		String[]  array =  s.split("-");
		System.out.println(array.length);
		
		for (String i : array)
			System.out.println(i);
		
		PlaceOrderPOJO o = new PlaceOrderPOJO("success","some message");
		Gson gson = new GsonBuilder().create();
		System.out.println(gson.toJson(o));
		
		o = new PlaceOrderPOJO("error","some message");
		System.out.println(gson.toJson(o));*/
		
		Date date = new Date();
		System.out.println(date);
		
		//Date start = new Date();
		
		LocalTime now = LocalTime.now();
		LocalTime start = LocalTime.parse("09:20");
		LocalTime end = LocalTime.parse("22:20");
		
		System.out.println(now.isAfter(start));
		System.out.println(now.isAfter(end));
		
		//TimeZone.getTimeZone("Asia/Calcutta").
		ZoneId  id = ZoneId.of("Asia/Calcutta");
		
		String s = "300-up-buy-nifty17dec";
		String[] array = s.split("-");
		System.out.println(array.length);
		
	}

}
