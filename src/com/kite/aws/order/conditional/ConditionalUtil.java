package com.kite.aws.order.conditional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.kite.aws.login.KiteStore;
import com.kite.aws.order.OrderExecutor;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Order;

public class ConditionalUtil {
	
	private static final String EXCHANGE = "NFO";
	public static final String TRIGGER_PENDING = "TRIGGER PENDING";
	
	public static String[] getQuotes(String call,String put) {
		String[] quotes = new String[3];
		KiteConnect connect = KiteStore.kiteconnect; 
		
		if (connect != null) {
			try {
				quotes[0] = connect.getQuote(EXCHANGE, call).lastPrice + "";
				quotes[1] = connect.getQuote(EXCHANGE, put).lastPrice + "";
				quotes[2] = "success";
			} catch (KiteException e) {
				quotes[0] = "error";
				quotes[1] = "error";
				quotes[2] = "KiteException in ConditionalUtil.getQuotes()";
			} catch (JSONException e) {
				quotes[0] = "error";
				quotes[1] = "error";
				quotes[2] = "JSONException in ConditionalUtil.getQuotes()";
			}
		} else {
			quotes[0] = "error";
			quotes[1] = "error";
			quotes[2] = "KiteConnect is null in ConditionalUtil.getQuotes()";
		}
		return quotes;
	}
	
	public static ConditionalOrder createOrder(String data) {
		ConditionalOrder order = null;
		String[] tokens = data.split("-");
		
		if (tokens.length == 10) {
			order = new ConditionalOrder(tokens[0],tokens[1],tokens[2],tokens[3],tokens[4],tokens[5],tokens[6],tokens[7],tokens[8],tokens[9]);
		}
		
		return order;
	}
	
	public static Object[] getAllBookOrders(String symbol,String position,String product,String quantity) {
		
		String code = "";
		String message = "";
		
		Object[] response = new Object[3];
		
		KiteConnect kiteConnect = KiteStore.kiteconnect;
		Order matchingOrder = null;
		
		if (kiteConnect == null) {
			code = "error";
			message ="kiteConnect is null";
		//	matchingOrder = null;
			
			response[0] = code;
			response[1] = message;
			response[2] = matchingOrder;
			return response;
		}
		
		try {
			Order orderBook = kiteConnect.getOrders();
			List<Order> orderList = orderBook.orders;
			int size = orderList.size();
			
			if (size > 0) {
				for (int i = size-1; i>0 ;i--) {
					Order item = orderList.get(i);
				//	if (item.orderType.equals("SL-M")) {
						String status = item.status;
						String tradingSymbol = item.tradingSymbol;
						String transactionType = item.transactionType;
						String productType = item.product;
						String orderQuantity = item.quantity;
					//	String orderType = item.orderType;
						
						/*if  ((status.equals("COMPLETE")) && (tradingSymbol.equals(symbol))) {
							matchingOrderList.add(item);
						}*/
						
						boolean symbolCheck = tradingSymbol.equals(symbol);
						boolean positionCheck = transactionType.equals(position);
						boolean productCheck = productType.equals(product);
						boolean quantityCheck = orderQuantity.equals(quantity);
						//boolean statusCheck = (status.equals("COMPLETE") || status.equals("OPEN"));
						//boolean statusCheck = status.equals(TRIGGER_PENDING);
						boolean statusCheck = status.equals("COMPLETE") || status.equals(TRIGGER_PENDING);
						if (symbolCheck && positionCheck && productCheck && quantityCheck && statusCheck) {
					//		matchingOrderList.add(item);
							matchingOrder = item;
							code = "success";
							message ="success";
							response[0] = code;
							response[1] = message;
							response[2] = matchingOrder;
							return response;
						}
				//	}
				}
			} else {
				code = "error";
				message ="order book is empty";
				matchingOrder = null;
				
				response[0] = code;
				response[1] = message;
				response[2] = matchingOrder;
				return response;
				
			}
		} catch (JSONException e) {
			code = "error";
			message ="JSONException in NewOrderBook.getAllBookOrders()";
			matchingOrder = null;
			
			response[0] = code;
			response[1] = message;
			response[2] = matchingOrder;
			return response;
		}
		catch (KiteException e) {
			code = "error";
			message ="KiteException in NewOrderBook.getAllBookOrders()";
			matchingOrder = null;
			
			response[0] = code;
			response[1] = message;
			response[2] = matchingOrder;
			return response;
		}
		
		
		if (matchingOrder == null) {
			code = "exception";
			message ="Found no matching order";
		}
		
		response[0] = code;
		response[1] = message;
		response[2] = matchingOrder;
		return response;
		
	}
	
