package com.ohdodok.catchytape.core.domain.model

import java.io.IOException

class CtException(message: String?, val ctError: CtErrorType) : IOException(message)