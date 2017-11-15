package com.scherule.users.test.functional.tests.security


import com.scherule.users.domain.models.User
import com.scherule.users.test.functional.AbstractFunctionalTest
import com.scherule.users.test.functional.managers.UsersManager
import io.restassured.RestAssured.given
import io.restassured.http.Header
import io.restassured.response.Response
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


class OAuthSecurityTest : AbstractFunctionalTest() {

    @Autowired
    private lateinit var usersManager: UsersManager


    private lateinit var dummyUser: User

    @Before
    fun setup() {
        dummyUser = usersManager.createDummyUser()
    }

    @Test
    @Throws(Exception::class)
    fun cannotGetTokenIfWrongPasswordUsed() {
        issueTokenRequest(dummyUser.email, "invalid").then().statusCode(400)
    }

    @Test
    @Throws(Exception::class)
    fun cannotGetTokenIfNotExistingUserUsed() {
        issueTokenRequest("invalid", "anything").then().statusCode(401)
    }

    @Test
    @Throws(Exception::class)
    fun canUseTokenToGetSecuredUserDetails() {
        val token = getToken(issueTokenRequest(dummyUser.email, "secret"))
        given()
                .header("Authorization", "Bearer $token")
                .get("/api/users/me")
                .then()
                .statusCode(200)
    }

    @Test
    @Throws(Exception::class)
    fun cannotUseTemperedWithToken() {
        val token = getToken(issueTokenRequest(dummyUser.email, "secret"))
        given()
                .header(Header("Authorization", "Bearer x${token}x"))
                .get("/api/users/me")
                .then()
                .statusCode(401)
    }

    @Test
    @Throws(Exception::class)
    fun unauthorizedIfNoTokenPresent() {
        given()
                .get("/api/users/me")
                .then()
                .statusCode(401)
    }

    @Throws(Exception::class)
    private fun issueTokenRequest(username: String, password: String): Response {
        val params = HashMap<String, String>()
        params.put("grant_type", "password")
        params.put("client_id", VALID_CLIENT_ID)
        params.put("username", username)
        params.put("password", password)

        return given()
                .contentType("application/x-www-form-urlencoded")
                .params(params)
                .auth()
                .preemptive()
                .basic(VALID_CLIENT_ID, VALID_CLIENT_SECRET)
                .post("/oauth/token")
    }

    private fun getToken(result: Response): String {
        result.then().statusCode(200)
        return result.jsonPath().getString("access_token")
    }

    companion object {
        private val VALID_CLIENT_ID = "expenses-tracker-service"
        private val VALID_CLIENT_SECRET = "expenses-tracker-service-secret"
    }

}