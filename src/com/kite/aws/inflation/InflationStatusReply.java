package com.kite.aws.inflation;

public class InflationStatusReply {
	
	private  String orderStatus;
	private  String orderStatusMessage;
	private String quantity;
	private String callSymbol;
	private String putSymbol;
	private String sell;
	private String productType;
	private String triggerSum;
	//private String operator;
	private  String time;
	private String realTimeSum;
	
	
	
	public InflationStatusReply(String orderStatus, String orderStatusMessage, String quantity, String callSymbol,
			String putSymbol, String sell, String productType, String triggerSum, String time, String realTimeSum) {
		super();
		this.orderStatus = orderStatus;
		this.orderStatusMessage = orderStatusMessage;
		this.quantity = quantity;
		this.callSymbol = callSymbol;
		this.putSymbol = putSymbol;
		this.sell = sell;
		this.productType = productType;
		this.triggerSum = triggerSum;
		this.time = time;
		this.realTimeSum = realTimeSum;
	}

	public InflationStatusReply() {
		
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderStatusMessage() {
		return orderStatusMessage;
	}

	public void setOrderStatusMessage(String orderStatusMessage) {
		this.orderStatusMessage = orderStatusMessage;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getCallSymbol() {
		return callSymbol;
	}

	public void setCallSymbol(String callSymbol) {
		this.callSymbol = callSymbol;
	}

	public String getPutSymbol() {
		return putSymbol;
	}

	public void setPutSymbol(String putSymbol) {
		this.putSymbol = putSymbol;
	}

	public String isSell() {
		return sell;
	}

	public void setSell(String sell) {
		this.sell = sell;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTriggerSum() {
		return triggerSum;
	}

	public void setTriggerSum(String triggerSum) {
		this.triggerSum = triggerSum;
	}

	public String getRealTimeSum() {
		return realTimeSum;
	}

	public void setRealTimeSum(String realTimeSum) {
		this.realTimeSum = realTimeSum;
	}

	/*public String getOperator() {
		return operator;
	}*/

	/*public void setOperator(String operator) {
		this.operator = operator;
	}*/
	
	

}
