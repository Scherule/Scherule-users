package com.scherule.users.test.functional.layers.logic

import cucumber.api.junit.Cucumber
import org.joda.time.DateTimeZone
import org.junit.BeforeClass
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
abstract class CucumberTestsRunner {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            DateTimeZone.setDefault(DateTimeZone.UTC)
        }
    }

}