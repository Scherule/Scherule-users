package com.scherule.users.test.functional.layers.endpoints

import io.restassured.RestAssured
import org.hamcrest.Matchers
import org.junit.Test


class SwaggerTest : EndpointTest() {

    @Test
    fun canGetSwagger() {
        RestAssured.given()
                .get("/swagger-ui.html")
                .then()
                .statusCode(200)
                .content(Matchers.containsString("<title>Swagger UI</title>"))
    }

}