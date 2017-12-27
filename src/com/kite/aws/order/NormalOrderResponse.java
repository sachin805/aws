package com.kite.aws.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class NormalOrderResponse {
	private String code;
	private String message;
	
	public NormalOrderResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public static void main(String[] args) {
		NormalOrderResponse o = new NormalOrderResponse( "200","Executed both orders successfully !!");
		Gson gson = new GsonBuilder().create();
		System.out.println(gson.toJson(o));
		
		NormalOrderResponse o1 = new NormalOrderResponse( "300","kiteConnect is null in RestOrderExecutor.placeKiteOrder()");
	//	Gson gson = new GsonBuilder().create();
		System.out.println(gson.toJson(o1));
	}
	
	

}
