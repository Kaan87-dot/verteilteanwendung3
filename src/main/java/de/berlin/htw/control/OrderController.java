package de.berlin.htw.control;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.NotSupportedException;

import de.berlin.htw.boundary.dto.Orders;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@Dependent
public class OrderController {

    public Orders todo() {
    	throw new NotSupportedException("TODO");
    }

}
