package com.kite.aws.order.conditional;

public class QuotePOJO {
	
	private String status;
	private String message;
	private String call;
	private String put;
	
	public QuotePOJO(String status, String message, String call, String put) {
		super();
		this.status = status;
		this.message = message;
		this.call = call;
		this.put = put;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCall() {
		return call;
	}

	public void setCall(String call) {
		this.call = call;
	}

	public String getPut() {
		return put;
	}

	public void setPut(String put) {
		this.put = put;
	}
	
	

}
