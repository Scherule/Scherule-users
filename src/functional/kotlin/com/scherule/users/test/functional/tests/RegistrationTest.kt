package com.scherule.users.test.functional.tests

import com.scherule.users.domain.commands.RegistrationCommand
import com.scherule.users.domain.models.UserCodeType
import com.scherule.users.domain.repositories.UserCodesRepository
import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.domain.services.UserService
import com.scherule.users.test.functional.AbstractFunctionalTest
import io.restassured.RestAssured
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.assertj.core.api.Assertions.*


class RegistrationTest : AbstractFunctionalTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var codesRepository: UserCodesRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userService: UserService

    @Before
    fun setUp() {
        codesRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun postRegister() {
        RestAssured.given()
                .spec(jsonContentType())
                .body("""
                    {
                        "email": "hello.kitty@dummy.com",
                        "password": "peterPan123"
                    }
                """.trim())
                .post("/api/registration")
                .then()
                .statusCode(200)

        assertThat(userRepository.findByEmail("hello.kitty@dummy.com")).isNotNull()
    }

    @Test
    fun postRegistrationConfirmation() {
        val registrationCode = createUserAndGetConfirmationCode()

        RestAssured.given()
                .spec(jsonContentType())
                .body("""
                    {
                        "code": "$registrationCode"
                    }
                """.trim())
                .post("/api/registration/confirmation")
                .then()
                .statusCode(200)
    }

    @Test
    fun isBadRequestForMalformedConfirmationCode() {
        val registrationCode = createUserAndGetConfirmationCode()

        RestAssured.given()
                .spec(jsonContentType())
                .body("""
                    {
                        "code": "${registrationCode}x"
                    }
                """.trim())
                .post("/api/registration/confirmation")
                .then()
                .statusCode(400)
    }

    @Test
    fun isBadRequestIfSameConfirmationCodeUsedTwice() {
        val registrationCode = createUserAndGetConfirmationCode()

        val confirmationRequest = RestAssured.given()
                .spec(jsonContentType())
                .body("""
                    {
                        "code": "$registrationCode"
                    }
                """.trim())

        confirmationRequest
                .post("/api/registration/confirmation")
        confirmationRequest
                .post("/api/registration/confirmation")
                .then()
                .statusCode(400)
    }

    private fun createUserAndGetConfirmationCode(): String {
        val user = userService.registerUser(
                RegistrationCommand(
                        email = "someone@anyone.com",
                        password = "qwerty"
                )
        )

        return codesRepository.findByUserAndType(user, UserCodeType.REGISTRATION_CONFIRMATION).map { it.code }.get()
    }

}