package com.scherule.users.test.functional.layers.logic.features

import com.scherule.users.test.functional.layers.logic.CucumberTestsRunner
import cucumber.api.CucumberOptions


@CucumberOptions(
        glue = arrayOf("com.scherule.users.test.functional.layers.logic.steps"),
        features = arrayOf("src/functional/resources/features/password.change.feature"),
        format = arrayOf("pretty", "html:target/cucumber")
)
internal class PasswordChangeFeature : CucumberTestsRunner()