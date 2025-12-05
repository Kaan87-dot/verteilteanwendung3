package de.berlin.htw.boundary.dto;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
public class Basket extends Order {

    private Float remainingBalance;

	public Float getRemainingBalance() {
		return remainingBalance;
	}

	public void setRemainingBalance(Float remainingBalance) {
		this.remainingBalance = remainingBalance;
	}

}
