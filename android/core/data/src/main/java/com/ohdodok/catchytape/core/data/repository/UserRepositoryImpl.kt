package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.UserApi
import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun loginWithGoogle(googleToken: String) {
        userApi.login(LoginRequest(googleToken))
    }
}