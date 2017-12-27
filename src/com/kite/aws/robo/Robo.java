package com.kite.aws.robo;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

import org.json.JSONException;

import com.kite.aws.login.KiteStore;
import com.kite.aws.order.OrderExecutor;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Position;

import static com.kite.aws.login.GlobalLoggerHandler.ROBO_LOGGER;

public class Robo extends TimerTask {
	
	public static ArrayBlockingQueue<NiftyQuote> queue;
	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Calcutta");
	private static ArrayList<Integer> excludeList;
	private static boolean flag = true;
	private static final LocalTime  START_TIME = LocalTime.parse("09:20");
	private static final LocalTime  END_TIME = LocalTime.parse("15:05");
	
	static {
		queue = new ArrayBlockingQueue<NiftyQuote>(7);
		excludeList = new ArrayList<Integer>();
	/*	excludeList.add(7);
		excludeList.add(8);
		excludeList.add(9);
		excludeList.add(28); */
		excludeList.add(29);
		excludeList.add(30);
		excludeList.add(31);
		excludeList.add(32);
	//	excludeList.add(33);
		excludeList.add(50);
		excludeList.add(59);
		excludeList.add(60);
		excludeList.add(61);
		excludeList.add(62);
		/*excludeList.add(63);
		excludeList.add(64);*/
		excludeList.add(78);
		excludeList.add(79);
		excludeList.add(80);
		excludeList.add(81);
		//excludeList.add(85);
		/*excludeList.add(95);
		excludeList.add(96);
		excludeList.add(97);*/
	}
	
	@Override
	public void run() {
		if (flag) {
			if (timeCheck()) {
			NiftyQuote nifty = RoboUtil.getNiftyQuote();
			
			if ((nifty != null) && (nifty.getCode().equals("success"))) {
				try {
					queue.add(nifty);
				} catch(IllegalStateException e) {
					// means queue is full
					queue.poll();
					queue.add(nifty);
				}
				
				NiftyQuote[] array = queue.toArray(new NiftyQuote[queue.size()]);
				int length = array.length;
				printArray(array);
				
				if (positionCheck()) {
					boolean excludeCheck = excludeCheck((int)nifty.getLtp());
					if (excludeCheck) {
						boolean upFirstMin = false;
						boolean downFirstMin = false;
					//	boolean upFirstMinFourPoints = false;
					//	boolean downFirstMinFourPoints = false;
						if ((array != null) && (length == 7) && (array[6].getLtp() != 0.0)) {
							boolean[] reply = conditionCheck(array);
							upFirstMin = reply[0];
							downFirstMin = reply[1];
							/*upFirstMinFourPoints = reply[2];
							downFirstMinFourPoints = reply[3];*/
						} else {
							ROBO_LOGGER.info("NiftyQuote array is not filled yet");
							return;
						}
					//	boolean upDirection = direction.equals("both") || direction.equals("up");
						if (upFirstMin) {
							OrderExecutor.exitAllPositions();
							
							try {
								Thread.currentThread().sleep(500);
							} catch (InterruptedException e) {
								 ROBO_LOGGER.info("InterruptedException while squaring off....Please handle it manually");
							}
							double six  = array[5].getLtp();
							double seven = array[6].getLtp();
							
							ROBO_LOGGER.info("Going long.......");
							ROBO_LOGGER.info("Current time : " + nifty.getNow());
							ROBO_LOGGER.info("Long 5 points check : "+upFirstMin);
						//	ROBO_LOGGER.info("Long 4 points check : "+upFirstMinFourPoints);
							ROBO_LOGGER.info("seven : " + seven + " six : " + six);
							ROBO_LOGGER.info("(seven-six) : " +(seven-six));
							String symbol = RoboOrderExecutor.getSymbol((int)array[6].getLtp(), true);
							String[] reply =  RoboOrderExecutor.up(array[6].getLtp(),symbol);
							RoboOrder order = null;
							/*if (upFirstMinFourPoints) {
								order = new RoboOrder(array[1].getLtp(),nifty.getNow(),true,symbol,RoboOrderExecutor.quantity,true);
							} else {
								order = new RoboOrder(array[1].getLtp(),nifty.getNow(),true,symbol,RoboOrderExecutor.quantity,false);
							}*/
							order = new RoboOrder(array[6].getLtp(),nifty.getNow(),true,symbol,RoboStore.quantity,false);
							RoboStore.order = order;
							if (reply[0].equals("success")) {
								ROBO_LOGGER.info("Robo went long when nifty is at :: " +nifty.getLtp());
							}
						}
						//boolean downDirection = direction.equals("both") || direction.equals("down");
						if (downFirstMin) {
							OrderExecutor.exitAllPositions();
							try {
								Thread.currentThread().sleep(500);
							} catch (InterruptedException e) {
								 ROBO_LOGGER.info("InterruptedException while squaring off....Please handle it manually");
							}
							double six  = array[5].getLtp();
							double seven = array[6].getLtp();
							
							ROBO_LOGGER.info("Going short.......");
							ROBO_LOGGER.info("Current time : " + nifty.getNow());
							ROBO_LOGGER.info("Long 5 points check : "+downFirstMin);
							//ROBO_LOGGER.info("Long 4 points check : "+downFirstMinFourPoints);
							ROBO_LOGGER.info("seven : " + seven + " six : " + six);
							ROBO_LOGGER.info("(seven-six) : " +(seven-six));
							String symbol = RoboOrderExecutor.getSymbol((int)array[6].getLtp(), false);
							String[] reply =  RoboOrderExecutor.down(array[6].getLtp(),symbol);
							
							RoboOrder order = null;
							/*if (downFirstMinFourPoints) {
								order = new RoboOrder(array[1].getLtp(),nifty.getNow(),false,symbol,RoboOrderExecutor.quantity,true);
							} else {
								order = new RoboOrder(array[1].getLtp(),nifty.getNow(),false,symbol,RoboOrderExecutor.quantity,false);
							}*/
							order = new RoboOrder(array[6].getLtp(),nifty.getNow(),false,symbol,RoboStore.quantity,false);
							RoboStore.order = order;
							if (reply[0].equals("success")) {
								ROBO_LOGGER.info("Robo went short when nifty is at :: " +nifty.getLtp());
							}
						}
					} else {
						ROBO_LOGGER.info("Last two digits of nifty are in exclude list : " + ((int)nifty.getLtp()));
					}
					
				
				} else {
					ROBO_LOGGER.info("There are open positions....so skiping candle formation");
				}
				
			} else {
				ROBO_LOGGER.info("Fatal : Quote from RoboUtil.getNiftyQuote() is null or not correct");
			}
		  } 
		} else {
			ROBO_LOGGER.info("Robo is turned off...");
		}
		
	}
	
	
	/**
	 * @param array
	 * @return
	 */
	private boolean[] conditionCheck(NiftyQuote[] array) {
		boolean reply[] = new boolean[2];
		
		double six = array[5].getLtp();
		double seven = array[6].getLtp();
		
		double diff = seven - six;
		boolean addCheck = false;
		boolean upCheck = false;
		boolean downCheck = false;
		// first up movement check 
		if (diff > 0) {
			upCheck = (diff >= 4) && (diff <= 5);
			addCheck = additionalConditions(array,true);
		}
		//second down movement check 
		if (diff < 0) {
			diff = Math.abs(diff);
			downCheck = (diff >= 4) && (diff <= 5);
			addCheck = additionalConditions(array,false);
		}
		
		if (upCheck) {
			reply[0] = upCheck && addCheck;
		}
		
		if (downCheck) {
			reply[1] = downCheck && addCheck;
		}
		
		
		return reply;
	}
	
