package com.ohdodok.catchytape.core.domain.model

import java.io.IOException

class CtException(message: String, val cTError: CtErrorType) : IOException(message)