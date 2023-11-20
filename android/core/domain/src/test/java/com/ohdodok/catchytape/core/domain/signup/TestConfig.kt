package com.ohdodok.catchytape.core.domain.signup

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.junitxml.JunitXmlReporter

class TestConfig : AbstractProjectConfig() {

    override fun extensions(): List<Extension> = listOf(
        JunitXmlReporter(
            includeContainers = false, // don't write out status for all tests
            useTestPathAsName = true, // use the full test path (ie, includes parent test names)
            outputDir = "test-results/excludeContainers"
        )
    )
}