package com.scherule.users.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Confirmation code was malformed")
class WrongConfirmationCodeException : Exception() {
}