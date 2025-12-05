package de.berlin.htw;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class OrderResourceTest {

    @Test
    void testUnauthorized() {
        given()
            .log().all()
            .when().get("/orders")
            .then()
            .log().all()
            .statusCode(401);
    }

    @Test
    void testAuthorization() {
        given()
        	.log().all()
        	.when().header("X-User-Id", "1")
        	.get("/orders")
        	.then()
        	.log().all()
        	.statusCode(415);
    }

}