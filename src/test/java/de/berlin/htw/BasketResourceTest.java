package de.berlin.htw;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

@QuarkusTest
class BasketResourceTest {

    @Inject
    protected RedisDataSource redisDS;
    
    @Test
    void testGetBasket() {
        ValueCommands<String, Integer> countCommands = redisDS.value(Integer.class);
        
        given()
            .log().all()
            .when().header("X-User-Id", "2")
            .get("/basket")
            .then()
            .log().all()
            .statusCode(415);
        
        assertEquals(88, countCommands.get("TODO"));
    }

    @Test
    void testAddItem() {
        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .post("/basket/anyID")
            .then()
            .log().all()
            .statusCode(501);
    }

    @Test
    void testCheckout() {
        given()
            .log().all()
            .when().header("X-User-Id", "4")
            .post("/basket")
            .then()
            .log().all()
            .statusCode(201)
            .header("Location", "http://localhost:8081/hierFehltNoEtwas");
    }

    @Test
    void testValidationProductNameTooLong() {
        String longName = "a".repeat(256); // 256 characters, exceeds max of 255
        String validItem = String.format("""
            {
                "productName": "%s",
                "productId": "1-2-3-4-5-6",
                "count": 1,
                "price": 50.0
            }
            """, longName);
        
        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .body(validItem)
            .post("/basket/1-2-3-4-5-6")
            .then()
            .log().all()
            .statusCode(400);
    }

    @Test
    void testValidationInvalidProductIdFormat() {
        String invalidItem = """
            {
                "productName": "Test Product",
                "productId": "123456",
                "count": 1,
                "price": 50.0
            }
            """;
        
        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .body(invalidItem)
            .post("/basket/123456")
            .then()
            .log().all()
            .statusCode(400);
    }

    @Test
    void testValidationPriceTooLow() {
        String invalidItem = """
            {
                "productName": "Test Product",
                "productId": "1-2-3-4-5-6",
                "count": 1,
                "price": 5.0
            }
            """;
        
        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .body(invalidItem)
            .post("/basket/1-2-3-4-5-6")
            .then()
            .log().all()
            .statusCode(400);
    }

    @Test
    void testValidationPriceTooHigh() {
        String invalidItem = """
            {
                "productName": "Test Product",
                "productId": "1-2-3-4-5-6",
                "count": 1,
                "price": 150.0
            }
            """;
        
        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .body(invalidItem)
            .post("/basket/1-2-3-4-5-6")
            .then()
            .log().all()
            .statusCode(400);
    }

    @Test
    void testValidationValidItem() {
        String validItem = """
            {
                "productName": "Test Product",
                "productId": "1-2-3-4-5-6",
                "count": 1,
                "price": 50.0
            }
            """;
        
        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .body(validItem)
            .post("/basket/1-2-3-4-5-6")
            .then()
            .log().all()
            .statusCode(501); // Still returns 501 because functionality not implemented yet
    }

}