package com.kite.aws.order.conditional;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.kite.aws.login.KiteStore;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;

public class ConditionalOrderExecutor {
	
	public static String[] placeConditionalOrder(ConditionalOrder order) {
		KiteConnect connect = KiteStore.kiteconnect;
		String[] reply = new String[2]; 
		
		if (connect == null) {
			//form response and return it to caller
			reply[0] = "error";
			reply[1] = "connect is null in ConditionalOrderExecutor.placeConditionalOrder()";
			return reply;
		}
		//before placing SL-M orders, please check if ltp values are coming zero or not.
		// if zero then do nothing.
		double call = getLTP(order.getCall());
		double put = getLTP(order.getPut());
		
		if (call == 0.0 || put == 0.0) {
			reply[0] = "error";
			reply[1] = "Call or Put LTP is coming zero from Kite Server";
			return reply;
		}
		
		String callTriggerPrice = null;
		String putTriggerPrice = null;
		 Map<String, Object> callParam = new HashMap<String, Object>();
		 callParam.put("tradingsymbol", order.getCall());
		 callParam.put("exchange", "NFO");
		// double callLtp = getLTP(order.getCall());
		 double callLtp = call;
		/* if (callLtp == 0.0) {
			 reply[0] = "error";
			 reply[1] = "Call LTP came as zero. Please try again";
			 return reply;
		 }*/
		 if (order.getPosition().equalsIgnoreCase("sell")) {
			 callParam.put("transaction_type", "SELL");
			 callTriggerPrice = (callLtp - Double.parseDouble(order.getCallEntry())) + "";
		 } else {
			 callParam.put("transaction_type", "BUY"); 
			 callTriggerPrice = (getLTP(order.getCall()) + Double.parseDouble(order.getCallEntry())) + "";
		 }
		 callParam.put("order_type","SL-M");
		 callParam.put("quantity", order.getQuantity());
		 callParam.put("product", order.getProduct());
		 callParam.put("validity","DAY");
		 callParam.put("trigger_price", callTriggerPrice);
		 
		 Map<String, Object> putParam = new HashMap<String, Object>();
		 putParam.put("tradingsymbol", order.getPut());
		 putParam.put("exchange", "NFO");
		// double putLtp = getLTP(order.getPut());
		 double putLtp = put;
		 /*if (putLtp == 0.0) {
			 reply[0] = "error";
			 reply[1] = "Put LTP came as zero. Please try again";
			 return reply;
		 }*/
		 if (order.getPosition().equals("SELL")) {
			 putParam.put("transaction_type", "SELL");
			 putTriggerPrice = (putLtp - Double.parseDouble(order.getPutEntry())) + "";
		 } else {
			 putParam.put("transaction_type", "BUY"); 
			 putTriggerPrice = (putLtp + Double.parseDouble(order.getPutEntry())) + "";
		 }
		 putParam.put("order_type","SL-M");
		 putParam.put("quantity", order.getQuantity());
		 putParam.put("product", order.getProduct());
		 putParam.put("validity","DAY");
		 putParam.put("trigger_price", putTriggerPrice);
		 
		 try {
			 connect.placeOrder(callParam, "regular");
			 connect.placeOrder(putParam, "regular");
			 reply[0] = "success";
			 reply[1] = "success";
		 } catch(KiteException e) {
			 reply[0] = "error";
			 reply[1] = "KiteException in ConditionalOrderExecutor.placeConditionalOrder()";
		 } catch(JSONException e) {
			 reply[0] = "error";
			 reply[1] = "JSONException in ConditionalOrderExecutor.placeConditionalOrder()";
		 }
		 return reply;
	}
	
	public static double getLTP(String symbol) {
		double ltp = 0.0;
		KiteConnect connect = KiteStore.kiteconnect;
		try {
			ltp = connect.getQuote("NFO", symbol).lastPrice;
		} catch (JSONException e) {
			ltp = 0.0;
		} catch (KiteException e) {
			ltp = 0.0;
		}
		return ltp;
	}

}
