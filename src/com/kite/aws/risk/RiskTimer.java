package com.kite.aws.risk;


import static com.kite.aws.login.GlobalLoggerHandler.RISK_TIMER_LOGGER;
import static com.kite.aws.risk.RiskStore.m2m;
import static com.kite.aws.risk.RiskStore.profitTarget;
import static com.kite.aws.risk.RiskStore.riskTimerStatus;
import static com.kite.aws.risk.RiskStore.riskTimerStatusMessage;
import static com.kite.aws.risk.RiskStore.stopLoss;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONException;

import  com.kite.aws.login.KiteStore;
import com.kite.aws.order.OrderExecutor;
import com.kite.aws.order.SquareOffOrder;
import com.kite.aws.order.conditional.ConditionalOrder;
import com.kite.aws.order.conditional.ConditionalOrderStore;
import com.kite.aws.order.conditional.ConditionalUtil;
import com.kite.aws.robo.RoboStore;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Margins;
import com.rainmatter.models.Order;
import com.rainmatter.models.Position;

public class RiskTimer extends TimerTask {
	
	private String riskStatus = "";
	private static String KITE_CONNECT_NULL = "KITE_CONNECT_NULL";
	private static String KITE_EXCEPTION = "KITE_EXCEPTION";
	private static String NUMBER_FORMAT_EXCEPTION = "NUMBER_FORMAT_EXCEPTION";
	private static String NO_OPEN_POSITION = "NO_OPEN_POSITION";
	private static String SOME_WRONG = "SOME_WRONG";
	private static String NO_TRIGGER = "NO_TRIGGER";
	
