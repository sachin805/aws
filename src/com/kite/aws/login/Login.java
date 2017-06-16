package com.kite.aws.login;

import org.json.JSONException;

import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.UserModel;
import com.kite.aws.util.GlobalConstants;
public class Login {
	
	public static String accessToken;
	
	public static KiteConnect kiteconnect;
	
	public static UserModel userModel;
	
	public static double profitTrigger;
	
	public static double lossTrigger;
	
	

	public static String[] loginUser(String kiteURL) {

		String requestToken = extractRequestToken(kiteURL);
		KiteConnect kiteconnect = new KiteConnect(GlobalConstants.API_KEY);
		UserModel userModel = null;
		String code = "success";
		String message = "";

		try {
			userModel = kiteconnect.requestAccessToken(requestToken,
					GlobalConstants.API_SECRET);
		} catch (JSONException e) {
			code = "error";
			message = "JSONException while genrating access token";
		} catch (KiteException e) {
			code = "error";
			message = "KiteException while genrating access token";
		}
		if (userModel != null) {
			String accessToken = userModel.accessToken;
			
			if ((accessToken != null) && (accessToken.trim().length() > 0)) {
				message = "Generated access token successfuly";
				Login.accessToken = accessToken;
				Login.kiteconnect = kiteconnect;
				Login.kiteconnect.setAccessToken(accessToken);
				Login.kiteconnect.setPublicToken(userModel.publicToken);
				Login.userModel = userModel;
			//	InstrumentStore.getInstance(kiteconnect);
			}
		} else {
			code = "error";
			message = "userModel is null in Login.java";
		}

		return new String[] { code, message };
	}

	private static String extractRequestToken(String kiteURL) {
		String requestToken = null;
		String[] tokenArray = kiteURL.split("request_token=");
		requestToken = tokenArray[1];
		return requestToken;
	}
	
	
	public static Object[] login(String requestToken) {

	//	String requestToken = extractRequestToken(kiteURL);
		KiteConnect kiteconnect = new KiteConnect(GlobalConstants.API_KEY);
		UserModel userModel = null;
		String code = "success";
		String message = "";

		try {
			userModel = kiteconnect.requestAccessToken(requestToken,
					GlobalConstants.API_SECRET);
		} catch (JSONException e) {
			code = "error";
			message = "JSONException while genrating access token";
		} catch (KiteException e) {
			code = "error";
			message = "KiteException while genrating access token";
		}
		if (userModel != null) {
			String accessToken = userModel.accessToken;
			
			if ((accessToken != null) && (accessToken.trim().length() > 0)) {
				message = "Generated access token successfuly";
				/*Login.accessToken = accessToken;
				Login.kiteconnect = kiteconnect;
				Login.kiteconnect.setAccessToken(accessToken);
				Login.kiteconnect.setPublicToken(userModel.publicToken);
				Login.userModel = userModel;*/
				
				kiteconnect.setAccessToken(accessToken);
				kiteconnect.setPublicToken(userModel.publicToken);
				kiteconnect.setUserId(GlobalConstants.USER_ID);
				
				//now REST context
				KiteStore.kiteconnect = kiteconnect;
				KiteStore.userModel = userModel;
				KiteStore.accessToken = accessToken;
				KiteStore.publicToken = userModel.publicToken;
				
			//	InstrumentStore.getInstance(kiteconnect);
			}
		} else {
			code = "error";
			message = "userModel is null in Login.java";
		}

		return new Object[] { code, message, kiteconnect};
	}

	

}
