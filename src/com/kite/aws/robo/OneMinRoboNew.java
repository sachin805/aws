package com.kite.aws.robo;

import static com.kite.aws.login.GlobalLoggerHandler.ROBO_LOGGER;

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

public class OneMinRoboNew extends TimerTask {
	
	private static final LocalTime  START_TIME = LocalTime.parse("09:45");
	private static final LocalTime  END_TIME = LocalTime.parse("14:55");
	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Calcutta");
	private static final String QUANTITY = "75";
	private static final String PRODUCT = "NRML";
	private static ArrayBlockingQueue<NiftyQuote> queue;
	private static ArrayList<Integer> excludeList;
	private static boolean flag = true;
	
	static {
		queue = new ArrayBlockingQueue<NiftyQuote>(3,true);
		excludeList = new ArrayList<Integer>();
		excludeList.add(7);
		excludeList.add(8);
		excludeList.add(9);
		excludeList.add(28);
		excludeList.add(29);
		excludeList.add(30);
		excludeList.add(31);
		excludeList.add(32);
		excludeList.add(33);
		excludeList.add(50);
		excludeList.add(59);
		excludeList.add(60);
		excludeList.add(61);
		excludeList.add(62);
		excludeList.add(63);
		excludeList.add(64);
		excludeList.add(78);
		excludeList.add(79);
		excludeList.add(80);
		excludeList.add(81);
		excludeList.add(85);
		excludeList.add(95);
		excludeList.add(96);
		excludeList.add(97);
    }
	
