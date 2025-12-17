package de.berlin.htw.boundary.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
public class Order {

    @NotNull(message = "Items list is required")
    @Size(max = 10, message = "Basket cannot contain more than 10 items")
    @Valid
    private List<Item> items;
    
    private Float total;
    
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

}
