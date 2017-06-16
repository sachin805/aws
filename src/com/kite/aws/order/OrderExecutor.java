package com.kite.aws.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Order;
import com.rainmatter.models.Position;
import com.kite.aws.login.KiteStore;

public class OrderExecutor {
	
	public static String[] placeKiteOrder(String quantity,
			String callSymbol, String putSymbol, boolean sell,String productType) {
		String errorCode = "300";
		String returnMessage = "";
		KiteConnect connect = KiteStore.kiteconnect;
		
		if (connect != null) {
		 
		 Map<String, Object> callParam = new HashMap<String, Object>();
		 callParam.put("tradingsymbol", callSymbol);
		 callParam.put("exchange", "NFO");
		 if (sell) {
		 callParam.put("transaction_type", "SELL");
		 } else {
			 callParam.put("transaction_type", "BUY"); 
		 }
		 callParam.put("order_type","MARKET");
		 callParam.put("quantity", quantity);
		 callParam.put("product", productType);
		 callParam.put("validity","DAY");
		 
		 Map<String, Object> putParam = new HashMap<String, Object>();
		 putParam.put("tradingsymbol", putSymbol);
		 putParam.put("exchange", "NFO");
		 if (sell) {
			 putParam.put("transaction_type", "SELL");
		 } else {
			 putParam.put("transaction_type", "BUY"); 
		 }
		 putParam.put("order_type","MARKET");
		 putParam.put("quantity", quantity);
		 putParam.put("product", productType);
		 putParam.put("validity","DAY");
		 
		 try {
			 connect.placeOrder(callParam, "regular");
			 connect.placeOrder(putParam, "regular");
			 errorCode = "200";
		 	 returnMessage = "Executed both orders successfully !!";
	 	 } catch (KiteException e) {
	 		errorCode = "300";
	 		returnMessage = "Error while executing order(s). Please handle it manually immediately !!";
		  }
		} else {
			errorCode = "300";
	 		returnMessage = "kiteConnect is null in RestOrderExecutor.placeKiteOrder()";
		}
		 return new String[] { errorCode, returnMessage };
	}
	
