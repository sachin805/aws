package com.kite.aws.robo;

import java.time.LocalTime;

public class NiftyQuote {
	private String code;
	private String message;
	private double ltp;
	private LocalTime now;
	
	public NiftyQuote(String code, String message, double ltp,LocalTime now) {
		super();
		this.code = code;
		this.message = message;
		this.ltp = ltp;
		this.now = now;
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

	public double getLtp() {
		return ltp;
	}

	public void setLtp(double ltp) {
		this.ltp = ltp;
	}

	public LocalTime getNow() {
		return now;
	}

	public void setNow(LocalTime now) {
		this.now = now;
	}
}
