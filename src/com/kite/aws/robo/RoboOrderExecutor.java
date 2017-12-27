package com.kite.aws.robo;

import static com.kite.aws.login.GlobalLoggerHandler.ORDER_LOGGER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.kite.aws.login.KiteStore;
import com.kite.aws.util.GlobalConstants;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Order;
import com.rainmatter.models.Position;
import static com.kite.aws.login.GlobalLoggerHandler.ROBO_LOGGER;

public class RoboOrderExecutor {

	private static Map<String, Object> param;

//	private static Map<String, Object> shortParam;

	//private static final String instrument = "NIFTY17NOVFUT";
	
	

	private static final String product = "MIS";

	private static final String success = "success";

	private static final String error = "error";

	private static final String msg1 = "KiteConnect is null in  RoboOrderExecutor.longFut()";

	private static final String msg2 = "JSONException or KiteException in RoboOrderExecutor.longFut()";

	private static final String msg3 = "KiteConnect is null in  RoboOrderExecutor.shortFut()";

	private static final String msg4 = "JSONException or KiteException in RoboOrderExecutor.shortFut()";

	static {
		
		param = new HashMap<String, Object>();
		//longParam.put("tradingsymbol", instrument);
		param.put("exchange", "NFO");
	//	param.put("transaction_type", "SELL");
		param.put("order_type", "MARKET");
	//	param.put("quantity", quantity);
		param.put("product", product);
		param.put("validity", "DAY");

		/*shortParam = new HashMap<String, Object>();
		//shortParam.put("tradingsymbol", instrument);
		shortParam.put("exchange", "NFO");
		shortParam.put("transaction_type", "SELL");
		shortParam.put("order_type", "MARKET");
		shortParam.put("quantity", quantity);
		shortParam.put("product", product);
		shortParam.put("validity", "DAY");*/
	}

	/*public static String[] longFut() {
		if (KiteStore.kiteconnect == null) {
			return new String[] { error, msg1 };
		}
		try {
			KiteStore.kiteconnect.placeOrder(longParam, "regular");
		} catch (JSONException | KiteException e) {
			return new String[] { error, msg2 };
		}
		return new String[] { success, success };
	}*/
	
	public static String[] up(double ltp,String symbol) {
		if (KiteStore.kiteconnect == null) {
			return new String[] { error, msg1 };
		}
		try {
			setParamsUI(symbol);
			KiteStore.kiteconnect.placeOrder(param, "regular");
		} catch (JSONException | KiteException e) {
			return new String[] { error, msg2 };
		}
		return new String[] { success, success };
	}

	/*public static String[] shortFut() {

		if (KiteStore.kiteconnect == null) {
			return new String[] { error, msg3 };
		}
		try {
			KiteStore.kiteconnect.placeOrder(shortParam, "regular");
		} catch (JSONException | KiteException e) {
			return new String[] { error, msg4 };
		}
		return new String[] { success, success };
	}*/
	
