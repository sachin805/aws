package com.kite.aws.login;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LoginResponse {
	private String code;
	private String message;
	
	
	public LoginResponse(String code, String message) {
		super();
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
		LoginResponse o = new LoginResponse("success", "some message");
		Gson gson = new GsonBuilder().create();
		System.out.println(gson.toJson(o));
		
		LoginResponse o1 = new LoginResponse("error", "some message");
		//Gson gson = new GsonBuilder().create();
		System.out.println(gson.toJson(o1));
	}
	
}
