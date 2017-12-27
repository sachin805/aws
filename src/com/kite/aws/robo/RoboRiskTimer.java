package com.kite.aws.robo;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONException;

import com.kite.aws.login.KiteStore;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Position;

import static com.kite.aws.login.GlobalLoggerHandler.ROBO_LOGGER;

public class RoboRiskTimer extends TimerTask {
	
	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Calcutta");
	
	private static int trailSL = 0;
	
	private static boolean isTrailSLSet = false;
	
	@Override
	public void run() {
		
		//LocalTime now = LocalTime.now(ZONE_ID);
		/*if (strangleCheck()) {
			return;
		}*/
		
		if (RoboStore.order == null) {
		//	ROBO_LOGGER.info(" ROBO RISK :: There is no robo order in robo store at time " + now);
			trailSL = 0;
			isTrailSLSet = false;
			return;
		} else {
			NiftyQuote nifty = RoboUtil.getNiftyQuote();
			double ltp = nifty.getLtp();
			ROBO_LOGGER.info("Control in RoboRiskTimer.run()");
			ROBO_LOGGER.info("Robo order details : ");
			ROBO_LOGGER.info("RoboStore.order.getSymbol() : " + RoboStore.order.getSymbol());
			boolean up = RoboStore.order.isLong();
			if (up) {
				ROBO_LOGGER.info(" Direction : up");
			} else {
				ROBO_LOGGER.info(" Direction : down");
			}
			ROBO_LOGGER.info("Quantity : " + RoboStore.order.getQty());
			ROBO_LOGGER.info("Nifty when robo order got executed : " +RoboStore.order.getNifty());
			ROBO_LOGGER.info("Current nifty : " + ltp);
			ROBO_LOGGER.info("(Current nifty - execution price) : " +(ltp-RoboStore.order.getNifty()));
			
			if(trailProfitCheck(ltp)) {
				ROBO_LOGGER.info("Doing ");
				exitRobo();
			}
			
			if(lossCheck(ltp)) {
				ROBO_LOGGER.info("Doing ");
				exitRobo();
			}
		}
	}
	
	
	private boolean trailProfitCheck(double ltp) {
		boolean check = false;
		RoboOrder order = RoboStore.order;
		
		if (order == null) {
			ROBO_LOGGER.info("FATAL : RoboStore.order is null in RoboRiskTimer.profitLossCheck()");
			return false;
		}
		
		ROBO_LOGGER.info("Control in RoboRiskTimer.trailProfitCheck()");
		double entry = order.getNifty();
		double diff = ltp - entry;
		boolean isLong = order.isLong();
		
		
		ROBO_LOGGER.info("Value of isTrailSLSet before processing : " +isTrailSLSet);
		
		/*if (isTrailSLSet) {
			if (diff < 11) {
				ROBO_LOGGER.info("Returning true in RoboRiskTimer.trailProfitCheck()");
				return true;
			} else {
				ROBO_LOGGER.info("Returning false in RoboRiskTimer.trailProfitCheck()");
				return false;
			}
		}*/
		
		if (isTrailSLSet) {
			if (isLong) {
				if (diff < trailSL) {
					ROBO_LOGGER.info("Returning true in RoboRiskTimer.trailProfitCheck()");
					return true;
				} else {
					ROBO_LOGGER.info("Returning false in RoboRiskTimer.trailProfitCheck()");
					return false;
				}
			} else {
				if (Math.abs(diff) < trailSL) {
					ROBO_LOGGER.info("Returning true in RoboRiskTimer.trailProfitCheck()");
					return true;
				} else {
					ROBO_LOGGER.info("Returning false in RoboRiskTimer.trailProfitCheck()");
					return false;
				}
			}
		}
		
		// went long
		if (isLong) {
			// profit case
			if (diff > 0) {
				boolean isUp = RoboStore.direction.equalsIgnoreCase("up") || RoboStore.direction.equalsIgnoreCase("both");
				boolean isDown = RoboStore.direction.equalsIgnoreCase("down");
				
				if (isUp && (diff >= 11)) {
					ROBO_LOGGER.info("ltp : " + ltp );
					ROBO_LOGGER.info("entry : " + entry );
					ROBO_LOGGER.info("(ltp - entry): " + diff );
					ROBO_LOGGER.info("PROFIT (diff > 0) &&  diff >= 11 :: true");
					ROBO_LOGGER.info("Robo SL set");
					//check = true;
					isTrailSLSet = true;
					trailSL = 9;
				}
				
				if (isDown && diff >= 4) {
					ROBO_LOGGER.info("ltp : " + ltp );
					ROBO_LOGGER.info("entry : " + entry );
					ROBO_LOGGER.info("(ltp - entry): " + diff );
					ROBO_LOGGER.info("PROFIT (diff > 0) &&  diff >= 4 :: true");
					ROBO_LOGGER.info("Robo SL set");
					//check = true;
					isTrailSLSet = true;
					trailSL = 3;
				}
			}
		} else {
			//went short
			if (diff < 0) {
				boolean isDown = RoboStore.direction.equalsIgnoreCase("down") || RoboStore.direction.equalsIgnoreCase("both");
				boolean isUp = RoboStore.direction.equalsIgnoreCase("up");
				// profit case 
				if (isDown && Math.abs(diff) >= 11) {
					ROBO_LOGGER.info("ltp : " + ltp );
					ROBO_LOGGER.info("entry : " + entry );
					ROBO_LOGGER.info("(ltp - entry): " + diff );
					ROBO_LOGGER.info("PROFIT (diff < 0)  && Math.abs(diff) >= 11 :: true");
					ROBO_LOGGER.info("Robo SL set");
				//	check = true;
					isTrailSLSet = true;
					trailSL = 9;
				}
				
				if (isUp && diff >= 4) {
					ROBO_LOGGER.info("ltp : " + ltp );
					ROBO_LOGGER.info("entry : " + entry );
					ROBO_LOGGER.info("(ltp - entry): " + diff );
					ROBO_LOGGER.info("PROFIT (diff < 0) &&  diff >= 4 :: true");
					ROBO_LOGGER.info("Robo SL set");
					//check = true;
					isTrailSLSet = true;
					trailSL = 3;
				}
			}
		}
		ROBO_LOGGER.info("Value of isTrailSLSet after processing : " +isTrailSLSet);
		ROBO_LOGGER.info("Exiting RoboRiskTimer.trailProfitCheck() with check : " +check);
		return check;
	}
	
