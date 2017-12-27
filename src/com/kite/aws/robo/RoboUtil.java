package com.kite.aws.robo;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.json.JSONException;

import com.kite.aws.login.KiteStore;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.IndicesQuote;
import com.rainmatter.models.Position;

public class RoboUtil {
	
	public static final String NSE = "NSE";
	
	public static final String NIFTY_TRADING_SYMBOL = "NIFTY 50";
	
	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Calcutta");
	
	public static NiftyQuote getNiftyQuote() {
		KiteConnect connect = KiteStore.kiteconnect;
		NiftyQuote nifty = null;
		IndicesQuote quote = null;
		double ltp = 0.0;
		String msg = "success";
		String code = "success";
		LocalTime now = LocalTime.now(ZONE_ID);
		
		if (connect != null) {
			try {
				quote = connect.getQuoteIndices(NSE, NIFTY_TRADING_SYMBOL);
				ltp = quote.lastPrice;
				
				nifty = new NiftyQuote(code, msg, ltp,now);
			} catch (JSONException e) {
				code = "exception";
				ltp = 0.0;
				msg = "JSONException in RoboUtil.getNiftyQuote()";
				nifty = new NiftyQuote(code, msg, ltp,now);
			} catch (KiteException e) {
				code = "exception";
				ltp = 0.0;
				msg = "KiteException in RoboUtil.getNiftyQuote()";
				nifty = new NiftyQuote(code, msg, ltp,now);
			}
			
		} else {
			code = "exception";
			ltp = 0.0;
			msg = "KiteConnect is null RoboUtil.getNiftyQuote()";
			nifty = new NiftyQuote(code, msg, ltp,now);
		}
		return nifty;
	}
	
	public static boolean positionCountCheck() {
		boolean check = false;
		
		KiteConnect connect = KiteStore.kiteconnect;
		if (connect != null) {
			try {
				Position position = connect.getPositions();
				List<Position> netPositions = position.netPositions;
				int openCount  = 0;
				for (Position item : netPositions) {
					String symbol = item.tradingSymbol.toLowerCase();
					if (symbol.endsWith("fut")) {
						if(item.netQuantity != 0) {
							openCount++;
							break;
						}
					}
				}
				check = (openCount != 0);
			} catch (JSONException e) {
				check = true;
				//e.printStackTrace();
			} catch (KiteException e) {
				check = true;
				//e.printStackTrace();
			}
		} else {
			check = true;
		}
		return check;
	}
	
	
	

}
