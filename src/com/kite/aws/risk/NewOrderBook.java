package com.kite.aws.risk;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.kite.aws.login.KiteStore;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Order;
import com.rainmatter.models.Position;
import static com.kite.aws.login.GlobalLoggerHandler.NEW_ORDERBOOK_LOGGER;;

public class NewOrderBook {
	
	public static Object[] getPL(Position entry) {
		int netQuantity = entry.netQuantity;
		double pl = 0.0;
		
		String code = "";
		String message = "";
		String profitLoss = "";
		
		if (netQuantity == 0) {
			code = "success";
			message = "no open positions";
			profitLoss = "0.0";
		}
		
		//buy
		if (netQuantity > 0) {
			double ltp = entry.lastPrice;
			
			int sellQty = entry.sellQuantity;
			
			if (sellQty == 0) { // clean position
				pl = (ltp - entry.buyPrice) * netQuantity;
				code = "success";
				message = "success";
				profitLoss ="" + pl;
				return new Object[] { code,message,profitLoss};
			}
			
			//get all matching orders
			//Object[] response = getAllBookOrders(entry.tradingSymbol,"BUY");
			Object[] response = getAllBookOrders(entry);
			String status = (String) response[0];
			if (status.equals("success")) {
				ArrayList<Order> matchList = (ArrayList<Order>) response[2];
				matchList = sortOrderList(matchList);
				int totalQty = netQuantity;
				double average = 0.0;
				double positionValue = 0.0;
				double buyValue = 0.0;
				double sellValue = 0.0;
				for (Order item : matchList) {
					String transactionType = item.transactionType;
					String orderType = item.orderType;
					int qty = Integer.parseInt(item.quantity);
					
					if (transactionType.equals("SELL")) {
						if (orderType.equals("MARKET")) {
							//double price = Double.parseDouble(item.averagePrice);
							double price = getPrice(item);
							//positionValue = positionValue + (qty * price);
							sellValue = sellValue + (qty * price);
						} else {
							if (orderType.equals("LIMIT")) {
							//	double price = Double.parseDouble(item.price);
								double price = getPrice(item);
								//positionValue = positionValue + (qty * price);
								sellValue = sellValue + (qty * price);
							}
						}
						totalQty = totalQty + qty;
					}
					
					if (transactionType.equals("BUY")) {
						if (orderType.equals("MARKET")) {
							//double price = Double.parseDouble(item.averagePrice);
							double price = getPrice(item);
							//positionValue = positionValue - (qty * price);
							//positionValue = positionValue + (qty * price);
							buyValue = buyValue + (qty * price);
						} else {
							if (orderType.equals("LIMIT")) {
							//	double price = Double.parseDouble(item.price);
								double price = getPrice(item);
								//positionValue = positionValue - (qty * price);
								//positionValue = positionValue + (qty * price);
								buyValue = buyValue + (qty * price);
							}
						}
						totalQty = totalQty - qty;
					}
					if (totalQty <= 0) {
						break;
					}
				}
				positionValue = buyValue - sellValue;
				average = positionValue/Math.abs(netQuantity);
				//average = Math.abs(average);
				pl = (ltp - average) * netQuantity;
				code = "success";
				message = "success";
				profitLoss ="" + pl;
				NEW_ORDERBOOK_LOGGER.info(entry.tradingSymbol + " " + entry.netQuantity);
				NEW_ORDERBOOK_LOGGER.info("profitLoss = " + profitLoss);
			} else {
				code = "error";
				message = (String) response[1];
				profitLoss = "0.0";
			}
			
		} else { // Short sell position
			double ltp = entry.lastPrice;
			int buyQty = entry.buyQuantity;
			int netQty = Math.abs(entry.netQuantity);
			if (buyQty == 0) { // clean position
				pl = (entry.sellPrice - ltp) * netQty;
				code = "success";
				message = "success";
				profitLoss ="" + pl;
				return new Object[] {code,message,profitLoss};
			}
			
			//get all matching orders
			//Object[] response = getAllBookOrders(entry.tradingSymbol,"SELL");
			Object[] response = getAllBookOrders(entry);
			String status = (String) response[0];
			
			if (status.equals("success")) {
					ArrayList<Order> matchList = (ArrayList<Order>) response[2];
					matchList = sortOrderList(matchList);
					int totalQty = Math.abs(netQuantity);
					double average = 0.0;
					double positionValue = 0.0;
					double buyValue = 0.0;
					double sellValue = 0.0;
					for (Order item : matchList) {
						String transactionType = item.transactionType;
						String orderType = item.orderType;
						int qty = Integer.parseInt(item.quantity);
						
						if (transactionType.equals("BUY")) {
							if (orderType.equals("MARKET")) {
							//	double price = Double.parseDouble(item.averagePrice);
							double price = getPrice(item);
							//	positionValue = positionValue - (qty * price);
							buyValue = buyValue + (qty * price);
							} else {
								if (orderType.equals("LIMIT")) {
							//		double price = Double.parseDouble(item.price);
									double price = getPrice(item);
									//positionValue = positionValue - (qty * price);
									buyValue = buyValue + (qty * price);
								}
							}
							totalQty = totalQty + qty;
						}
						
						if (transactionType.equals("SELL")) {
							if (orderType.equals("MARKET")) {
							//	double price = Double.parseDouble(item.averagePrice);
								double price = getPrice(item);
								//positionValue = positionValue + (qty * price);
								sellValue = sellValue + (qty * price);
							} else {
								if (orderType.equals("LIMIT")) {
								//	double price = Double.parseDouble(item.price);
									double price = getPrice(item);
									//positionValue = positionValue + (qty * price);
									sellValue = sellValue + (qty * price);
								}
							}
							totalQty = totalQty - qty;
						}
						//NEW_ORDERBOOK_LOGGER.info("Inside for loop");
						//int qty = Integer.parseInt(item.quantity);
						//NEW_ORDERBOOK_LOGGER.info("qty = " +qty);
						//totalQty = totalQty - qty;
						//NEW_ORDERBOOK_LOGGER.info("totalQty = " + totalQty);
						double price = Double.parseDouble(item.averagePrice);
						NEW_ORDERBOOK_LOGGER.info("price = " +price);
						//positionValue = positionValue + (qty * price);
						NEW_ORDERBOOK_LOGGER.info("positionValue = " +positionValue);
						NEW_ORDERBOOK_LOGGER.info("condition (totalQty <= 0) = " +(totalQty <= 0));
						if (totalQty <= 0) {
							break;
						}
						
						/*if (totalQty >= qty) {
							double price = Double.parseDouble(item.averagePrice);
							positionValue = positionValue + (qty * price);
							totalQty = totalQty - qty;
							break;
						} else {
							totalQty = totalQty - qty;
							double price = Double.parseDouble(item.averagePrice);
							positionValue = positionValue + (qty * price);
						}*/
						
						
					}
					NEW_ORDERBOOK_LOGGER.info("Outside of for loop");
				//	average = positionValue/Math.abs(netQuantity);
					positionValue = sellValue - buyValue;
					average = positionValue/Math.abs(netQuantity);
					NEW_ORDERBOOK_LOGGER.info("average ="+average);
					pl = (average - ltp) * Math.abs(netQuantity);
					NEW_ORDERBOOK_LOGGER.info("pl=" +pl);
					code = "success";
					message = "success";
					profitLoss ="" + pl;
					NEW_ORDERBOOK_LOGGER.info(entry.tradingSymbol + " " + entry.netQuantity);
					NEW_ORDERBOOK_LOGGER.info("profitLoss = " + profitLoss);
			} else {
				code = "error";
				message = (String) response[1];
				profitLoss = "0.0";
			}
		}
		return new Object[] { code,message,profitLoss};
		
	}
	
