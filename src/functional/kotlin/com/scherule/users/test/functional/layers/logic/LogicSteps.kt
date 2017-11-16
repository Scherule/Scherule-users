package com.scherule.users.test.functional.layers.logic

import com.scherule.users.domain.models.User
import com.scherule.users.test.functional.FunctionalTest
import com.scherule.users.test.functional.FunctionalTestContext
import cucumber.api.java8.En
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@Category(FunctionalTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("dev", "test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = arrayOf(CucumberContext::class, FunctionalTestContext::class))
abstract class AbstractSteps : En

@TestConfiguration
class CucumberContext {

    @Bean
    @Scope("cucumber-glue")
    fun stepsContext() = StepContext()

}

class StepContext {

    var user: Optional<User> = Optional.empty()

}