	public static String[] down(double ltp,String symbol) {

		if (KiteStore.kiteconnect == null) {
			return new String[] { error, msg3 };
		}
		try {
			setParamsUI(symbol);
			KiteStore.kiteconnect.placeOrder(param, "regular");
		} catch (JSONException | KiteException e) {
			return new String[] { error, msg4 };
		}
		return new String[] { success, success };
	}
	
	
	private static void setParamsUI(String symbol) {
		/*if ((!position.equals("")) && (position.equals("buy"))) {
			param.put("transaction_type", "BUY");
		}
		
		if ((!position.equals("")) && (position.equals("sell"))) {
			param.put("transaction_type", "SELL");
		}
		
		if ((!quantity.equals(""))) {
			param.put("quantity", quantity);
		}
		
		//temp change
		
		if(position.equals("")) {
			param.put("transaction_type", "SELL");
		}
		
		if ((quantity.equals(""))) {
			param.put("quantity", "300");
		}*/
		
		if ((RoboStore.position!=null) && (RoboStore.position.equalsIgnoreCase("buy"))) {
			param.put("transaction_type", "BUY");
		}
		
		if ((RoboStore.position!=null) && (RoboStore.position.equalsIgnoreCase("sell"))) {
			param.put("transaction_type", "SELL");
		}
		
		if (RoboStore.quantity != null) {
			param.put("quantity", RoboStore.quantity);
		}
		
		param.put("tradingsymbol",symbol);
	}
	
	
	public static void exitRoboPositions() {
		KiteConnect connect = KiteStore.kiteconnect;
		 
		 if (connect == null) {
			 return;
		 } else {
				Position position = null;
			 try {
					position = connect.getPositions();
				} catch (JSONException e) {
					 return;
				} catch (KiteException e) {
					 return;
				}
			 
			 List<Position> netPositions = position.netPositions;
			 List<Position> openPositions = new ArrayList<Position>();
			 int openPositionCount = 0;
			 for (Position item : netPositions) {
				 if ((item.netQuantity != 0) && (item.product.equals("MIS"))) {
					 openPositions.add(item);
					 openPositionCount++;
				 }
			 }
			 
			 if (openPositionCount == 0) {
				 return;
			 } else {
				 for (Position item : openPositions) {
					 ROBO_LOGGER.info("Placing exit order from RoboOrderExecutor");
					 if (item.netQuantity < 0) {
						 ROBO_LOGGER.info("RoboOrderExecutor : (item.netQuantity < 0) " + (item.netQuantity < 0));
						 ROBO_LOGGER.info("item.tradingSymbol : " + item.tradingSymbol);
						 ROBO_LOGGER.info("MIS");
						 ROBO_LOGGER.info("Math.abs(item.netQuantity) : " + Math.abs(item.netQuantity));
						 ROBO_LOGGER.info("false");
						 placeSignleOrder(item.tradingSymbol,"MIS",Math.abs(item.netQuantity)+"",false);
					 } else {
						 ROBO_LOGGER.info("RoboOrderExecutor : (item.netQuantity < 0) " + (item.netQuantity < 0));
						 ROBO_LOGGER.info("item.tradingSymbol : " + item.tradingSymbol);
						 ROBO_LOGGER.info("MIS");
						 ROBO_LOGGER.info("Math.abs(item.netQuantity) : " + Math.abs(item.netQuantity));
						 ROBO_LOGGER.info("true");
						 placeSignleOrder(item.tradingSymbol,"MIS",Math.abs(item.netQuantity)+"",true);
					 }
					 
				 }
			 }
		 }
	}
	
	
	
	public static String[] placeSignleOrder(String instrument,String product, String quantity,boolean isSell) {
		String code = "error";
		String returnMessage = "";
		
		/*int intQuantity = Integer.parseInt(quantity);
		intQuantity = Math.abs(intQuantity);
		String realQuantity = "" + intQuantity;*/
		
	//	String realInsrtument = InstrumentStore.getInstance(Login.kiteconnect).getTradingSymbol(instrument);
		String logMsg =  instrument + " , " +product + " , " +quantity + " , "+isSell;
		ORDER_LOGGER.info("Got order for execution :" + logMsg);
		
		 Map<String, Object> orderParam = new HashMap<String, Object>();
		 orderParam.put("tradingsymbol", instrument);
		 orderParam.put("exchange", "NFO");
		 if (isSell) {
			 orderParam.put("transaction_type", "SELL");
		 } else {
			 orderParam.put("transaction_type", "BUY"); 
		 }
		 orderParam.put("order_type","MARKET");
		 orderParam.put("quantity", quantity);
		 orderParam.put("product", product);
		 orderParam.put("validity","DAY");
		 
		 KiteConnect connect = KiteStore.kiteconnect;
		 
		 Order order = null;
		 
		 try {
			 order = connect.placeOrder(orderParam, "regular");
	 	 } catch (JSONException | KiteException e) {
	 		code = "error";
	 		returnMessage = "Error while executing order...Please take manual control";
		  }
		 if ((order != null)) {
			 code = "success";
		 	 returnMessage = "Executed order...";
		 	ORDER_LOGGER.info("Executed order : " + logMsg);
		 }
		 return new String[] { code,returnMessage };
	}
	
	
	public static String getSymbol(int ltp,boolean up) {
		int rem = (ltp % 100);
		if (RoboStore.position.equalsIgnoreCase("buy")) {
			if (up) {
				// buy in-the-money call
				// so find in-the-money strike price
				return RoboStore.instrument + (ltp - rem) + "CE";
			} else {
				// buy in-the-money put
				// so find in-the-money strike price
				return RoboStore.instrument + (ltp + 100 - rem) + "PE";
			}
		}
		
		if (RoboStore.position.equalsIgnoreCase("sell")) {
			if (up) {
				// sell in-the-money put
				// so find in-the-money strike price
				return RoboStore.instrument + (ltp + 100 - rem) + "PE";
				
			} else {
				// sell in-the-money call
				// so find in-the-money strike price
				return RoboStore.instrument + (ltp - rem) + "CE";
			}
		}
		return null;
		
	}

}