	@Override
	public void run() {
		if(flag && timeCheck()) {
			NiftyQuote nifty = RoboUtil.getNiftyQuote();
			String code = nifty.getCode();
			
			if (code.equals("success")) {
				try {
					queue.add(nifty);
				} catch(IllegalStateException  e) {
					// means queue is full
					queue.poll();
					queue.add(nifty);
				}
				
				if (RoboStore.order != null) {
					manageRisk();
				} else {
					RoboStore.clearRoboStore();
				}
				
			//	if (twelveMinCheck()) {
				NiftyQuote[] array = queue.toArray(new NiftyQuote[queue.size()]);
				int length = array.length;
				
				boolean upFirstMin = false;
				boolean downFirstMin = false;
				boolean upFirstMinFourPoints = false;
				boolean downFirstMinFourPoints = false;
				if ((array != null) && (length == 3) && (array[1].getLtp() != 0.0) && (array[2].getLtp() != 0.0)) {
					boolean[] reply = firstMinCheck(array);
					upFirstMin = reply[0];
					downFirstMin = reply[1];
					upFirstMinFourPoints = reply[2];
					downFirstMinFourPoints = reply[3];
				} else {
					return;
				}
				
				LocalTime now = LocalTime.now(ZONE_ID);
				boolean positionCheck = positionCheck();
				boolean excludeFlag = excludeCheck((int)array[2].getLtp());
				
				ROBO_LOGGER.info("Before going long or short positionCheck : " + positionCheck);
				ROBO_LOGGER.info("Before going long or short excludeFlag : " + excludeFlag);
				
				positionCheck = positionCheck && excludeFlag;
				
				if ((upFirstMin && positionCheck) || (upFirstMinFourPoints && positionCheck)) {
					double one  = array[1].getLtp();
					double three = array[2].getLtp();
					
					ROBO_LOGGER.info("Going long.......");
					ROBO_LOGGER.info("Long 5 points check : "+upFirstMin);
					ROBO_LOGGER.info("Long 4 points check : "+upFirstMinFourPoints);
					ROBO_LOGGER.info("Current time : " + now);
					ROBO_LOGGER.info("three : " + three + " one : " + one);
					ROBO_LOGGER.info("(three-one) : " +(three-one));
					ROBO_LOGGER.info("upFirstMin : " + upFirstMin);
				//	String[] reply =  RoboOrderExecutor.longFut();
					String symbol = RoboOrderExecutor.getSymbol((int)array[2].getLtp(), true); 
					String[] reply =  RoboOrderExecutor.up(array[2].getLtp(),symbol);
					if (reply[0].equals("success")) {
						ROBO_LOGGER.info("Robo went long when nifty is at :: " +nifty.getLtp());
					}
					/*RoboStore.wentLong = true;
					RoboStore.ltp = nifty.getLtp();
					RoboStore.executionTime = LocalTime.now(ZONE_ID);*/
					RoboOrder order = null;
					if((RoboStore.delayTime != null) && (now.isAfter(RoboStore.delayTime))) {
						if (upFirstMinFourPoints) {
							order = new RoboOrder(nifty.getLtp(),LocalTime.now(ZONE_ID),true,symbol,null,true);
						} else {
							order = new RoboOrder(nifty.getLtp(),LocalTime.now(ZONE_ID),true,symbol,null,false);
						}
						RoboStore.order = order;
						RoboStore.executionTime = now;
						RoboStore.twoMins = now.plusSeconds(100l);
					} else {
						ROBO_LOGGER.info("Got long signal but skipping it...");
						ROBO_LOGGER.info("now : " + now);
						ROBO_LOGGER.info("delayTime : " + RoboStore.delayTime);
					}
				}
				
				if ((downFirstMin && positionCheck) || (downFirstMinFourPoints && positionCheck)) {
					double one  = array[0].getLtp();
					double three = array[2].getLtp();
					
					
					ROBO_LOGGER.info("Going short.......");
					ROBO_LOGGER.info("Short 5 points check : "+downFirstMin);
					ROBO_LOGGER.info("Short 4 points check : "+downFirstMinFourPoints);
					ROBO_LOGGER.info("Current time : " + now);
					ROBO_LOGGER.info("three : " + three + " one : " + one);
					ROBO_LOGGER.info("(three-one) : " +(three-one));
					//String[] reply =  RoboOrderExecutor.shortFut();
					String symbol = RoboOrderExecutor.getSymbol((int)array[2].getLtp(), false); 
					String[] reply =  RoboOrderExecutor.down(array[2].getLtp(),symbol);
					if (reply[0].equals("success")) {
						ROBO_LOGGER.info("Robo went short when nifty is at :: " +nifty.getLtp());
					}
					//RoboStore.wentShort = true;
					//RoboStore.ltp = nifty.getLtp();
					//RoboStore.executionTime = LocalTime.now(ZONE_ID);
					if((RoboStore.delayTime != null) && (now.isAfter(RoboStore.delayTime))) {
						RoboOrder order = null;
						if (downFirstMinFourPoints) {
							order = new RoboOrder(nifty.getLtp(),LocalTime.now(ZONE_ID),false,symbol,null,true);
						} else {
							order = new RoboOrder(nifty.getLtp(),LocalTime.now(ZONE_ID),false,symbol,null,false);
						}
						RoboStore.order = order;
						RoboStore.executionTime = now;
						RoboStore.twoMins = now.plusSeconds(100l);
					} else {
						ROBO_LOGGER.info("Got short signal but skipping it...");
						ROBO_LOGGER.info("now : " + now);
						ROBO_LOGGER.info("delayTime : " + RoboStore.delayTime);
					}
					
				  }
			//	}
			}
		}
		
	}
	
	private boolean excludeCheck(int nifty) {
		int twoDigits = nifty % 100;
		if (excludeList.contains(new Integer(twoDigits))) {
			return false;
		} else {
			return true;
		}
	}
	
	private void printArray(NiftyQuote[] array) {
		String line = "[ ";
		for (NiftyQuote item : array) {
			line = line + item.getLtp() + " , ";
		}
		line = line + " ]";
		ROBO_LOGGER.info("Nifty array : " + line);
	}
	
