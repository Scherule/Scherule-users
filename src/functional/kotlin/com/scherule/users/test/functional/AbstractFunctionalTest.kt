package com.scherule.users.test.functional

import com.scherule.users.test.functional.managers.UsersManager
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import org.junit.Before
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.mail.MailSender
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner


@Category(FunctionalTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("dev", "test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
abstract class AbstractFunctionalTest {

    @LocalServerPort
    private var appPort: Int? = null

    @Autowired
    private lateinit var userManager: UsersManager

    @Before
    fun before() {
        reset()
        userManager.resetAll()
    }

    private fun reset() {
        RestAssured.reset()
        RestAssured.port = appPort!!
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    fun jsonContentType() = RequestSpecBuilder()
                .setContentType("application/json")
                .setAccept("application/json")
                .build()

    @Configuration
    @ComponentScan("com.scherule.users.test.functional.managers")
    class TestUtilsContext {

        @MockBean
        private lateinit var mailSender: MailSender

    }

}