package com.kite.aws.risk;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONException;

import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Position;
import com.kite.aws.order.OrderExecutor;
import com.kite.aws.order.SquareOffOrder;
import  com.kite.aws.login.KiteStore;
import static com.kite.aws.risk.RiskStore.*;

public class RiskTimer extends TimerTask {
	
	@Override
	public void run() {
			System.out.println("Entry -> RiskTimer" );
			
			KiteConnect kiteConnect = KiteStore.kiteconnect;
			if (kiteConnect == null) {
				return;
			}
			Position position = null;
			double pAl = 0.0;
			
			try {
				position = kiteConnect.getPositions();
			} catch (JSONException e) {
				//e.printStackTrace();
				RiskStore.riskTimerStatus = "error";
				riskTimerStatusMessage = "JSONException in RiskTimer.run() while getting positions";
				m2m = ""+pAl;
				return;
			} catch (KiteException e) {
				riskTimerStatus = "error";
				riskTimerStatusMessage = "KiteException in RiskTimer.run() while getting positions";
				m2m = ""+pAl;
				return;
			}
			
			
			List<Position> netPositions = position.netPositions;
			int openPositionCount = 0;
			for (Position item : netPositions) {
				int netQuantity = item.netQuantity;
				if (netQuantity != 0) {
					openPositionCount = openPositionCount + 1;
				}
			}
			
			for (Position item : netPositions) {
				//netM2m = netM2m + item.m2m;
				pAl = pAl + item.pnl;
			}
			DecimalFormat df = new DecimalFormat("#.##");
			pAl = Double.valueOf(df.format(pAl));
			
			if (openPositionCount == 0) {
				riskTimerStatus = "NO_TRIGGER";
 				m2m = ""+pAl;
				return;
			} 
			
			// now trigger orders
						if (profitLossTriggerCheck(pAl,netPositions)) {
							List<SquareOffOrder> squareOffList = new ArrayList<SquareOffOrder>();
							for (Position item : netPositions) {
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
								String[] reply = OrderExecutor.placeSignleOrder(order.getInstrument(), order.getProduct(), order.getQuantity(), order.isSell());
								/*try {
									Thread.sleep(200);
								} catch(InterruptedException e) {
									riskTimerStatus = "error";
									riskTimerStatusMessage = "InterruptedException in RiskTimer.run() while Thread.sleep(200)";
									m2m = ""+netM2m;
									return;
								}*/
								if (reply[0].equals("error")) {
									// we should notify main process that something went wrong
									// and scheduler must be stopped immediately 
									riskTimerStatus = "error";
									riskTimerStatusMessage = "Something went wrong while executing order in RiskTimer.run()";
									m2m = ""+pAl;
									return;
								} else {
									successCount++;
								}
							}
							
							if (squareOffList.size() == successCount) {
								riskTimerStatus = "complete";
								riskTimerStatusMessage = "Squared off all open positions";
								m2m = ""+pAl;
								return;
							}
							
						} else {
							riskTimerStatus = "NO_TRIGGER";
							//riskTimerStatusMessage = ""+netM2m;
							m2m = ""+pAl;
						}
			
		System.out.println("Exit -> RiskTimer" );
	}
	
	
private boolean profitLossTriggerCheck(double netM2m,List<Position> netPositions) {
		
		boolean check = false;
			String targetStr = profitTarget;
			//String lossStr = stopLoss;
			if ((targetStr!=null)) {
				double profit =  Double.valueOf(targetStr);
				//double loss =  Double.valueOf(lossStr);
			
				if (netM2m < MAX_LOSS) {
					check = true;
				}
				
				if (netM2m > profit) {
					check = true;
				}
				
				double realStopLoss = netM2m - (RiskStore.absPointsStopLoss * (calculateTottalQuantity(netPositions)/2));
				stopLoss = "" + realStopLoss;
				if (netM2m < realStopLoss) {
					check = true;
				}
			} 
		return check;
	}


private int calculateTottalQuantity(List<Position> netPositions){
	int totalQuantity = 0;
	for (Position item : netPositions) {
		int netQuantity = item.netQuantity;
		if (netQuantity != 0) {
			totalQuantity = totalQuantity + Math.abs(netQuantity);
		}
	}
	return totalQuantity;
}

}
