package com.scherule.users.test.functional

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.ComponentScan

@TestConfiguration
@ComponentScan("com.scherule.users.test.functional.managers")
class FunctionalTestContext