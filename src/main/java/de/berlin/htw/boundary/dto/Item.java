package de.berlin.htw.boundary.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
public class Item {

    @NotNull(message = "Product name is required")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String productName;
    
    @NotNull(message = "Product ID is required")
    @Pattern(regexp = "\\d+-\\d+-\\d+-\\d+-\\d+-\\d+", message = "Product ID must match format: X-X-X-X-X-X (6 numbers separated by dashes)")
    private String productId;
    
    @NotNull(message = "Count is required")
    @Min(value = 1, message = "Count must be at least 1")
    private Integer count;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "10.0", message = "Price must be at least 10 Euro")
    @DecimalMax(value = "100.0", message = "Price must be at most 100 Euro")
    private Float price;

    public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Integer getCount() {
        return count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }
    
	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

}
