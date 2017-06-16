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
}
