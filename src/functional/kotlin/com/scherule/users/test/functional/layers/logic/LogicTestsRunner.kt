package com.scherule.users.test.functional.layers.logic

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.joda.time.DateTimeZone
import org.junit.BeforeClass
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
        glue = arrayOf("com.scherule.users.test.functional.layers.logic.steps"),
        features = arrayOf("src/functional/resources/features"),
        format = arrayOf("pretty", "html:target/cucumber")
)
class LogicTestsRunner {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            DateTimeZone.setDefault(DateTimeZone.UTC)
        }
    }

}