	private void manageRisk() {
		NiftyQuote[] array = queue.toArray(new NiftyQuote[queue.size()]);
		printArray(array);
		if ((array != null) && (array.length == 3)) {
			/*if (KiteStore.kiteconnect.getUserId().equals("SS1620")) {
				if (allConditions(array,true)) {
					exitRobo();
				}
			}*/
			if (allConditions(array)) {
				exitRobo();
			} /*else {
				slViolation();
				trailProfit();
			}*/
		}
	}
	
	
	private void slViolation() {
		
		if (RoboStore.trailSL.equals("NA")) {
		} else { // it means trail SL is already set
			double sl = Double.parseDouble(RoboStore.trailSL);
			boolean longProfit = (RoboStore.order.isLong()) && (RoboStore.netPoints >0);
			boolean shortProfit = (RoboStore.order.isLong() == false) && (RoboStore.netPoints <0);
			if(longProfit || shortProfit) {
				if (sl < Math.abs(RoboStore.netPoints)) {
					exitRobo();
				}
			}
		}
		
	}
	
	private void trailProfit() {
		if (RoboStore.order != null) {
				boolean longProfit = (RoboStore.order.isLong()) && (RoboStore.netPoints >0);
				boolean shortProfit = (RoboStore.order.isLong() == false) && (RoboStore.netPoints <0);
				
				if(longProfit || shortProfit) {
					setSL_Levels();
				}
		}
	}
	
	private void setSL_Levels() {
		if (RoboStore.netPoints > 4) {
			setSL(0.0);
		}
		
		if (RoboStore.netPoints > 10) {
			setSL(3.0);
		}
		
		if (RoboStore.netPoints > 15) {
			setSL(5.0);
		}
		
		if (RoboStore.netPoints > 20) {
			setSL(10.0);
		}
	}
	
	private void setSL(double sl) {
		if (RoboStore.trailSL.equals("NA")) {
			RoboStore.trailSL = sl + "";
		} else {
			if (sl > Double.parseDouble(RoboStore.trailSL)) {
				RoboStore.trailSL = sl + "";
			}
		}
	}
	
	private void exitRobo() {
		RoboStore.delayTime = LocalTime.now(ZONE_ID).plusSeconds(120l);
		RoboStore.clearRoboStore();
		RoboOrderExecutor.exitRoboPositions();
	}
	
	// for first 2 minutes entry must support the move in same direction
	/*private boolean firstCondition() {
		
		boolean supportCheck = false;
	//	boolean exitCheck = false;
		LocalTime exeTime = RoboStore.order.getExecutionTime();
		LocalTime twoMins = exeTime.plusSeconds(150l);
		LocalTime now = LocalTime.now(ZONE_ID);
		
		NiftyQuote[] array = queue.toArray(new NiftyQuote[queue.size()]);
		
		if (now.isAfter(exeTime) && now.isBefore(twoMins)) {
			double level = RoboStore.order.getNifty();
			//double currentLevel = queue.peek().getLtp();
			double currentLevel = array[2].getLtp();
			double difference = currentLevel-level;
				if (RoboStore.order.isLong()) {
					if (difference >= 0) {
						supportCheck = true;
					} else {
						supportCheck = false;
						if (Math.abs(difference) >= 6) {
							exitCheck = true;
						}
					}
				} else {
					if (difference <= 0) {
						supportCheck = true;
					} else {
						supportCheck = false;
						if (Math.abs(difference) >= 6) {
							exitCheck = true;
						}
					}
				}
			
		} else {
			// here first 2 minutes are now passed
			supportCheck = true;
		//	exitCheck = false;
		}
		//return new boolean[] { supportCheck,exitCheck };
		return supportCheck;
	}*/
	
