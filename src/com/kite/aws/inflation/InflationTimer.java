package com.kite.aws.inflation;

import static com.kite.aws.inflation.InflationStore.inflationSum;
import static com.kite.aws.inflation.InflationStore.orderStatus;
import static com.kite.aws.inflation.InflationStore.orderStatusMessage;

import java.text.DecimalFormat;
import java.util.TimerTask;

import com.kite.aws.login.KiteStore;
import com.kite.aws.order.OrderExecutor;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Quote;

public class InflationTimer extends TimerTask {

	@Override
	public void run() {
	//	System.out.println("Entry -> InflationTimer");
		InflationOrder order = InflationStore.inflationOrder;

		if (order == null) {
			setStatus("NA", "No order to execute", "0");
	//		System.out.println("Exit -> InflationTimer");
			return;
		}

		KiteConnect connect = KiteStore.kiteconnect;
		if (connect == null) {
			setStatus("error", "KiteConnect is null in InflationTimer.run()", "0");
	//		System.out.println("Exit -> InflationTimer");
			return;
		}

		Quote callQuote = null;
		Quote putQuote = null;

		try {
			callQuote = connect.getQuote("NFO", order.getCallSymbol());
			putQuote = connect.getQuote("NFO", order.getPutSymbol());
		} catch (KiteException e) {
			setStatus("error", "KiteException in InflationTimer while getting quotes", "0");
		//	System.out.println("Exit -> InflationTimer");
			return;
		}

		double sum = callQuote.lastPrice + putQuote.lastPrice;
		DecimalFormat df = new DecimalFormat("#.##");
		sum = Double.valueOf(df.format(sum));
		
		String operator = order.getOperator();
		if (operator.equals("lte")) {
			if (sum <= order.getSum()) {
				placeOrder(order, sum);
			} else {
				setStatus("NO_TRIGGER", "NO_TRIGGER", "" + sum);
			}
		}
		
		if (operator.equals("gte")) {
			if (sum >= order.getSum()) {
				placeOrder(order, sum);
			} else {
				setStatus("NO_TRIGGER", "NO_TRIGGER", "" + sum);
			}
		}

		/*if (sum >= order.getSum()) {
			placeOrder(order, sum);
		} else {
			setStatus("NO_TRIGGER", "NO_TRIGGER", "" + sum);
		}*/

//		System.out.println("Exit -> InflationTimer");
	}

	public static void placeOrder(InflationOrder order, double sum) {
		String[] replyArray = OrderExecutor.placeKiteOrder(order.getQuantity(), order.getCallSymbol(),
				order.getPutSymbol(), order.isSell(), order.getProductType());
		
		if (replyArray[0].equals("200")) {
			setStatus("success", "Executed inflation order at sum", "" + sum);
			InflationStore.inflationOrder = null;
		} else {
			setStatus("error", "Error while executing inflation order !!", "" + sum);
			InflationStore.inflationOrder = null;
		}
		
		if (sum < 0) {
			if (replyArray[0].equals("200")) {
				setStatus("success", "Executed inflation order at market rate","0");
				InflationStore.inflationOrder = null;
			} else {
				setStatus("error", "Error while executing inflation order at market rate!!", "" + "0");
				InflationStore.inflationOrder = null;
			}
		}
	}

	private static void setStatus(String status, String message, String sum) {
		orderStatus = status;
		orderStatusMessage = message;
		inflationSum = sum;
	}

}
