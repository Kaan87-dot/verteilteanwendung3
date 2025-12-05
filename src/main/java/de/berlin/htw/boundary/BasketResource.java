package de.berlin.htw.boundary;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;

import de.berlin.htw.boundary.dto.Basket;
import de.berlin.htw.boundary.dto.Item;
import de.berlin.htw.boundary.dto.Order;
import de.berlin.htw.control.BasketController;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@Path("/basket")
public class BasketResource {

    @Context
    UriInfo uri;
    
    @Context
    SecurityContext context;
    
    @Inject
    BasketController basket;

    @Inject
    Logger logger;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve the basket with all items.")
    @APIResponse(responseCode = "200", description = "Retieve all items in basket successfully")
    @APIResponse(responseCode = "401", description = "No or wrong User Id provided as header")
    public Basket getBasket() {
    	logger.info(context.getUserPrincipal().getName() 
    			+ " is calling " + uri.getAbsolutePath());
    	
        return basket.todo();
    }

    @DELETE
    @Operation(summary = "Remove all items from basket.")
    @APIResponse(responseCode = "204", description = "Items removed successfully")
    @APIResponse(responseCode = "401", description = "No or wrong User Id provided as header")
    public void clearBasket() {
    	logger.info(context.getUserPrincipal().getName() 
    			+ " is calling " + uri.getAbsolutePath());
    	// no content
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Checkout the basket and complete the order.")
    @APIResponse(responseCode = "201", description = "Checkout successfully",
            headers = @Header(name = "Location", description = "URL to retrive all orders"),
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Order.class)) )
    @APIResponse(responseCode = "401", description = "No or wrong User Id provided as header")
    @APIResponse(responseCode = "404", description = "No product with this ID in the basket")
    public Response checkout() {
    	logger.info(context.getUserPrincipal().getName() 
    			+ " is calling " + uri.getAbsolutePath());
    	// return the url of orders and the created order itself
        return Response
        		.created(uri.getBaseUriBuilder().path("hierFehltNoEtwas").build())
        		.build();
    }

    @POST
    @Path("{productId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add an item to basket.")
    @APIResponse(responseCode = "201", description = "Item added successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Basket.class)) )
    @APIResponse(responseCode = "400", description = "Invalid request message")
    @APIResponse(responseCode = "401", description = "No or wrong User Id provided as header")
    @APIResponse(responseCode = "409", description = "Another product with this ID already exist in the basket")
    public Response addItem(
            @Parameter(description = "ID of the product", required = true) @PathParam("productId") final String productId,
            @Parameter(description = "The item to add in the basket", required = true) final Item item) {
    	logger.info(context.getUserPrincipal().getName() 
    			+ " is calling " + uri.getAbsolutePath());
    	// return basket with remaining balance
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Remove an item from basket.")
    @APIResponse(responseCode = "200", description = "Item removed successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Basket.class)) )
    @APIResponse(responseCode = "401", description = "No or wrong User Id provided as header")
    @APIResponse(responseCode = "404", description = "No product with this ID in the basket")
    public Response removeItem(
            @Parameter(description = "ID of the product", required = true) @PathParam("productId") final String productId) {
    	logger.info(context.getUserPrincipal().getName() 
    			+ " is calling " + uri.getAbsolutePath());
    	// return basket with remaining balance
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @PATCH
    @Path("{productId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Change the number of an item in the basket.")
    @APIResponse(responseCode = "200", description = "Number changed successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Basket.class)) )
    @APIResponse(responseCode = "400", description = "Invalid request message")
    @APIResponse(responseCode = "401", description = "No or wrong User Id provided as header")
    @APIResponse(responseCode = "404", description = "No product with this ID in the basket")
    public Response changeCount(
            @Parameter(description = "ID of the product", required = true) @PathParam("productId") final String productId,
            @Parameter(description = "The number of that product in the basket", required = true) final Item item) {
    	logger.info(context.getUserPrincipal().getName() 
    			+ " is calling " + uri.getAbsolutePath());
    	// return basket with remaining balance
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

}