	private static Object[] getAllBookOrders(String symbol,String opeation) {
		
		String code = "";
		String message = "";
		
		Object[] response = new Object[3];
		
		KiteConnect kiteConnect = KiteStore.kiteconnect;
		ArrayList<Order> matchingOrderList = new ArrayList<Order>();
		
		if (kiteConnect == null) {
			code = "error";
			message ="kiteConnect is null";
			matchingOrderList = null;
			
			response[0] = code;
			response[1] = message;
			response[2] = matchingOrderList;
			return response;
		}
		
		try {
			Order orderBook = kiteConnect.getOrders();
			List<Order> orderList = orderBook.orders;
			int size = orderList.size();
			
			if (size > 0) {
				for (int i = size-1; i>0 ;i--) {
					Order item = orderList.get(i);
					String status = item.status;
					String tradingSymbol = item.tradingSymbol;
					String transactionType = item.transactionType;
					String product = item.product;
					
					if  ((status.equals("COMPLETE")) && (tradingSymbol.equals(symbol)) && (transactionType.equals(opeation)) && (product.equals("MIS"))) {
						matchingOrderList.add(item);
					}
				}
			} else {
				code = "error";
				message ="order book is empty";
				matchingOrderList = null;
				
				response[0] = code;
				response[1] = message;
				response[2] = matchingOrderList;
				return response;
				
			}
		} catch (JSONException e) {
			code = "error";
			message ="JSONException in NewOrderBook.getAllBookOrders()";
			matchingOrderList = null;
			
			response[0] = code;
			response[1] = message;
			response[2] = matchingOrderList;
			return response;
		}
		catch (KiteException e) {
			code = "error";
			message ="KiteException in NewOrderBook.getAllBookOrders()";
			matchingOrderList = null;
			
			response[0] = code;
			response[1] = message;
			response[2] = matchingOrderList;
			return response;
		}
		
		if (matchingOrderList != null) {
			code = "success";
			message ="success";
		}
		
		response[0] = code;
		response[1] = message;
		response[2] = matchingOrderList;
		return response;
		
	}
	
private static Object[] getAllBookOrders(Position entry) {
		
		String code = "";
		String message = "";
		
		Object[] response = new Object[3];
		
		KiteConnect kiteConnect = KiteStore.kiteconnect;
		ArrayList<Order> matchingOrderList = new ArrayList<Order>();
		
		if (kiteConnect == null) {
			code = "error";
			message ="kiteConnect is null";
			matchingOrderList = null;
			
			response[0] = code;
			response[1] = message;
			response[2] = matchingOrderList;
			return response;
		}
		
		try {
			Order orderBook = kiteConnect.getOrders();
			List<Order> orderList = orderBook.orders;
			int size = orderList.size();
			
			if (size > 0) {
				for (int i = size-1; i>0 ;i--) {
					Order item = orderList.get(i);
					String status = item.status;
					String tradingSymbol = item.tradingSymbol;
					//String transactionType = item.transactionType;
					String product = item.product;
					
					if  ((status.equals("COMPLETE")) && (tradingSymbol.equals(entry.tradingSymbol)) && (product.equals(entry.product))) {
						matchingOrderList.add(item);
					}
				}
			} else {
				code = "error";
				message ="order book is empty";
				matchingOrderList = null;
				
				response[0] = code;
				response[1] = message;
				response[2] = matchingOrderList;
				return response;
				
			}
		} catch (JSONException e) {
			code = "error";
			message ="JSONException in NewOrderBook.getAllBookOrders()";
			matchingOrderList = null;
			
			response[0] = code;
			response[1] = message;
			response[2] = matchingOrderList;
			return response;
		}
		catch (KiteException e) {
			code = "error";
			message ="KiteException in NewOrderBook.getAllBookOrders()";
			matchingOrderList = null;
			
			response[0] = code;
			response[1] = message;
			response[2] = matchingOrderList;
			return response;
		}
		
		if (matchingOrderList != null) {
			code = "success";
			message ="success";
		}
		
		response[0] = code;
		response[1] = message;
		response[2] = matchingOrderList;
		return response;
		
	}
	
	private static ArrayList<Order> sortOrderList(ArrayList<Order> orderList) {
		int size = orderList.size();
		
		for (int i=0;i<size;i++) {
			for (int j=i+1;j<size;j++) {
				Order one = orderList.get(i);
				Order two = orderList.get(j);
				String oneOrderId = one.orderId;
				String twoOrderId = two.orderId;
				if (oneOrderId.compareTo(twoOrderId) < 0) {
					Order temp = one;
					one = two;
					two = temp;
					orderList.set(i, one);
					orderList.set(j, two);
				}
			}
		}
		return orderList;
	}
	
	public static double getPrice(Order order) {
		double ans = 0.0;
		if (Double.parseDouble(order.averagePrice) == 0.0) {
			ans = Double.parseDouble(order.price);
		} else {
			ans = Double.parseDouble(order.averagePrice);
		}
		return ans;
	}

}