	private boolean allConditions(NiftyQuote[] array) {
		double ltp = array[2].getLtp();
		double entry = RoboStore.order.getNifty();
		double difference = ltp - entry;
		boolean isLong = RoboStore.order.isLong();
		boolean fourPoints = RoboStore.order.isFourPoints();
		boolean fivePoints = !fourPoints;
		
		//first and most imp is risk management and profit booking.
		ROBO_LOGGER.info("Robo risk and profit management.......");
		ROBO_LOGGER.info("first condition..");
		if (isLong) {
			ROBO_LOGGER.info("long..");
			if (difference < 0) { // loss
				if (fivePoints && Math.abs(difference) >= 6) {
					ROBO_LOGGER.info("Math.abs(difference) >= 6, loss, five points : true" );
					return true;
				}
				
				if (fourPoints && Math.abs(difference) >= 4) {
					ROBO_LOGGER.info("Math.abs(difference) >= 4, loss, four points : true" );
					return true;
				}
			} else { //profit
				if (fivePoints && difference >= 3) {
					ROBO_LOGGER.info("difference >= 3, profit,fivePoints : true" );
					return true;
				}
				
				if (fourPoints && difference >= 2) {
					ROBO_LOGGER.info("difference >= 2, profit,fourPoints : true" );
					return true;
				}
			}
		 } else {
			 ROBO_LOGGER.info("short..");
			 if (fivePoints && difference >= 6) { //loss
					ROBO_LOGGER.info("difference >= 6, loss,fivePoints : true" );
					return true;
		       }  else { //profit
		    	   if (fivePoints && (difference < 0) && (Math.abs(difference) >= 3)) {
		    		   ROBO_LOGGER.info("((difference < 0) && (Math.abs(difference) >= 3)), profit,fivePoints : true" );
		    		   return true;
		    	   }
		       }
			 
			 if (fourPoints && difference >= 4) { //loss
					ROBO_LOGGER.info("difference >= 4, loss,fourPoints : true" );
					return true;
		       }  else { //profit
		    	   if (fourPoints && (difference < 0) && (Math.abs(difference) >= 2)) {
		    		   ROBO_LOGGER.info("((difference < 0) && (Math.abs(difference) >= 2)), profit,fourPoints : true" );
		    		   return true;
		    	   }
		       }
		 }
		
		
		LocalTime twoMins = RoboStore.twoMins;
		LocalTime now = LocalTime.now(ZONE_ID);
		ROBO_LOGGER.info("second condition..");
		if (now.isBefore(twoMins)) {
			ROBO_LOGGER.info("second condition : do nothing");
		} else {
			// here first 2 minutes are now passed
			if (RoboStore.oneTime2MinCheck == false) {
				ROBO_LOGGER.info("(RoboStore.oneTime2MinCheck == false) : " +(RoboStore.oneTime2MinCheck == false));
				RoboStore.oneTime2MinCheck = true;
				if (isLong) {
					if (ltp < entry) {
						ROBO_LOGGER.info("ltp = " +ltp);
						ROBO_LOGGER.info("entry = " +entry);
						ROBO_LOGGER.info("long, (ltp < entry)  : true");
						return true;
					}
				} else {
					if (ltp > entry) {
						ROBO_LOGGER.info("ltp = " +ltp);
						ROBO_LOGGER.info("entry = " +entry);
						ROBO_LOGGER.info("short, (ltp > entry)  : true");
						return true;
					}
				}
				
			}
		}
		
	/*	if (Math.abs(difference) <= 10) {
			double oneMinDiff = array[6].getLtp() - array[0].getLtp();
			if (isLong) {
				if (oneMinDiff < 0) {
					if (Math.abs(oneMinDiff) > 3) {
						return true;
					}
				}
			} else {
				if (oneMinDiff > 3) {
					return true;
				}
			}
		}*/
		ROBO_LOGGER.info("first and second conditions skipped");
		ROBO_LOGGER.info("returning false");
		return false;
		
	}
	
	
	private boolean allConditions(NiftyQuote[] array,boolean sachin) {
		double ltp = array[6].getLtp();
		double entry = RoboStore.order.getNifty();
		double difference = ltp - entry;
		boolean isLong = RoboStore.order.isLong();
		boolean fourPoints = RoboStore.order.isFourPoints();
		boolean fivePoints = !fourPoints;
		
		//first and most imp is risk management and profit booking.
		ROBO_LOGGER.info("Robo risk and profit management.......");
		ROBO_LOGGER.info("first condition..");
		if (isLong) {
			ROBO_LOGGER.info("long..");
			if (difference < 0) { // loss
				if (fivePoints && Math.abs(difference) >= 6) {
					ROBO_LOGGER.info("Math.abs(difference) >= 6, loss, five points : true" );
					return true;
				}
				
				if (fourPoints && Math.abs(difference) >= 4) {
					ROBO_LOGGER.info("Math.abs(difference) >= 4, loss, four points : true" );
					return true;
				}
			} else { //profit
				/*if (fivePoints && difference >= 3) {
					ROBO_LOGGER.info("difference >= 3, profit,fivePoints : true" );
					return true;
				}
				
				if (fourPoints && difference >= 2) {
					ROBO_LOGGER.info("difference >= 2, profit,fourPoints : true" );
					return true;
				}*/
			}
		 } else {
			 ROBO_LOGGER.info("short..");
			 if (fivePoints && difference >= 6) { //loss
					ROBO_LOGGER.info("difference >= 6, loss,fivePoints : true" );
					return true;
		       }  else { //profit
		    	   /*if (fivePoints && (difference < 0) && (Math.abs(difference) >= 3)) {
		    		   ROBO_LOGGER.info("((difference < 0) && (Math.abs(difference) >= 3)), profit,fivePoints : true" );
		    		   return true;
		    	   }*/
		       }
			 
			 if (fourPoints && difference >= 4) { //loss
					ROBO_LOGGER.info("difference >= 4, loss,fourPoints : true" );
					return true;
		       }  else { //profit
		    	  /* if (fourPoints && (difference < 0) && (Math.abs(difference) >= 2)) {
		    		   ROBO_LOGGER.info("((difference < 0) && (Math.abs(difference) >= 2)), profit,fourPoints : true" );
		    		   return true;
		    	   }*/
		       }
		 }
		
		
		LocalTime twoMins = RoboStore.twoMins;
		LocalTime now = LocalTime.now(ZONE_ID);
		ROBO_LOGGER.info("second condition..");
		if (now.isBefore(twoMins)) {
			ROBO_LOGGER.info("second condition : do nothing");
		} else {
			// here first 2 minutes are now passed
			if (RoboStore.oneTime2MinCheck == false) {
				ROBO_LOGGER.info("(RoboStore.oneTime2MinCheck == false) : " +(RoboStore.oneTime2MinCheck == false));
				RoboStore.oneTime2MinCheck = true;
				if (isLong) {
					if (ltp < entry) {
						ROBO_LOGGER.info("ltp = " +ltp);
						ROBO_LOGGER.info("entry = " +entry);
						ROBO_LOGGER.info("long, (ltp < entry)  : true");
						return true;
					}
				} else {
					if (ltp > entry) {
						ROBO_LOGGER.info("ltp = " +ltp);
						ROBO_LOGGER.info("entry = " +entry);
						ROBO_LOGGER.info("short, (ltp > entry)  : true");
						return true;
					}
				}
				
			}
		}
		
	/*	if (Math.abs(difference) <= 10) {
			double oneMinDiff = array[6].getLtp() - array[0].getLtp();
			if (isLong) {
				if (oneMinDiff < 0) {
					if (Math.abs(oneMinDiff) > 3) {
						return true;
					}
				}
			} else {
				if (oneMinDiff > 3) {
					return true;
				}
			}
		}*/
		ROBO_LOGGER.info("first and second conditions skipped");
		ROBO_LOGGER.info("returning false");
		return false;
		
	}
	
	
	private boolean firstCondition(double ltp) {
		boolean check = false;
		LocalTime exeTime = RoboStore.executionTime;
		LocalTime twoMins = RoboStore.twoMins;
		LocalTime now = LocalTime.now(ZONE_ID);
		
		//NiftyQuote[] array = queue.toArray(new NiftyQuote[queue.size()]);
		
		if (now.isAfter(exeTime) && now.isBefore(twoMins)) {
			// do nothing
			
			
			
		} else {
			// here first 2 minutes are now passed
			if (RoboStore.oneTime2MinCheck == false) {
				double level = RoboStore.order.getNifty();
				double currentNifty = ltp;
				if (RoboStore.order.isLong()) {
					if (currentNifty < level) {
						check = true;
					}
				} else {
					if (currentNifty > level) {
						check = true;
					}
				}
				RoboStore.oneTime2MinCheck = true;
			}
		}
		return check;
	}
	
	
	private boolean secondCondition(double ltp) {
		boolean check = false;
		double entry = RoboStore.order.getNifty();
		//NiftyQuote[] array = queue.toArray(new NiftyQuote[queue.size()]);
		
		double difference = ltp - entry;
		
		if (RoboStore.order.isLong()) {
			if (difference < 0) {
				if (Math.abs(difference) >= 6) {
					check = true;
				}
			}
		} else {
			if (difference > 6) {
				check = true;
			}
		}
		return check;
	}
	
