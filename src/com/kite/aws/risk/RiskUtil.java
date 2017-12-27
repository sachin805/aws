package com.kite.aws.risk;

import java.util.List;

import org.json.JSONException;

import com.kite.aws.login.KiteStore;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Position;

public class RiskUtil {
	
	public static String[] setAutoTargets() {
		String[] reply = new String[2];
		
		KiteConnect kiteConnect = KiteStore.kiteconnect;
		
		if (kiteConnect == null) {
			reply[0] = "error";
			reply[1] = "kiteConnect is null in RiskUtil.setAutoTargets()";
			return reply;
		}
		
		Position position = null;
		try {
			position = kiteConnect.getPositions();
		} catch (JSONException e) {
			reply[0] = "error";
			reply[1] = "JSONException in RiskUtil.setAutoTargets()";
			return reply;
			
		} catch (KiteException e) {
			reply[0] = "error";
			reply[1] = "KiteException in RiskUtil.setAutoTargets()";
			return reply;
		}
		
		List<Position> netPositions = position.netPositions;
		int openPositionCount = 0;
		for (Position item : netPositions) {
			int netQuantity = item.netQuantity;
			if (netQuantity != 0) {
				openPositionCount = openPositionCount + 1;
			}
		}
		
		if (openPositionCount == 0) {
			reply[0] = "error";
			reply[1] = "There are no open positions";
			return reply;
		} else {
			double netM2m = 0.0;
			for (Position item : netPositions) {
				netM2m = netM2m + item.m2m;
			}
			int totalQuantity = 0;
			for (Position item : netPositions) {
				int netQuantity = item.netQuantity;
				if (netQuantity != 0) {
					totalQuantity = totalQuantity + Math.abs(netQuantity);
				}
			}
			double realStopLoss = netM2m - (RiskStore.absPointsStopLoss * (totalQuantity/2));
			RiskStore.stopLoss = "" +realStopLoss;
			reply[0] = "success";
			reply[1] = "updated stop loss to " + realStopLoss;
			return reply;
		}
		
	}
	
	public static String[] stopLossLimitCheck(String lossStr) {
		List<Position> netPositions;
		KiteConnect kiteconnect = null;
		kiteconnect = KiteStore.kiteconnect;
		String[] reply = new String[3];
		
		if (kiteconnect == null) {
			reply[0] = "fatal";
			reply[1] = "kiteconnect is null in RiskUtil.stopLossLimitCheck()";
			reply[2] = null;
			return reply;
		}
		
		try {
			netPositions = kiteconnect.getPositions().netPositions;
		} catch (JSONException e) {
			reply[0] = "fatal";
			reply[1] = "JSONException in RiskUtil.stopLossLimitCheck()";
			reply[2] = null;
			return reply;
		} catch (KiteException e) {
			reply[0] = "fatal";
			reply[1] = "KiteException in RiskUtil.stopLossLimitCheck()";
			reply[2] = null;
			return reply;
		}
		
	//	double pnl = 0.0;
		int openPosCount = 0;
		int openQuantity = 0;
		for (Position item : netPositions) {
			int quantity = item.netQuantity;
		//	if ((quantity != 0)  && (item.product.equals("MIS"))){
		// now onwards NRML products are also considered
		// so removing check	
				if ((quantity != 0)){
				openQuantity = openQuantity + Math.abs(item.netQuantity);
				openPosCount++;
				
				/*Object[] tokens = NewOrderBook.getPL(item);
				String code = (String)tokens[0];
				if (code.equals("success")) {
					String plStr = (String) tokens[2];
					pnl = pnl + Double.parseDouble(plStr);
				}*/
			}
		}
		
		if (openPosCount == 0) {
			reply[0] = "error";
			reply[1] = "There are no open positions";
			reply[2] = null;
			return reply;
		}
		
			double lossTrigger = (openQuantity/openPosCount) * RiskStore.absPointsStopLoss;
			lossTrigger = 0.0 - lossTrigger;
			double loss = Double.parseDouble(lossStr);
			
			if (loss < lossTrigger) {
				reply[0] = "error";
				reply[1] = "Please set loss less than : " +lossTrigger;
				reply[2] = lossTrigger + "";
			} else {
				reply[0] = "success";
				reply[1] = "success";
				reply[2] = lossTrigger + "";
			}
			return reply;
	}
	
}
