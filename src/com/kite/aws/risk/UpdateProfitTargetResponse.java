package com.kite.aws.risk;

public class UpdateProfitTargetResponse {
	
	private String profitTarget;
	private String stopLoss;
	private String status;
	private String message;

	public UpdateProfitTargetResponse(String profitTarget, String stopLoss,String status,String message) {
		this.profitTarget = profitTarget;
		this.stopLoss = stopLoss;
		this.message = message;
		this.status = status;
	}

	public String getProfitTarget() {
		return profitTarget;
	}

	public void setProfitTarget(String profitTarget) {
		this.profitTarget = profitTarget;
	}

	public String getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(String stopLoss) {
		this.stopLoss = stopLoss;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
