package com.rainmatter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.kite.aws.util.GlobalConstants;

/**
 * A wrapper for user and session details.
 */
public class UserModel {

    @SerializedName("member_id")
    public String memberId;
    public String[] product;
    @SerializedName("password_reset")
    public boolean passwordReset;
    @SerializedName("user_name")
    public String userName;
    @SerializedName("broker")
    public String broker;
    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("public_token")
    public String publicToken;
    @SerializedName("user_type")
    public String userType;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("login_time")
    public String loginTime;
    public String[] exchange;
    public String[] orderType;
    @SerializedName("email")
    public String email;

    public UserModel parseResponse(JSONObject response) throws JSONException{
    	
    	if (GlobalConstants.API_KEY.equals("oc1g68oja0hpnjcp")) {
    		String[] tokens = getTokens(response);
            UserModel userModel = new UserModel();
            userModel.memberId = "ZERODHA";
            userModel.product = new String[] {"BO","CO","CNC","MIS","NRML"}; 
            userModel.passwordReset = false;
            userModel.userName = "RASHI PATHAK";
            userModel.broker = "ZERODHA";
            userModel.accessToken = tokens[0];
            userModel.publicToken = tokens[1];
            userModel.userType = "investor";
            userModel.userId = "DR2680";
            userModel.loginTime = tokens[2];
            userModel.exchange = new String[] {"NFO"};
            userModel.orderType = new String[] {"LIMIT","MARKET","SL","SL-M"};
            userModel.email = "rashi.pathak@gmail.com";
            return userModel;
    		
    	} else {
    		if (GlobalConstants.API_KEY.equals("3kwyjzh67vq1v3oc")) {
    			 GsonBuilder gsonBuilder = new GsonBuilder();
    		     Gson gson = gsonBuilder.create();
    		     UserModel userModel = gson.fromJson(String.valueOf(response.get("data")), UserModel.class);
    		     userModel = parseArray(userModel, response.getJSONObject("data"));
    		     return userModel;
        	}
    	}
    	return null;
    	
    	
    }
    
    private String[] getTokens(JSONObject response) {
    	String[] tokenArray = new String[3];
    	String reponseStr = response.toString();
    	
    	String[] temp = reponseStr.split("\"access_token\":\"");
    	String accessToken = temp[1].split("\"")[0];
    	tokenArray[0] = accessToken;
    	
    	temp = reponseStr.split("\"public_token\":\"");
    	String publicToken = temp[1].split("\"")[0];
    	tokenArray[1] = publicToken;
    	
    	temp = reponseStr.split("\"login_time\":\"");
    	String loginTime = temp[1].split("\"")[0];
    	tokenArray[2] = loginTime;
    	
    	
    	
    	return tokenArray;
    }

    public UserModel parseArray(UserModel userModel, JSONObject response) throws JSONException{
        JSONArray productArray = response.getJSONArray("product");
        userModel.product  = new String[productArray.length()];
        for(int i = 0; i < productArray.length(); i++) {
            userModel.product[i] = productArray.getString(i);
        }

        JSONArray exchangesArray = response.getJSONArray("exchange");
        userModel.exchange = new String[exchangesArray.length()];
        for (int j = 0; j < exchangesArray.length(); j++){
            userModel.exchange[j] = exchangesArray.getString(j);
        }

        JSONArray orderTypeArray = response.getJSONArray("order_type");
        userModel.orderType = new String[orderTypeArray.length()];
        for(int k = 0; k < orderTypeArray.length(); k++){
            userModel.orderType[k] = orderTypeArray.getString(k);
        }

        return userModel;
    }
}
