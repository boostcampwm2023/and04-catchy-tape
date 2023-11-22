package com.ohdodok.catchytape.core.data.store

import javax.inject.Inject

class TokenStore @Inject constructor() {

    var token: String = ""
        private set

    fun updateToken(newToken: String) {
        token = newToken
    }
}