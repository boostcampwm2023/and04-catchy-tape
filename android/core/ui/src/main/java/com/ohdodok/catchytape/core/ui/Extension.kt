package com.ohdodok.catchytape.core.ui

import com.ohdodok.catchytape.core.domain.model.CtErrorType


fun CtErrorType.toMessageId(): Int {
    return when (this) {
        CtErrorType.DUPLICATED_NICKNAME -> R.string.error_message_duplicated_nickname
        CtErrorType.WRONG_TOKEN -> R.string.error_message_wrong_token
        CtErrorType.SERVER -> R.string.error_message_server
        CtErrorType.CONNECTION -> R.string.error_message_connection
        CtErrorType.IO -> R.string.error_message_io
        CtErrorType.SSL_HAND_SHAKE -> R.string.error_message_ssl_hand_shake
        CtErrorType.UN_KNOWN -> R.string.error_message_un_known
        CtErrorType.UN_AUTHORIZED -> R.string.error_message_un_authorized
        CtErrorType.NOT_EXIST_PLAYLIST_ON_USER -> R.string.error_message_not_exist_playlist_on_user
        CtErrorType.NOT_EXIST_MUSIC -> R.string.error_message_not_exist_music
        CtErrorType.ALREADY_ADDED -> R.string.error_message_already_added
        CtErrorType.INVALID_INPUT_VALUE -> R.string.error_message_invalid_input_value
        CtErrorType.NOT_EXIST_USER -> R.string.error_message_not_exist_user
        CtErrorType.ALREADY_EXIST_EMAIL -> R.string.error_message_already_exist_email
        CtErrorType.NOT_EXIST_GENRE -> R.string.error_message_not_exist_genre
        CtErrorType.EXPIRED_TOKEN -> R.string.error_message_expired_token
        CtErrorType.SERVICE -> R.string.error_message_service
    }

}
