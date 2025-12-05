package de.berlin.htw.boundary.dto;

import java.util.List;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
public class Order {

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
