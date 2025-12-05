package de.berlin.htw;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import jakarta.inject.Inject;

@QuarkusTest
class BasketResourceTest {

    @Inject
    protected RedisDataSource redisDS;
    
    @BeforeEach
    void clearBaskets() {
        // Clear all baskets before each test
        redisDS.key().del("basket:user:2", "basket:user:3", "basket:user:4");
    }
    
    @Test
    void testGetBasket() {
        given()
            .log().all()
            .when().header("X-User-Id", "2")
            .get("/basket")
            .then()
            .log().all()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    void testAddItem() {
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
            .statusCode(201)
            .contentType(ContentType.JSON);
    }

    @Test
    void testCheckout() {
        // First add an item to the basket
        String validItem = """
            {
                "productName": "Test Product",
                "productId": "1-2-3-4-5-7",
                "count": 1,
                "price": 50.0
            }
            """;
        
        given()
            .when().header("X-User-Id", "4")
            .contentType(ContentType.JSON)
            .body(validItem)
            .post("/basket/1-2-3-4-5-7");
        
        given()
            .log().all()
            .when().header("X-User-Id", "4")
            .post("/basket")
            .then()
            .log().all()
            .statusCode(201)
            .header("Location", "http://localhost:8081/orders");
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
            .statusCode(201); // Now returns 201 because functionality is implemented
    }

}