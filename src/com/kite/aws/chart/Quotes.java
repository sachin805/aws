package com.kite.aws.chart;

import java.text.DecimalFormat;

import org.json.JSONException;

import com.kite.aws.login.KiteStore;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Quote;

public class Quotes {
	
	public static final String EXCHANGE = "NFO";
	public static final DecimalFormat df = new DecimalFormat("#.##");
	
	public static String[] someMethod(String tradingSymbol) {
		String[] response = new String[2];
		String code = "";
		String price = "";
		
		KiteConnect connect= KiteStore.kiteconnect;
		
		if (connect == null) {
			code = "error";
			price = "0.00";
			response[0] = code;
			response[1] = price;
			return response;
		} else {
			try {
				Quote quote = connect.getQuote(EXCHANGE, tradingSymbol);
				double lastPrice = quote.lastPrice;
				lastPrice = Double.valueOf(df.format(lastPrice));
				code = "success";
				price = lastPrice + "";
				response[0] = code;
				response[1] = price;
			} catch (JSONException e) {
				//e.printStackTrace();
				code = "error";
				price = "0.00";
				response[0] = code;
				response[1] = price;
				return response;
			} catch (KiteException e) {
				//e.printStackTrace();
				code = "error";
				price = "0.00";
				response[0] = code;
				response[1] = price;
				return response;
			}
		}
		return response;
	}

}
