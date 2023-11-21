package com.ohdodok.catchytape.core.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SampleTest : FunSpec({

    test("test sample 1") {
        1 + 2 shouldBe 3
    }

    test("test sample 2") {
        3 + 4 shouldBe 7
    }

    test("test sample 3") {
        3 + 7 shouldBe 10
    }

})