	private boolean lossCheck(double ltp) {
		boolean check = false;
		RoboOrder order = RoboStore.order;
		
		ROBO_LOGGER.info("Control in RoboRiskTimer.lossCheck()");
		
		if (order == null) {
			ROBO_LOGGER.info("FATAL : RoboStore.order is null in RoboRiskTimer.profitLossCheck()");
			return false;
		}
		
		double entry = order.getNifty();
		double diff = ltp - entry;
		boolean isLong = order.isLong();
		
		// went long
		if (isLong) {
			// loss case
			if (diff < 0) {
				if (Math.abs(diff) >= 7) {
					ROBO_LOGGER.info("ltp : " + ltp );
					ROBO_LOGGER.info("entry : " + entry );
					ROBO_LOGGER.info("(ltp - entry): " + diff );
					ROBO_LOGGER.info("LOSS (diff < 0) &&   Math.abs(diff) >= 7 :: true");
					check = true;
				}
			} 
		} else {
			//went short
			if (diff > 0) {
				// loss case
				if (diff >= 7) {
					ROBO_LOGGER.info("ltp : " + ltp );
					ROBO_LOGGER.info("entry : " + entry );
					ROBO_LOGGER.info("(ltp - entry): " + diff );
					ROBO_LOGGER.info("LOSS (diff > 0)  && diff >= 7 :: true");
					check = true;
				}
			}
		}
		
		ROBO_LOGGER.info("Value of flag check before return : "+check);
		return check;
	}
	
	
	private void exitRobo() {
		RoboStore.order = null;
		RoboStore.exitTime = LocalTime.now(ZONE_ID);
		isTrailSLSet = false;
		RoboOrderExecutor.exitRoboPositions();
	}
	
	
	/*private boolean strangleCheck() {
		boolean check = false;
		
		KiteConnect connect = KiteStore.kiteconnect;
		if (connect == null) {
			ROBO_LOGGER.info("KiteConnect is null in RoboRiskTimer.strangleCheck()");
			return false;
		}
		
		Position position = null;
		try {
			position = connect.getPositions();
		} catch (JSONException e) {
			ROBO_LOGGER.info("JSONException in RoboRiskTimer.strangleCheck()");
			return false;
		} catch (KiteException e) {
			ROBO_LOGGER.info("KiteException in RoboRiskTimer.strangleCheck()");
			return false;
		}
		
		int positionCount = 0;
		List<Position> netPositions = position.netPositions;
		for (Position item : netPositions) {
			if ((item.netQuantity != 0) && (item.product.equals("MIS"))) {
				positionCount++;
			}
		}
		check = (positionCount == 2);
		
		return check;
	}*/

}
