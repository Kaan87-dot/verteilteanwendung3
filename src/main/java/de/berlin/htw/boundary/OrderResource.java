package de.berlin.htw.boundary;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.berlin.htw.boundary.dto.Orders;
import de.berlin.htw.control.OrderController;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@Path("/orders")
public class OrderResource {

    @Context
    UriInfo uri;
    
    @Context
    SecurityContext context;
    
    @Inject
    OrderController order;

    @Inject
    Logger logger;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all completed orders of a users.")
    @APIResponse(responseCode = "200", description = "Retieve all completed orders successfully")
    @APIResponse(responseCode = "401", description = "No or wrong User Id provided as header")
    public Orders getCompletedOrders() {
    	logger.info(context.getUserPrincipal().getName() 
    			+ " is calling " + uri.getAbsolutePath());

        return order.todo();
    }

}