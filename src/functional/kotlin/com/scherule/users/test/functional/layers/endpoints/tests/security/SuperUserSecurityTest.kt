package com.scherule.users.test.functional.layers.endpoints.tests.security

import com.scherule.users.test.functional.layers.endpoints.EndpointTest
import io.restassured.RestAssured
import io.restassured.authentication.FormAuthConfig
import org.hamcrest.Matchers
import org.junit.Test


class SuperUserSecurityTest : EndpointTest() {

    @Test
    fun superUserCanCreateUser() {
        RestAssured.given()
                .auth().form("master", "Secret123!", FormAuthConfig("api/login", "username", "password"))
                .post("/api/users")
                .then()
                .statusCode(200)
                .body("username", Matchers.equalTo("someone@anyone.com"))
    }


}