package com.kite.aws.robo;

import java.time.LocalTime;
import java.time.ZoneId;

public class RoboOrder {
	
	private double nifty;
	private LocalTime executionTime;
	private boolean isLong; // means up direction only. It can be long call or short put.
	private String symbol;
	private String qty;
	private boolean isFourPoints;
	private LocalTime twoMins;
	private boolean twoMinsCheck;
	
	private static final ZoneId ZONE_ID = ZoneId.of("Asia/Calcutta");
	
	
	
	public RoboOrder(double nifty, LocalTime executionTime, boolean isLong, String symbol, String qty,boolean isFourPoints) {
		super();
		this.nifty = nifty;
		this.executionTime = executionTime;
		this.isLong = isLong;
		this.symbol = symbol;
		this.qty = qty;
		this.isFourPoints = isFourPoints;
		this.twoMins =  LocalTime.now(ZONE_ID).plusSeconds(120l);
		this.twoMinsCheck = false;
	}

	public double getNifty() {
		return nifty;
	}

	public void setNifty(double nifty) {
		this.nifty = nifty;
	}

	public LocalTime getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(LocalTime executionTime) {
		this.executionTime = executionTime;
	}

	public boolean isLong() {
		return isLong;
	}

	public void setLong(boolean isLong) {
		this.isLong = isLong;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public boolean isFourPoints() {
		return isFourPoints;
	}

	public void setFourPoints(boolean isFourPoints) {
		this.isFourPoints = isFourPoints;
	}

	public LocalTime getTwoMins() {
		return twoMins;
	}

	public void setTwoMins(LocalTime twoMins) {
		this.twoMins = twoMins;
	}

	public boolean isTwoMinsCheck() {
		return twoMinsCheck;
	}

	public void setTwoMinsCheck(boolean twoMinsCheck) {
		this.twoMinsCheck = twoMinsCheck;
	}
	
}