	private boolean additionalConditions(NiftyQuote[] array,boolean up) {
		boolean flag = false;
		double sum5Candle = 0.0d;
		double sum3Candle = 0.0d;
		double c0,c1,c2,c3,c4;
		
		c0 =  array[1].getLtp() -  array[0].getLtp();
		c1 =  array[2].getLtp() -  array[1].getLtp();
		c2 =  array[3].getLtp() -  array[2].getLtp();
		c3 =  array[4].getLtp() -  array[3].getLtp();
		c4 =  array[5].getLtp() -  array[4].getLtp();
		
		boolean bigMoveCheck = false;
		ROBO_LOGGER.info("Control in Robo.additionalConditions()");
		if (up) {
			ROBO_LOGGER.info("up movement .....");
		} else {
			ROBO_LOGGER.info("down movement .....");
		}
		String candle = "[ c0 = " + c0 + ",c1 = " +c1 +", c2 = " + c2 + ",c3 = " + c3 + " , c4 = " + c4 + " ]";
		ROBO_LOGGER.info(candle);
		
		bigMoveCheck = (Math.abs(c0) >= 4) || (Math.abs(c1) >= 4) || (Math.abs(c2) >= 4) || (Math.abs(c3) >= 4) || (Math.abs(c4) >= 4) ;
		
		if (bigMoveCheck) {
			ROBO_LOGGER.info("There was a big move in last 5 mins so skipping the signal");
			return false;
		} else {
			sum3Candle = c0+c1+c2;
			sum5Candle = sum3Candle+c3+c4;
			ROBO_LOGGER.info("sum3Candle :: " +sum3Candle);
			ROBO_LOGGER.info("sum5Candle :: " +sum3Candle);
			if (up) {
				flag = (sum3Candle >0) && (sum5Candle >0);
			} else {
				flag = (sum3Candle <0) && (sum5Candle <0);
			}
		}
		return flag;
	}
	
	private void printArray(NiftyQuote[] array) {
		String line = "[ ";
		for (NiftyQuote item : array) {
			line = line + "( " + item.getLtp() + " , " + item.getNow() + " )";
		}
		line = line + " ]";
		ROBO_LOGGER.info(line);
	}
	
	private static boolean positionCheck() {
		return ((RoboStore.order == null) && (!RoboStore.direction.equals("off")));
	}
	
	private boolean excludeCheck(int nifty) {
		int twoDigits = nifty % 100;
		if (excludeList.contains(new Integer(twoDigits))) {
			return false;
		} else {
			return true;
		}
	}
	
	private static boolean timeCheck() {
		boolean check = false;
		LocalTime now = LocalTime.now(ZONE_ID);
		/*if (RoboStore.exitTime == null) {
			check = now.isAfter(START_TIME) && now.isBefore(END_TIME);
		} else {
			LocalTime time = RoboStore.exitTime;
			time.plusSeconds(60l);
			check = now.isAfter(time) && now.isAfter(START_TIME) && now.isBefore(END_TIME);
		}*/
		
		check = now.isAfter(START_TIME) && now.isBefore(END_TIME);
		if (check == false) {
			queue.clear();
		}
		return check;
	}
	
	public static void stop() {
		queue.clear();
		flag = false;
	}
	
	public static void start() {
		flag = true;
	}

}
