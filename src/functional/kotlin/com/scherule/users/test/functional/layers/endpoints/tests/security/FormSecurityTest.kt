package com.scherule.users.test.functional.layers.endpoints.tests.security

import com.scherule.users.test.functional.layers.endpoints.EndpointTest
import com.scherule.users.test.functional.managers.UsersManager
import io.restassured.RestAssured
import io.restassured.authentication.FormAuthConfig
import org.hamcrest.Matchers
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class FormSecurityTest : EndpointTest() {

    @Autowired
    private lateinit var userManager: UsersManager

    @Test
    fun canAccess() {
        val dummyUser = userManager.createDummyUser()
        RestAssured.given()
                .auth().form(dummyUser.email, "secret", FormAuthConfig("api/login", "username", "password"))
                .get("/api/users/me")
                .then()
                .statusCode(200)
                .body("username", Matchers.equalTo("someone@anyone.com"))
    }

    @Test
    fun cannotAccessIfInvalidPasswordUsed() {
        val dummyUser = userManager.createDummyUser()
        RestAssured.given()
                .auth().form(dummyUser.email, "secret", FormAuthConfig("api/login", "username", "invalid"))
                .get("/api/users/me")
                .then()
                .statusCode(401)
    }

    @Test
    fun cannotAccessIfMissingUser() {
        val dummyUser = userManager.createDummyUser()
        RestAssured.given()
                .auth().form(dummyUser.email, "secret", FormAuthConfig("api/login", "missing", "password"))
                .get("/api/users/me")
                .then()
                .statusCode(401)
    }

    @Test
    fun cannotAccessIfNoAuthenticationPresent() {
        RestAssured.given()
                .auth().none()
                .get("/api/users/me")
                .then()
                .statusCode(401)
    }

}