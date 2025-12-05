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

}