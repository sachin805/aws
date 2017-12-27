package com.kite.aws.robo;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

import com.kite.aws.order.OrderExecutor;
import static com.kite.aws.login.GlobalLoggerHandler.ROBO_LOGGER;;

public class RoboTimer extends TimerTask {
	
//	private static int cnt = 1;
//	private static double t1,t2,t3,t4,t5,t6,t7;
	private static ArrayBlockingQueue<NiftyQuote> queue;
	
	//private static final String START = "09:20";
	//private static final String END = "15:10";
	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Calcutta");
	//private static final LocalTime  START_TIME = LocalTime.parse("09:20");
	private static final LocalTime  START_TIME = LocalTime.parse("09:30");
	private static final LocalTime  END_TIME = LocalTime.parse("15:00");
	public static boolean flag = true;
	private static final String QUANTITY = "75";
	private static final String PRODUCT = "NRML";
	
	static {
		queue = new ArrayBlockingQueue<NiftyQuote>(7,true);
	}
	
	@Override
	public void run() {
		if (flag) {
		if(timeCheck()) {
		
		NiftyQuote nifty = RoboUtil.getNiftyQuote();
		String code = nifty.getCode();
		
		if (code.equals("success")) {
			try {
				queue.add(nifty);
			} catch(IllegalStateException  e) {
				// means queue is full
				NiftyQuote head = queue.poll();
				queue.add(nifty);
			}
			NiftyQuote[] array = queue.toArray(new NiftyQuote[queue.size()]);
			int length = array.length;
			
			boolean upFirstMin = false;
			boolean downFirstMin = false;
			if (length >= 3) {
				boolean[] reply = firstMinCheck(array);
				upFirstMin = reply[0];
				downFirstMin = reply[1];
			}
			
			boolean upSecondMin = false;
			boolean downSecondMin = false;
			if (length >= 5) {
				boolean[] reply = secondMinCheck(array);
				upSecondMin = reply[0];
				downSecondMin = reply[1];
			}
			
			boolean upThirdMin = false;
			boolean downThirdMin = false;
			if (length >= 7) {
				boolean[] reply = thirdMinCheck(array);
				upThirdMin = reply[0];
				downThirdMin = reply[1];
			}
			
			if (upFirstMin && upSecondMin && upThirdMin) {
				double one  = array[0].getLtp();
				double three = array[2].getLtp();
				
				
				double five  = array[4].getLtp();
				double seven =  array[6].getLtp();
				ROBO_LOGGER.info("Going long.......");
				String c1 = " ( " +three + " " + " - " + one + " )" + (three-one);
				String c2 = " ( " +five + " " + " - " + three + " )" + (five-three);
				String c3 = " ( " +seven + " " + " - " + one + " )" + (seven-one);
				ROBO_LOGGER.info(c1);
				ROBO_LOGGER.info(c2);
				ROBO_LOGGER.info(c3);
				
				goLong(nifty.getLtp());
			}
			
			if (downFirstMin && downSecondMin && downThirdMin) {
				
				double one  = array[0].getLtp();
				double three = array[2].getLtp();
				
				
				double five  = array[4].getLtp();
				double seven =  array[6].getLtp();
				ROBO_LOGGER.info("Going short.......");
				String c1 = " ( " +three + " " + " - " + one + " )" + (three-one);
				String c2 = " ( " +five + " " + " - " + three + " )" + (five-three);
				String c3 = " ( " +seven + " " + " - " + one + " )" + (seven-one);
				ROBO_LOGGER.info(c1);
				ROBO_LOGGER.info(c2);
				ROBO_LOGGER.info(c3);
				
				goShort(nifty.getLtp());
			}
			
		} else {
			
		}
		
		}
		}
	}
	
	private boolean[] firstMinCheck(NiftyQuote[] array) {
		boolean[] reply = new boolean[2];
		
		double t1 = array[0].getLtp();
		double t3 = array[2].getLtp();
		
		double diff = t3-t1;
		// first up movement check 
		if (diff > 0) {
			reply[0] = (diff >= 3);
		}
		//second down movement check 
		if (diff < 0) {
			reply[1] = (Math.abs(diff) >= 3);
		}
		return reply;
	}
	
	private boolean[] secondMinCheck(NiftyQuote[] array) {
		boolean[] reply = new boolean[2];
		
		double t2 = array[2].getLtp();
		double t4 = array[4].getLtp();
		
		double diff = t4-t2;
		// first up movement check 
		if (diff > 0) {
			reply[0] = (diff >= 2);
		}
		//second down movement check 
		if (diff < 0) {
			reply[1] = (Math.abs(diff) >= 2);
		}
		return reply;
	}
	
	private boolean[] thirdMinCheck(NiftyQuote[] array) {
		boolean[] reply = new boolean[2];
		
		double t1 = array[0].getLtp();
		double t6 = array[6].getLtp();
		
		double diff = t6-t1;
		// first up movement check 
		if (diff > 0) {
			reply[0] = (diff >= 9);
		}
		//second down movement check 
		if (diff < 0) {
			reply[1] = (Math.abs(diff) >= 9);
		}
		return reply;
	}
	
	
	private void goLong(double ltp) {
		/* String[] reply = OrderExecutor.exitAllPositions(true);
		if (reply[0].equals("open_position")) {
			ROBO_LOGGER.info("There is already one open position. So not going long!!");
			return;
		}*/
		int floor = (int)ltp;
		int rem = floor % 100;
		int callStrike = 0;
		
		if (rem <= 50) {
			 callStrike = floor - rem;
		} else {
			 callStrike = (floor-rem) + 100;
		}
		String instrument = "NIFTY17OCT" + callStrike + "CE";
	//	String product = "MIS";
		//String quantity = "300";
		OrderExecutor.placeSignleOrder(instrument, PRODUCT, QUANTITY, false);
	}
	
	private void goShort(double ltp) {
		/*String[] reply = OrderExecutor.exitAllPositions(true);
		if (reply[0].equals("open_position")) {
			ROBO_LOGGER.info("There is already one open position. So not going short!!");
			return;
		}*/
		int ceil = (int)ltp;
		int rem = ceil % 100;
		int putStrike = 0;
		
		if (rem <= 50) {
			putStrike = ceil - rem;
		} else {
			putStrike = (ceil - rem) + 100;
		}
		String instrument = "NIFTY17OCT" + putStrike + "PE";
	//	String product = "MIS";
		//String quantity = "300";
		OrderExecutor.placeSignleOrder(instrument, PRODUCT, QUANTITY, false);
	}
	
	private static boolean timeCheck() {
		boolean flag = false;
		LocalTime now = LocalTime.now(ZONE_ID);
		flag = now.isAfter(START_TIME) && now.isBefore(END_TIME);
		return flag;
	}
	
	public static void stop() {
		queue.clear();
		flag = false;
	}
	
	public static void start() {
		flag = true;
	}

}
