package com.kite.aws.risk;

public class RiskResponse {
	private String operation;
	private String status;
	private String message;
	
	public RiskResponse(String operation, String status, String message) {
		this.operation = operation;
		this.status = status;
		this.message = message;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
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
	
	
	
	

}
