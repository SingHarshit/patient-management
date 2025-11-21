import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PatientIntegrationTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void testPatientIntegrationTestWithValidToken() {

        String loginPayload = """
            {
              "email": "testuser@test.com",
              "password": "test123"
            }
            """;

        // ✔ Extract token as plain string
        String token = given()
                .log().all()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .asString();

        System.out.println("Token = " + token);

        // ✔ Token should not be empty
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // 🔥 Call patient service
        given()
                .log().all()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients")
                .then()
                .log().all()
                .statusCode(200)
                .body(notNullValue());   // safe assertion
    }
}
