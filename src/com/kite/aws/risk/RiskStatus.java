package com.kite.aws.risk;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class RiskStatus {
	
	private String status;
	private String message;
	private String m2m;
	private String profitTarget;
	private String stopLoss;
	
	
	public RiskStatus(String status, String message, String m2m, String profitTarget, String stopLoss) {
		super();
		this.status = status;
		this.message = message;
		this.m2m = m2m;
		this.profitTarget = profitTarget;
		this.stopLoss = stopLoss;
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
	public String getM2m() {
		return m2m + " @ " + getTime();
	}
	public void setM2m(String m2m) {
		this.m2m = m2m;
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
	
	private String getTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
		cal.add(Calendar.DATE, 0);

		//System.out.println(dateFormat.format( cal.getTime()));
		return dateFormat.format(cal.getTime());
	}
	
	

}
