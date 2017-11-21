package com.scherule.users.test.functional.layers.endpoints

import io.restassured.RestAssured
import org.hamcrest.Matchers
import org.junit.Test


class UiRedirectionToIndexTest : EndpointTest() {

    @Test
    fun canGetSwagger() {
        RestAssured.given()
                .get("/login")
                .then()
                .statusCode(200)
                .content(Matchers.containsString("<my-app>Loading...</my-app>"))
    }

}