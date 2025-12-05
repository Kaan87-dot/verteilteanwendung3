package de.berlin.htw.boundary.dto;

import java.util.List;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
public class Orders {

    private List<Order> orders;
    
    private Float balance;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

	public Float getBalance() {
		return balance;
	}

	public void setBalance(Float balance) {
		this.balance = balance;
	}

}
