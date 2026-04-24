package com.learner.language.domain.auth

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.LanguageException

class OAuthAuthenticationException(
    val oauthErrorCode: ErrorCode,
    message: String,
) : LanguageException(oauthErrorCode, message)