	private boolean thirdCondition(NiftyQuote[] array) {
		boolean check = false;
		double level = RoboStore.order.getNifty();
		
		double difference = array[6].getLtp() - level;
		if (Math.abs(difference) <= 10) {
			if (RoboStore.order.isLong()) {
				double oneMinDiff = array[6].getLtp() - array[0].getLtp();
				if (oneMinDiff < 0) {
					if (Math.abs(oneMinDiff) > 3) {
						check = true;
					}
				}
			} else {
				if (difference >0) {
					if (difference > 3) {
						check = true;
					}
				}
			}
		}
		return check;
	}
	
	
	private void persistNetPoints(NiftyQuote[] array) {
		double level = RoboStore.order.getNifty();
		double difference = array[array.length-1].getLtp() - level;
		RoboStore.netPoints = difference;
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
	
	
	private boolean[] firstMinCheck(NiftyQuote[] array) {
		boolean[] reply = new boolean[4];
		
		double t1 = array[1].getLtp();
		double t7 = array[2].getLtp();
		
		double diff = t7-t1;
		// first up movement check 
		if (diff > 0) {
			reply[0] = (diff >= 5) && (diff <= 7);
			reply[2] = (diff >= 4) && (diff < 4.95); // for 4 points
		}
		//second down movement check 
		if (diff < 0) {
			diff = Math.abs(diff);
			reply[1] = (diff >= 5) && (diff <= 7);
			reply[3] = (diff >= 4) && (diff < 4.95);  // for 4 points
		}
		return reply;
	}
	
	private static boolean timeCheck() {
		LocalTime now = LocalTime.now(ZONE_ID);
		return now.isAfter(START_TIME) && now.isBefore(END_TIME);
	}
	
	private static boolean positionCheck() {
		boolean flag = false;
		
		KiteConnect connect = KiteStore.kiteconnect;
		 
		 if (connect == null) {
			 
		 } else {
			 Position position = null;
			 try {
					position = connect.getPositions();
				} catch (JSONException e) {
				} catch (KiteException e) {
				}
			 
			 List<Position> netPositions = position.netPositions;
			 int openPositionCount = 0;
			 for (Position item : netPositions) {
				 if ((item.netQuantity != 0) && (item.product.equals("MIS"))) {
					 openPositionCount++;
				 }
			 }
			 flag = (openPositionCount == 0);
		 }
		
		return flag;
	}

	private boolean twelveMinCheck() {
		boolean flag = false;
		
		if(RoboUtil.positionCountCheck()) {
			return true;
		}

		LocalTime executionTime = RoboStore.executionTime;
		if (executionTime != null) {
			LocalTime now = LocalTime.now(ZONE_ID);
			LocalTime fiften = executionTime.plusSeconds(720);
		//	flag = now.isAfter(executionTime) && now.isBefore(fiften);
			flag = now.isAfter(fiften);
		} else {
			flag = true;
		}

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
