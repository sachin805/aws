package com.kite.aws.order.conditional;

import java.util.ArrayList;
import java.util.TimerTask;

import com.kite.aws.risk.RiskStore;
import com.rainmatter.models.Order;

public class ConditionalTimer extends TimerTask {
	
	
	@Override
	public void run() {
		ConditionalOrder order = ConditionalOrderStore.order;
		
		if (order == null) {
			ConditionalOrderStore.code = "NA";
			ConditionalOrderStore.message = "No order to execute";
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
			return;
		} 
		String[] callCheck = getExecutionPrice((ArrayList<Order>) callOrderBook[2]);
		if (callCheck[0].equals("true")) {
			// it means call order(SL-M) got executed
			callOrderStatusCheck = true;
		}
		
		String put = order.getPut();
		boolean putOrderStatusCheck = false;
		Object[] putOrderBook = ConditionalUtil.getAllBookOrders(put, order.getPosition(), product, quantity);
		code = (String) putOrderBook[0];
		
		if (code.equals("error")) {
			//String message = (String) putOrderBook[1];
			// now persist it in global variable and return
			ConditionalOrderStore.code = "error";
			ConditionalOrderStore.message = "Error while retriving put SL-M orders";
			return;
		} 
		
		String[] putCheck = getExecutionPrice((ArrayList<Order>) putOrderBook[2]);
		if (putCheck[0].equals("true")) {
			// it means put order(SL-M) got executed
			putOrderStatusCheck = true;
		}
		
		boolean strangleCheck = callOrderStatusCheck && putOrderStatusCheck;
		boolean onlyCall = callOrderStatusCheck && (putOrderStatusCheck == false);
		boolean onlyPut = putOrderStatusCheck && (callOrderStatusCheck == false);
		
		if (strangleCheck) {
			// it means both orders got executed
			// classic short/long strangle case
		}
		
		if (onlyCall) {
				String[] reply = ConditionalUtil.getQuote(call);
				if (reply[0].equals("success")) {
					double callLtp = Double.parseDouble(reply[1]);
					Order callOrder = (Order)callOrderBook[2];
					double executedPrice = Double.parseDouble(callOrder.price);
					double callSL = Double.parseDouble(order.getCallSL());
					
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
				}
		}
		
		if (onlyPut) {
				String[] reply = ConditionalUtil.getQuote(put);
				if (reply[0].equals("success")) {
					double putLtp = Double.parseDouble(reply[1]);
					Order putOrder = (Order)putOrderBook[2];
					double executedPrice = Double.parseDouble(putOrder.price);
					double putSL = Double.parseDouble(order.getPutSL());
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
				}
		}
		
		
	}
	
	private String[] getExecutionPrice(ArrayList<Order> orderList) {
		for (Order item : orderList) {
			if (item.status.equals("COMPLETE")) {
				String pl =  getPL(item) + "";
				return new String[] {"true",pl};
			}
		}
		return new String[] {"false","NA"};
	}
	
	private double getPL(Order order) {
		String position = order.transactionType;
		double quantity = Double.parseDouble(order.quantity);
		double price = Double.parseDouble(order.price);
		double ltp = ConditionalOrderExecutor.getLTP(order.tradingSymbol);
		double pl = 0.0;
		
		if (position.equals("BUY")) {
			pl = (ltp - price) *  quantity;
		} else {
			pl = (price - ltp) * quantity;
		}
		return pl;
	}
	
	private String completeOtherLeg() {
		String[] response = ConditionalUtil.firePendingOrder();
		return response[0];
	}
	
	private void otherLegMessage() {
		ConditionalOrderStore.code = "error";
		ConditionalOrderStore.message = "Error while entering other leg of order";
	}
}