	private static final LocalTime  START_TIME = LocalTime.parse("09:15");
	private static final LocalTime  END_TIME = LocalTime.parse("15:30");
	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Calcutta");
	private static int singlePositionCountCheck;
	
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
		return check;
	}
	
	@Override
	public void run() {
		boolean check = timeCheck();
		if (check) {
			handleRisk(); 
		}
		/*if (ConditionalOrderStore.order != null) {
			handleConditional();
		}*/
	}
	
	private boolean checkDailyLoss() {
		Position position = null;
		KiteConnect connect = KiteStore.kiteconnect;
		
		try {
			position = connect.getPositions();
		} catch (JSONException e) {
			RISK_TIMER_LOGGER.info("JSONException in RiskTimer.checkDailyLoss()");
			e.printStackTrace();
			return true;
		}
		catch (KiteException e) {
			RISK_TIMER_LOGGER.info("KiteException in RiskTimer.checkDailyLoss()");
			e.printStackTrace();
			return true;
		}
		
		double m2m = 0.0;
		List<Position> netPositions = position.netPositions;
		for (Position p : netPositions) {
			m2m = m2m + p.m2m;
		}
		RISK_TIMER_LOGGER.info("m2m : " + m2m);
		try {
			if (m2m < 0) {
				Margins margin = connect.getMargins("equity");
				double absM2m = Math.abs(m2m);
				double net = Double.parseDouble(margin.net);
				RISK_TIMER_LOGGER.info("net : " + net);
				double maxLossAllowed = (net*7)/100.0;
				RISK_TIMER_LOGGER.info("maxLossAllowed : " + maxLossAllowed);
				RISK_TIMER_LOGGER.info("(absM2m > maxLossAllowed) : " + (absM2m > maxLossAllowed));
				if (absM2m > maxLossAllowed) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} catch (JSONException e) {
			RISK_TIMER_LOGGER.info("JSONException in RiskTimer.checkDailyLoss() while getting margin");
			e.printStackTrace();
			return true;
		} catch (KiteException e) {
			RISK_TIMER_LOGGER.info("KiteException in RiskTimer.checkDailyLoss() while getting margin");
			e.printStackTrace();
			return true;
		}
	}
	
	
	public void handleRisk() {
		
		if (RoboStore.order != null) {
			singlePositionCountCheck = 0;
			return;
		}
		
		/*if (preventOddPositions()) {
			return;
		}*/
		
		KiteConnect connect = KiteStore.kiteconnect;
		if (connect == null) {
			RiskStore.riskTimerStatus = "error";
			riskTimerStatusMessage = "connect is null in RiskTimer.run()";
			RISK_TIMER_LOGGER.info(riskTimerStatusMessage);
			riskStatus = KITE_CONNECT_NULL;
			return;
		}
		
		if (checkDailyLoss() == false) {
			RISK_TIMER_LOGGER.info("Exiting all open positions as daily max loss exceeded");
			OrderExecutor.exitAllPositions();
			return;
		}
		
		Position position = null;
		double profitAndLoss = 0.0;
		
		try {
			position = connect.getPositions();
		} catch (JSONException e) {
			riskTimerStatus = "error";
			riskTimerStatusMessage = "JSONException in RiskTimer.run() while getting positions";
			m2m = ""+profitAndLoss;
			RISK_TIMER_LOGGER.info("JSONException in RiskTimer.run() while getting positions");
			riskStatus = KITE_EXCEPTION;
			return;
		} catch (KiteException e) {
			riskTimerStatus = "error";
			riskTimerStatusMessage = "KiteException in RiskTimer.run() while getting positions";
			m2m = ""+profitAndLoss;
			RISK_TIMER_LOGGER.info("KiteException in RiskTimer.run() while getting positions");
			riskStatus = KITE_EXCEPTION;
			return;
		}
		
		
		List<Position> netPositions = position.netPositions;
		List<Position> trackPositions = new ArrayList<Position>();
		int openPositionCount = 0;
		int openPositionQuantity = 0;
		for (Position item : netPositions) {
				int netQuantity = item.netQuantity;
					if (netQuantity != 0) {
					openPositionCount = openPositionCount + 1;
					openPositionQuantity = openPositionQuantity + Math.abs(netQuantity);
					RISK_TIMER_LOGGER.info("Calculating profit and loss for " +item.tradingSymbol);
					RISK_TIMER_LOGGER.info("net quantity : " +item.netQuantity);
					Object[] tokens = NewOrderBook.getPL(item);
					String code = (String)tokens[0];
					RISK_TIMER_LOGGER.info("code : " +code);
					if (code.equals("success")) {
						profitAndLoss = profitAndLoss + Double.parseDouble((String)tokens[2]);
						RISK_TIMER_LOGGER.info("profit or loss : " +(String)tokens[2]);
					}
					trackPositions.add(item);
				}
		}
		
		RISK_TIMER_LOGGER.info("openPositionCount : " + openPositionCount);
		DecimalFormat df = new DecimalFormat("#.##");
		try {
				profitAndLoss = Double.valueOf(df.format(profitAndLoss));
		} catch (NumberFormatException e) {
			RISK_TIMER_LOGGER.info("NumberFormatException in RiskTimer.run()");
			profitAndLoss = 0.0;
			profitAndLoss = Double.valueOf(df.format(profitAndLoss));
			riskStatus = NUMBER_FORMAT_EXCEPTION;
		}
		
	
		if (openPositionCount == 0) {
			riskTimerStatus = "NO_TRIGGER";
				m2m = ""+profitAndLoss;
				stopLoss = "NA";
				profitTarget = "NA";
				riskTimerStatusMessage = "NA";
			//	RISK_TIMER_LOGGER.info("openPositionCount=0 in  RiskTimer.run()");
				riskStatus = NO_OPEN_POSITION;
				singlePositionCountCheck = 0;
			//	RiskStore.trailStopLoss = "NA";
			//	RiskStore.trailStopLossTriggerUI = "NA";
			    return;
		} 
		
		if (openPositionCount == 1) {
			if (singlePositionCountCheck == 0) {
				// this is the first time we got signal
				// we'll avoid it for first time
				RISK_TIMER_LOGGER.info("openPositionCount is 1 skiping it for first time");
				riskTimerStatus = "NO_TRIGGER";
				m2m = ""+profitAndLoss;
				singlePositionCountCheck++;
				return;
			} else {
				RISK_TIMER_LOGGER.info("openPositionCount is 1 so exiting all positions");
				riskTimerStatus = "NO_TRIGGER";
				m2m = ""+profitAndLoss;
				stopLoss = "NA";
				profitTarget = "NA";
				riskTimerStatusMessage = "NA";
				riskStatus = NO_OPEN_POSITION;
				OrderExecutor.exitAllPositions();
			    return;
			}
			
		}
		
		if (openPositionCount == 2) {
			singlePositionCountCheck = 0;
			m2m = ""+profitAndLoss;
			boolean validStrangle = validStrangleCheck(trackPositions);
			if (validStrangle == false) {
				riskTimerStatus = "NO_TRIGGER";
				stopLoss = "NA";
				profitTarget = "NA";
				riskTimerStatusMessage = "NA";
				riskStatus = NO_OPEN_POSITION;
				OrderExecutor.exitAllPositions();
			    return;
			}
		}
		
		RISK_TIMER_LOGGER.info("Before checking stop loss or profit target");
		RISK_TIMER_LOGGER.info(" profitAndLoss : " + profitAndLoss);
		RISK_TIMER_LOGGER.info(" openPositionQuantity : " + openPositionQuantity );
		RISK_TIMER_LOGGER.info(" openPositionCount : " + openPositionCount );
		
		
		if (lossTriggerCheck(profitAndLoss,openPositionQuantity,openPositionCount)) {
			RISK_TIMER_LOGGER.info("Got signal for stop loss in RiskTimer.run() from lossTriggerCheck()");
			String[] reply = OrderExecutor.exitAllPositions();
			if (reply[0].equals("success")) {
				riskTimerStatus = "complete";
				riskTimerStatusMessage = "Stop loss hit. Squared off all open positions";
				m2m = ""+profitAndLoss;
				stopLoss = "NA";
 				profitTarget = "NA";
 				riskStatus = NO_TRIGGER;
				return;
			}
		}
		
		
		if (profitTriggerCheck(profitAndLoss,openPositionQuantity,openPositionCount)) {
			RISK_TIMER_LOGGER.info("Got signal for profit target in RiskTimer.run() from lossTriggerCheck()");
			String[] reply = OrderExecutor.exitAllPositions();
			if (reply[0].equals("success")) {
				riskTimerStatus = "complete";
				riskTimerStatusMessage = "Profit target hit. Squared off all open positions";
				m2m = ""+profitAndLoss;
				stopLoss = "NA";
 				profitTarget = "NA";
 				riskStatus = NO_TRIGGER;
				return;
			}
		}
		
		riskTimerStatus = "NO_TRIGGER";
		m2m = ""+profitAndLoss;
		riskTimerStatusMessage = "NA";
		riskStatus = NO_TRIGGER;
		
		
		
		/*// now trigger orders
		if ((lossTriggerCheck(profitAndLoss,openPositionQuantity,openPositionCount)) || (profitTriggerCheck(profitAndLoss,openPositionQuantity,openPositionCount))) {
			RISK_TIMER_LOGGER.info("Got signal for target or stop loss in RiskTimer.run()");
			//RiskStore.trailStopLoss = "NA";
						List<SquareOffOrder> squareOffList = new ArrayList<SquareOffOrder>();
						for (Position item : netPositions) {
							if ((item.netQuantity != 0) && ((item.product.equals("MIS")))){
								String instrument = item.tradingSymbol;
								int netQuantity = item.netQuantity;
								String product = item.product;
								boolean sellCheck = ( netQuantity > 0 ? true : false);
								String netQuantityStr = Math.abs(netQuantity) + "";
								RISK_TIMER_LOGGER.info(" instrument : " +instrument);
								RISK_TIMER_LOGGER.info(" netQuantity : " +netQuantity );
								RISK_TIMER_LOGGER.info(" product : " + product);
								RISK_TIMER_LOGGER.info(" sellCheck : " + sellCheck);
								
								SquareOffOrder order = new SquareOffOrder(instrument,product,netQuantityStr,sellCheck);
								squareOffList.add(order);
							}
						}
						int successCount = 0;
						for (SquareOffOrder order : squareOffList) {
							String[] reply = OrderExecutor.placeSignleOrder(order.getInstrument(), order.getProduct(), order.getQuantity(), order.isSell());
								if (reply[0].equals("error")) {
								// we should notify main process that something went wrong
								// and scheduler must be stopped immediately 
								riskTimerStatus = "error";
								riskTimerStatusMessage = "Something went wrong while executing order in RiskTimer.run()";
								m2m = ""+profitAndLoss;
								riskStatus = SOME_WRONG;
								return;
							} else {
								successCount++;
							}
						}
						
						if (squareOffList.size() == successCount) {
							riskTimerStatus = "complete";
							riskTimerStatusMessage = "Squared off all open positions";
							m2m = ""+profitAndLoss;
							stopLoss = "NA";
			 				profitTarget = "NA";
			 				riskStatus = NO_TRIGGER;
							return;
						}
						
					} else {
						riskTimerStatus = "NO_TRIGGER";
						m2m = ""+profitAndLoss;
						riskTimerStatusMessage = "NA";
						riskStatus = NO_TRIGGER;
					}*/
	}
	
	
	private boolean validStrangleCheck(List<Position> trackPositions) {
		boolean nameCheck = false;
		boolean qtyCheck = false;
		
		Position first = trackPositions.get(0);
		Position second = trackPositions.get(1);
		
		RISK_TIMER_LOGGER.info("Control in validStrangleCheck()");
		
		
		//first name check
		// one must be pe and other must be ce
		String firstToken = first.tradingSymbol.toLowerCase();
		String secondToken = second.tradingSymbol.toLowerCase();
		
		if (firstToken.endsWith("pe")) {
			if (secondToken.endsWith("ce")) {
				nameCheck = true;
			}
		}
		
		if (firstToken.endsWith("ce")) {
			if (secondToken.endsWith("pe")) {
				nameCheck = true;
			}
		}
		
		RISK_TIMER_LOGGER.info("nameCheck : " + nameCheck);
		if (nameCheck) {
			// now size check
			// max 150 difference allowed
			int firstNetQty = Math.abs(first.netQuantity);
			int secondNetQty = Math.abs(second.netQuantity);
			
			int diff = Math.abs(firstNetQty-secondNetQty);
			qtyCheck = ((diff == 0) || (diff == 75));
			RISK_TIMER_LOGGER.info("qtyCheck : " +qtyCheck);
		}
		
		return (nameCheck && qtyCheck);
	}
	
	private void handleConditional() {
		RISK_TIMER_LOGGER.info("Conditional : Entry -> Conditional entry");
		ConditionalOrder order = ConditionalOrderStore.order;
		
		if (order == null) {
			ConditionalOrderStore.code = "NA";
			ConditionalOrderStore.message = "No order to execute";
			RISK_TIMER_LOGGER.info("Conditional : Error -> order is null");
			return;
		}
		String call = order.getCall();
		boolean callOrderStatusCheck = false;
		
		
		boolean sell = order.getPosition().equals("SELL");
		String product = order.getProduct();
		String quantity = order.getQuantity();
		
		Object[] callOrderBook = ConditionalUtil.getAllBookOrders(call, order.getPosition(), product, quantity);
		String code = (String) callOrderBook[0];
		
		if (code.equals("error")) {
			//String message = (String) callOrderBook[1];
			// now persist it in global variable and return
			ConditionalOrderStore.code = "error";
			ConditionalOrderStore.message = "Error while retriving call SL-M orders";
			String msg = call + "," + product + "," + order.getPosition() +"," +quantity;
			RISK_TIMER_LOGGER.info("Conditional : Error -> while finding executed matching order for");
			RISK_TIMER_LOGGER.info(msg);
			return;
		} 
		
		if (code.equals("exception")) {
			String msg = call + "," + product + "," + order.getPosition() +"," +quantity;
			RISK_TIMER_LOGGER.info("Conditional : Error -> no matching order executed found for");
			RISK_TIMER_LOGGER.info(msg);
			ConditionalOrderStore.code = "error";
			ConditionalOrderStore.message = "Conditional : Error -> no matching order executed found ";
			return;
			
		}
		
		/*String callCheck = getExecutionPrice((ArrayList<Order>) callOrderBook[2]);
		if (callCheck.equals("true")) {
			// it means call order(SL-M) got executed
			callOrderStatusCheck = true;
		}*/
		
		Order matchCallOrder = (Order)callOrderBook[2];
		if (matchCallOrder.status.equals("TRIGGER PENDING")) {
			callOrderStatusCheck = false;
		} else {
			callOrderStatusCheck = true;
		}
		//callOrderStatusCheck = (callOrderBook[2] != null);
		
		String put = order.getPut();
		boolean putOrderStatusCheck = false;
		Object[] putOrderBook = ConditionalUtil.getAllBookOrders(put, order.getPosition(), product, quantity);
		code = (String) putOrderBook[0];
		
		if (code.equals("error")) {
			//String message = (String) putOrderBook[1];
			// now persist it in global variable and return
			ConditionalOrderStore.code = "error";
			ConditionalOrderStore.message = "Error while retriving put SL-M orders";
			String msg = put + "," + product + "," + order.getPosition() +"," +quantity;
			RISK_TIMER_LOGGER.info("Conditional : Error -> while finding executed matching order for");
			RISK_TIMER_LOGGER.info(msg);
			return;
		} 
		
		if (code.equals("exception")) {
			String msg = put + "," + product + "," + order.getPosition() +"," +quantity;
			RISK_TIMER_LOGGER.info("Conditional : Error -> no matching order executed found for");
			RISK_TIMER_LOGGER.info(msg);
			ConditionalOrderStore.code = "error";
			ConditionalOrderStore.message = "Conditional : Error -> no matching order executed found ";
			return;
			
		}
		
		/*String putCheck = getExecutionPrice((ArrayList<Order>) putOrderBook[2]);
		if (putCheck.equals("true")) {
			// it means put order(SL-M) got executed
			putOrderStatusCheck = true;
		}*/
		
		//putOrderStatusCheck = (putOrderBook[2] != null);
		Order matchPutOrder = (Order)putOrderBook[2];
		if (matchPutOrder.status.equals("TRIGGER PENDING")) {
			putOrderStatusCheck = false;
		} else {
			putOrderStatusCheck = true;
		}
		
		boolean strangleCheck = callOrderStatusCheck && putOrderStatusCheck;
		boolean onlyCall = callOrderStatusCheck && (putOrderStatusCheck == false);
		boolean onlyPut = putOrderStatusCheck && (callOrderStatusCheck == false);
		
		if (strangleCheck) {
			// it means both orders got executed
			// classic short/long strangle case
			return;
		}
		
		if (onlyCall) {
				String[] reply = ConditionalUtil.getQuote(call);
				if (reply[0].equals("success")) {
					double callLtp = Double.parseDouble(reply[1]);
					Order callOrder = (Order)callOrderBook[2];
					double executedPrice = ConditionalUtil.getExecutionPrice(callOrder);
					double callSL = Double.parseDouble(order.getCallSL());
					RISK_TIMER_LOGGER.info("Conditional -> inside onlyCall if");
					RISK_TIMER_LOGGER.info(" callLtp : " + callLtp + " executedPrice :" +executedPrice + " callSL : " +callSL);
					
					if (sell) {
						double slPrice = executedPrice + callSL;
						if (callLtp >= slPrice) {
							if(completeOtherLeg().equals("error")) {
								otherLegMessage();
							}
						}
					} else {
						double slPrice = executedPrice - callSL;
						if (callLtp <= slPrice) {
							if(completeOtherLeg().equals("error")) {
								otherLegMessage();
							}
						}
					}
				} else {
					ConditionalOrderStore.code = "error";
					ConditionalOrderStore.message = "Error while getting call quote";
					RISK_TIMER_LOGGER.info(" Conditional ->" +ConditionalOrderStore.message );
				}
		}
		
		if (onlyPut) {
				String[] reply = ConditionalUtil.getQuote(put);
				if (reply[0].equals("success")) {
					double putLtp = Double.parseDouble(reply[1]);
					Order putOrder = (Order)putOrderBook[2];
					double executedPrice = ConditionalUtil.getExecutionPrice(putOrder);
					double putSL = Double.parseDouble(order.getPutSL());
					
					RISK_TIMER_LOGGER.info("Conditional -> inside onlyPut if");
					RISK_TIMER_LOGGER.info(" callLtp : " + putLtp + " executedPrice :" +executedPrice + " putSL : " +putSL);
					if (sell) {
						double slPrice = executedPrice + putSL;
						if (putLtp >= slPrice) {
							if(completeOtherLeg().equals("error")) {
								otherLegMessage();
							}
						}
					} else {
						double slPrice = executedPrice - putSL;
						if (putLtp <= slPrice) {
							if(completeOtherLeg().equals("error")) {
								otherLegMessage();
							}
						}
					}
				} else {
					ConditionalOrderStore.code = "error";
					ConditionalOrderStore.message = "Error while getting put quote";
					RISK_TIMER_LOGGER.info(" Conditional ->" +ConditionalOrderStore.message );
					}
				}
	}
	
	
	private boolean lossTriggerCheck(double pnl, int openPositionQuantity, int openPositionCount) {
		// first step : check if user has set stop loss or not
		// if set comparison will be against user stop loss
		RISK_TIMER_LOGGER.info("lossTriggerCheck() START");
		String userLoss = RiskStore.stopLoss;
		RISK_TIMER_LOGGER.info("userLoss = " + userLoss);
		if (userLoss.equals("NA")) {
			RISK_TIMER_LOGGER.info(" stop loss is not entered by user");
			double fixStopLoss = 0.0 - (openPositionQuantity / openPositionCount) * RiskStore.absPointsStopLoss;
			RISK_TIMER_LOGGER.info("fixStopLoss : " + fixStopLoss);
			RISK_TIMER_LOGGER.info("(pnl <= fixStopLoss)" + (pnl <= fixStopLoss));
			if (pnl <= fixStopLoss) {
				RISK_TIMER_LOGGER.info("lossTriggerCheck() END");
				return true;
			} else {
				RISK_TIMER_LOGGER.info("lossTriggerCheck() END");
				return false;
			}
		} else {
			RISK_TIMER_LOGGER.info(" stop loss is entered by user");
			RISK_TIMER_LOGGER.info("(pnl <= userLoss)" + (pnl <= Double.parseDouble(userLoss)));
			if (pnl <= Double.parseDouble(userLoss)) {
				RISK_TIMER_LOGGER.info("lossTriggerCheck() END");
				return true;
			} else {
				RISK_TIMER_LOGGER.info("lossTriggerCheck() END");
				return false;
			}
		}
	}
	
	private boolean profitTriggerCheck(double pnl, int openPositionQuantity, int openPositionCount) {
		// first step : check if user has entered profit target or not
		// if not no comparison can be made 
		RISK_TIMER_LOGGER.info("profitTriggerCheck() START");
		String profitTarget = RiskStore.profitTarget;
		RISK_TIMER_LOGGER.info("profitTarget : " + profitTarget);
		if (profitTarget.equals("NA")) {
			RISK_TIMER_LOGGER.info(" Profit target is not entered by user");
			RISK_TIMER_LOGGER.info("profitTriggerCheck() END");
			return false;
		} else {
			RISK_TIMER_LOGGER.info(" Profit target is entered by user");
			RISK_TIMER_LOGGER.info("(pnl >= Double.parseDouble(profitTarget))" + (pnl >= Double.parseDouble(profitTarget)));
			if (pnl >= Double.parseDouble(profitTarget)) {
				RISK_TIMER_LOGGER.info("profitTriggerCheck() END");
				return true;
			} else {
				RISK_TIMER_LOGGER.info("profitTriggerCheck() END");
				return false;
			}
		}
	}
	
	
	
	/*private boolean lossTriggerCheck(double pnl,int openPositionQuantity,int openPositionCount) {
		double lossTrigger = (openPositionQuantity/openPositionCount) * RiskStore.absPointsStopLoss;
		lossTrigger = 0.0 - lossTrigger;
		RISK_TIMER_LOGGER.info("lossTriggerCheck() START");
		//RISK_TIMER_LOGGER.info("value of openPositionQuantity is :" + openPositionQuantity);
		//RISK_TIMER_LOGGER.info("value of openPositionCount is :" + openPositionCount);
		RISK_TIMER_LOGGER.info("value of lossTrigger is : " +lossTrigger);
		RISK_TIMER_LOGGER.info("value of (pnl <= lossTrigger) is " + (pnl <= lossTrigger));
		RISK_TIMER_LOGGER.info("lossTriggerCheck() END");
		
		
		String userLoss = RiskStore.stopLoss;
		RISK_TIMER_LOGGER.info("userLoss = " +userLoss);
		if(!userLoss.equals("NA")) {
		//	RiskStore.trailStopLossTriggerUI = userLoss;
			if (pnl <= Double.parseDouble(userLoss)) {
				return true;
			}
		}
		
		RISK_TIMER_LOGGER.info("pnl <= lossTrigger" + (pnl <= lossTrigger));
		
		if (pnl <= lossTrigger) {
			return true;
		}
		return false;
	}*/
	
	/*private boolean profitTriggerCheck(double pnl,int openPositionQuantity,int openPositionCount) {
		boolean check = false;
		// check if user has set profit target manually 
		String userProfitTargetStr = RiskStore.profitTarget;
		
		RISK_TIMER_LOGGER.info("profitTriggerCheck() START");
		RISK_TIMER_LOGGER.info("value of userProfitTargetStr : " +userProfitTargetStr);
		
		
		if ((userProfitTargetStr != null) && (!userProfitTargetStr.equals("NA"))) {
			double userProfitTarget = Double.parseDouble(userProfitTargetStr);
			if (pnl >= userProfitTarget) {
				check = true;
			}
		} 
		RISK_TIMER_LOGGER.info("value of (pnl >= userProfitTarget) is " + check);
		RISK_TIMER_LOGGER.info("profitTriggerCheck() END");
		return check;
	}*/
	
/*	private void trailStopLoss(double pnl,int openPositionQuantity,int openPositionCount){
		//boolean flag = false;
		//String trSL = RiskStore.trailStopLoss;
		
		int netQty = openPositionQuantity/openPositionCount;
		double netPoints = pnl/netQty;
		//double netPoints = RoboStore.netPoints;
		
		RISK_TIMER_LOGGER.info("trailStopLoss() START");
		RISK_TIMER_LOGGER.info(" pnl : " +pnl);
		RISK_TIMER_LOGGER.info(" openPositionQuantity : " +openPositionQuantity);
		RISK_TIMER_LOGGER.info(" openPositionCount : " +openPositionCount);
		RISK_TIMER_LOGGER.info(" netPoints : " +netPoints);
		
		if ((netPoints >= 0.5) && (netPoints <= 2)) {
			setTrailing(-2.0);
			return;
		}
		
		if ((netPoints > 1) && (netPoints <= 2)) {
			setTrailing(1);
			return;
		}
		
		if ((netPoints > 2) && (netPoints <= 4)) {
			setTrailing(0.2);
			return;
		}
		
		if ((netPoints > 4) && (netPoints <= 7)) {
			setTrailing(0.2);
			return;
		}
		
		if ((netPoints > 7) && (netPoints <= 10)) {
			setTrailing(4);
			return;
		}
		
		if ((netPoints > 10) && (netPoints <= 15)) {
			setTrailing(7);
			return;
		}
		
		if ((netPoints > 15) && (netPoints <= 20)) {
			setTrailing(12);
			return;
		}
		
		if ((netPoints > 20) && (netPoints <= 25)) {
			setTrailing(15);
			return;
		}
		
		//new dynamic logic
		if ((netPoints > 3) && (netPoints <= 5)) {
			setTrailing(-1);
			return;
		}
		
		if ((netPoints > 5) && (netPoints <= 10)) {
			setTrailing(1);
			return;
		}
		
		if ((netPoints > 10) && (netPoints <= 15)) {
			setTrailing(netPoints-8);
			return;
		}
		
		if ((netPoints > 15) && (netPoints <= 20)) {
			setTrailing(netPoints-9);
			return;
		}
		
		if ((netPoints > 20) && (netPoints <= 25)) {
			setTrailing(netPoints-6);
			return;
		}
		
		if ((netPoints > 25) && (netPoints <= 35)) {
			setTrailing(netPoints-7);
			return;
		}
		
		if ((netPoints > 35) && (netPoints <= 50)) {
			setTrailing(netPoints-8);
			return;
		}
		
		if (netPoints > 50)  {
			setTrailing(netPoints-10);
			return;
		}
		
		if (trSL.equals("NA")) {
			// initial point
		} else {
			
		}
		
		//return flag;
	
	 // logic for one min robo	
		if ((netPoints > 6) ) {
			setTrailing(0);
		}
		
		if ((netPoints > 10) ) {
			setTrailing(3);
		}
		
		
		if ((netPoints > 15) ) {
			setTrailing(5);
		}
		
		if ((netPoints > 20) ) {
			setTrailing(10);
		}
		
	}*/
	
	/*private void setTrailing(double level) {
		String slStr = RiskStore.trailStopLoss;
		
		RISK_TIMER_LOGGER.info(" setTrailing()  " );
		RISK_TIMER_LOGGER.info(" level :  " + level );
		
		
		if (slStr.equals("NA")) {
			RiskStore.trailStopLoss = level + "";
		} else {
			double sl = Double.parseDouble(slStr);
			if (level > sl) {
				RiskStore.trailStopLoss = level + "";
			}
		}
	}*/
	
	
	
	private String completeOtherLeg() {
		String[] response = ConditionalUtil.firePendingOrder();
		return response[0];
	}
	
	private String getExecutionPrice(ArrayList<Order> orderList) {
		for (Order item : orderList) {
			if (item.status.equals("COMPLETE")) {
				return "true";
			}
		}
		return  "false";
	}
	
	
	private void otherLegMessage() {
		ConditionalOrderStore.code = "error";
		ConditionalOrderStore.message = "Error while entering other leg of order";
	}



	
	
	

}
