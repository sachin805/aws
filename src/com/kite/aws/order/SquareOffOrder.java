package com.kite.aws.order;

public class SquareOffOrder {
	private String instrument;
	private String product;
	private String quantity;
	private boolean isSell;

	public SquareOffOrder(String instrument, String product, String quantity,
			boolean isSell) {
		super();
		this.instrument = instrument;
		this.product = product;
		this.quantity = quantity;
		this.isSell = isSell;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public boolean isSell() {
		return isSell;
	}

	public void setSell(boolean isSell) {
		this.isSell = isSell;
	}

	@Override
	public boolean equals(Object obj) {
		
		SquareOffOrder temp = (SquareOffOrder)obj;
		
		if(temp.instrument.equals(this.instrument)){
			if(temp.product.equals(this.product)){
				if(temp.quantity.equals(this.quantity)){
					if(temp.isSell == this.isSell){
						return true;
						
					}
					
				}
				
			}
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		//return super.hashCode();
		if(isSell) {
			return instrument.hashCode()+product.hashCode()+quantity.hashCode()+"true".hashCode();
		} else {
			return instrument.hashCode()+product.hashCode()+quantity.hashCode()+"false".hashCode();
		}
	}
	
	
	
	
}