	public static String[] placeSignleOrder(String instrument,String product, String quantity,boolean isSell) {
		String code = "error";
		String returnMessage = "";
		
		/*int intQuantity = Integer.parseInt(quantity);
		intQuantity = Math.abs(intQuantity);
		String realQuantity = "" + intQuantity;*/
		
	//	String realInsrtument = InstrumentStore.getInstance(Login.kiteconnect).getTradingSymbol(instrument);
		
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
		 }
		 return new String[] { code,returnMessage };
	}

	
	public static String[] exitAllPositions() {
		 KiteConnect connect =KiteStore.kiteconnect;
		 Position positions = null;
		 String errorCode = null;
		 String returnMessage = null;
		 
		 String[] reply = new String[2];
		 
		 if (connect == null) {
			 errorCode = "error";
			 returnMessage = "Please genrate access token first !!";
			 reply[0] = errorCode;
			 reply[1] = returnMessage;
			 return reply;
		 }
		 
		 try {
			positions = connect.getPositions();
		} catch (JSONException e) {
			//e.printStackTrace();
			errorCode = "error";
			returnMessage = "JSONException in exitAllPositions()";
			reply[0] = errorCode;
			reply[1] = returnMessage;
			return reply;
		} catch (KiteException e) {
			//e.printStackTrace();
			errorCode = "error";
			returnMessage = "KiteException in exitAllPositions()";
			reply[0] = errorCode;
			reply[1] = returnMessage;
			return reply;
		}
		 int openPositionCount = 0;
		 for (Position item : positions.netPositions) {
				int netQuantity = item.netQuantity;
				if (netQuantity != 0) {
					openPositionCount = openPositionCount + 1;
				}
		}
		 
		 if (openPositionCount == 0) {
			 errorCode = "error";
			 returnMessage = "No open positions to square off";
			 reply[0] = errorCode;
			 reply[1] = returnMessage;
			 return reply;
		} else {
			List<SquareOffOrder> squareOffList = new ArrayList<SquareOffOrder>();
			for (Position item : positions.netPositions) {
				if (item.netQuantity != 0) {
					String instrument = item.tradingSymbol;
					int netQuantity = item.netQuantity;
					String product = item.product;
					boolean sellCheck = ( netQuantity > 0 ? true : false);
					String netQuantityStr = Math.abs(netQuantity) + "";
					SquareOffOrder order = new SquareOffOrder(instrument,product,netQuantityStr,sellCheck);
					squareOffList.add(order);
				}
			}
			
			int successCount = 0;
			for (SquareOffOrder order : squareOffList) {
				String[] response = placeSignleOrder(order.getInstrument(), order.getProduct(), order.getQuantity(), order.isSell());
				if (response[0].equals("error")) {
					 errorCode = "error";
					 returnMessage = "Error while squaring off....Please handle it manually";
					 reply[0] = errorCode;
					 reply[1] = returnMessage;
					 return reply;
				} else {
					successCount++;
				}
			}
			if (squareOffList.size() == successCount) {
				 errorCode = "success";
				 returnMessage = "Squared off all open positions !!";
				 reply[0] = errorCode;
				 reply[1] = returnMessage;
				 return reply;
			}
		}
	 return reply;
	}
	
	public static String[] exitHalfPositions() {
		 KiteConnect connect = KiteStore.kiteconnect;
		 Position positions = null;
		 String errorCode = null;
		 String returnMessage = null;
		 
		 String[] reply = new String[2];
		 
		 if (connect == null) {
			 errorCode = "error";
			 returnMessage = "Please genrate access token first !!";
			 reply[0] = errorCode;
			 reply[1] = returnMessage;
			 return reply;
		 }
		 
		 try {
			positions = connect.getPositions();
		} catch (JSONException e) {
			//e.printStackTrace();
			errorCode = "error";
			returnMessage = "JSONException in exitHalfPositions()";
			reply[0] = errorCode;
			reply[1] = returnMessage;
			return reply;
		} catch (KiteException e) {
			//e.printStackTrace();
			errorCode = "error";
			returnMessage = "KiteException in exitHalfPositions()";
			reply[0] = errorCode;
			reply[1] = returnMessage;
			return reply;
		}
		 int openPositionCount = 0;
		 for (Position item : positions.netPositions) {
				int netQuantity = item.netQuantity;
				if (netQuantity != 0) {
					openPositionCount = openPositionCount + 1;
				}
		}
		 
		 if (openPositionCount == 0) {
			 errorCode = "error";
			 returnMessage = "No open positions to square off";
			 reply[0] = errorCode;
			 reply[1] = returnMessage;
			 return reply;
		} else {
			List<SquareOffOrder> squareOffList = new ArrayList<SquareOffOrder>();
			for (Position item : positions.netPositions) {
				if (item.netQuantity != 0) {
					String instrument = item.tradingSymbol;
					int netQuantity = getHalfQuantity(item.netQuantity);
					String product = item.product;
					boolean sellCheck = ( netQuantity > 0 ? true : false);
					String netQuantityStr = Math.abs(netQuantity) + "";
					SquareOffOrder order = new SquareOffOrder(instrument,product,netQuantityStr,sellCheck);
					squareOffList.add(order);
				}
			}
			
			int successCount = 0;
			for (SquareOffOrder order : squareOffList) {
				String[] response = placeSignleOrder(order.getInstrument(), order.getProduct(), order.getQuantity(), order.isSell());
				if (response[0].equals("error")) {
					 errorCode = "error";
					 returnMessage = "Error while squaring off half quantity....Please handle it manually";
					 reply[0] = errorCode;
					 reply[1] = returnMessage;
					 return reply;
				} else {
					successCount++;
				}
			}
			if (squareOffList.size() == successCount) {
				 errorCode = "success";
				 returnMessage = "Squared off half positions !!";
				 reply[0] = errorCode;
				 reply[1] = returnMessage;
				 return reply;
			}
		}
	 return reply;
	}
	
	public static int getHalfQuantity(int qty) {
		int ans = 0;
		if (qty%2 == 0) {
			ans = qty/2;
		} else {
			if (qty > 0) {
				ans = qty/2 - 37;
			} else {
				ans = qty/2 + 37;
			}
		}
		//System.out.println(ans);
		return ans;
	}
}
