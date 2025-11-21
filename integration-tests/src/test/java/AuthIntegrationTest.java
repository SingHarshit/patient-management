import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthIntegrationTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004"; // Gateway
    }

    @Test
    public void shouldReturnOKWithValidToken() {

        String loginPayload = """
            {
              "email": "testuser@test.com",
              "password": "test123"
            }
            """;

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .log().all()
                .statusCode(200)   // ✅ ONLY THIS ASSERTION
                .extract().response();

        String token = response.asString(); // raw text

        System.out.println("Generated token: " + token);

        // Add manual assertions
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }


    @Test
    public void shouldReturnUnauthorizedOnInValidToken() {

        String loginPayload = """
        {
          "email": "invaliduser@test.com",
          "password": "test12356"
        }
        """;

        given()
                .log().all()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .log().all()
                .statusCode(401);  // ✔ expected for invalid user
    }


}
