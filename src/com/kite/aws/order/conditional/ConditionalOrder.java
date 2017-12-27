package com.kite.aws.order.conditional;

public class ConditionalOrder {
	private String call;
	private String callEntry;
	private String callSL;
	private String put;
	private String putEntry;
	private String putSL;
	private String quantity;
	private String position;
	private String product;
	private String target;

	public ConditionalOrder(String call, String callEntry, String callSL, String put, String putEntry, String putSL,
			String quantity, String position, String product,String target) {
		super();
		this.call = call.toUpperCase();
		this.callEntry = callEntry;
		this.callSL = callSL;
		this.put = put.toUpperCase();
		this.putEntry = putEntry;
		this.putSL = putSL;
		this.quantity = quantity;
		this.position = position.toUpperCase();
		this.product = product.toUpperCase();
		this.target = target;
	}

	public String getCall() {
		return call;
	}

	public void setCall(String call) {
		this.call = call;
	}

	public String getCallEntry() {
		return callEntry;
	}

	public void setCallEntry(String callEntry) {
		this.callEntry = callEntry;
	}

	public String getCallSL() {
		return callSL;
	}

	public void setCallSL(String callSL) {
		this.callSL = callSL;
	}

	public String getPut() {
		return put;
	}

	public void setPut(String put) {
		this.put = put;
	}

	public String getPutEntry() {
		return putEntry;
	}

	public void setPutEntry(String putEntry) {
		this.putEntry = putEntry;
	}

	public String getPutSL() {
		return putSL;
	}

	public void setPutSL(String putSL) {
		this.putSL = putSL;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	
}