	public static String[] squareOff() {
		String[] response = new String[2];
		String[] reply = OrderExecutor.exitAllPositions();
		if (reply[0].equals("success")) {
			String[] reply1 = cancelAllPendingOrders();
			if (reply1[0].equals("error")) {
				response[0] = "error";
				response[1] = reply1[1];
			}
		} else {
			response[0] = "error";
			response[1] = reply[1];
		}
		return response;
	}
	
	public static String[] cancelAllPendingOrders() {
		ConditionalOrderStore.order = null;
		String[] reply = new String[2];
		KiteConnect kiteConnect = KiteStore.kiteconnect;
		if (kiteConnect == null) {
			reply[0] = "error";
			reply[1] = "kiteConnect is null in ConditionalUtil.cancelAllPendingOrders()";
		}
		
		Order orderBook;
		try {
			orderBook = kiteConnect.getOrders();
			List<Order> orderList = orderBook.orders;
			for (Order order : orderList) {
				if (order.status.equals(TRIGGER_PENDING)) {
					kiteConnect.cancelOrder(order.orderId, order.orderVariety);
				}
			}
			reply[0] = "success";
			reply[1] = "success";
		} catch (JSONException e) {
			reply[0] = "error";
			reply[1] = "JSONException in ConditionalUtil.cancelAllPendingOrders()";
		} catch (KiteException e) {
			reply[0] = "error";
			reply[1] = "KiteException in ConditionalUtil.cancelAllPendingOrders()";
		}
		return reply;
	}
	
	public static String[] getQuote(String symbol) {
		String[] quotes = new String[2];
		KiteConnect connect = KiteStore.kiteconnect; 
		
		if (connect != null) {
			try {
				double ltp = connect.getQuote(EXCHANGE, symbol).lastPrice;
				if (ltp > 0.0) {
					quotes[1] = ltp + "";
					quotes[0] = "success";
				} else {
					quotes[0] = "error";
					quotes[1] = "Value of quote is zero  in ConditionalUtil.getQuote()";
				}
				
			} catch (KiteException e) {
				quotes[0] = "error";
				quotes[1] = "KiteException in ConditionalUtil.getQuote()";
			} catch (JSONException e) {
				quotes[0] = "error";
				quotes[1] = "KiteException in ConditionalUtil.getQuote()";
			}
		} else {
			quotes[0] = "error";
			quotes[1] = "KiteConnect is null in ConditionalUtil.getQuote()";
		}
		return quotes;
	}
	
	public static String[] firePendingOrder() {
		String[] reply = new String[2];
		KiteConnect kiteConnect = KiteStore.kiteconnect;
		
		if (kiteConnect == null) {
			reply[0] = "error";
			reply[1] = "kiteConnect is null in ConditionalUtil.cancelAllPendingOrders()";
			return reply;
		}
		
		Order orderBook;
		try {
			orderBook = kiteConnect.getOrders();
			List<Order> orderList = orderBook.orders;
			for (Order order : orderList) {
				if (order.status.equals(TRIGGER_PENDING)) {
					//kiteConnect.cancelOrder(order.orderId, order.orderVariety);
					 Map<String, Object> param = new HashMap<String, Object>();
					 param.put("tradingsymbol", order.tradingSymbol);
					 param.put("exchange", "NFO");
					 if (order.transactionType.equalsIgnoreCase("sell")) {
						 param.put("transaction_type", "SELL");
					 } else {
						 param.put("transaction_type", "BUY"); 
					 }
					 param.put("order_type","MARKET");
					 param.put("quantity", order.quantity);
					 param.put("product", order.product);
					 param.put("validity","DAY");
					 kiteConnect.modifyOrder(order.orderId, param, "regular");
					// break;
				}
			}
			reply[0] = "success";
			reply[1] = "success";
		} catch (JSONException e) {
			reply[0] = "error";
			reply[1] = "JSONException in ConditionalUtil.cancelAllPendingOrders()";
		} catch (KiteException e) {
			reply[0] = "error";
			reply[1] = "KiteException in ConditionalUtil.cancelAllPendingOrders()";
		}
		return reply;
	}
	
	/*public static double getExecutionPrice(Order order) {
		String orderType = order.orderType;
		double price = 0.0;
		
		if (orderType.equals("MARKET")) {
			price = Double.parseDouble(order.averagePrice);
		}else {
			if (orderType.equals("LIMIT")) {
				price = Double.parseDouble(order.price);
			}
		}
		return price;
	}*/
	
	public static double getExecutionPrice(Order order) {
		double price =  Double.parseDouble(order.price);
		if (price == 0.0) {
			return Double.parseDouble(order.averagePrice);
		} else {
			return price;
		}
	}
}
