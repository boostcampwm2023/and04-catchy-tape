package com.ohdodok.catchytape.core.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SampleTest : StringSpec({
    "Strings" {
        "length는 문자열의 길이를 반환해야 한다" {
            "hello".length shouldBe 5
        }
    }
    "중첩 하지 않아도 됨" {
        "hello".length shouldBe 5
    }
})