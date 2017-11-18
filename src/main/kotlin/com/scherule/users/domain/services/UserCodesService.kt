package com.scherule.users.domain.services

import com.scherule.users.domain.models.User
import com.scherule.users.domain.models.UserCode
import com.scherule.users.domain.models.UserCodeType
import com.scherule.users.domain.repositories.UserCodesRepository
import com.scherule.users.exceptions.WrongConfirmationCodeException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.SecureRandom
import java.util.*

@Component
class UserCodesService
@Autowired constructor(
        private val secureRandom: SecureRandom,
        private val userCodesRepository: UserCodesRepository
) {

    fun issueUserCode(user: User, userCodeType: UserCodeType): UserCode {
        val userCode = UserCode()
        userCode.user = user
        userCode.type = userCodeType
        userCode.sentToEmail = user.email
        userCode.code = encode(user.id!!)
        userCodesRepository.save(userCode)
        return userCode
    }

    fun consumeUserCode(code: String, userCodeType: UserCodeType, action: (code: UserCode) -> Unit) {
        val decodedCode = decode(code)
        if(decodedCode.contains(":")) {
            val userId: String = decodedCode.substringBefore(":")
            val userCode = userCodesRepository.findByUserAndType(userId, userCodeType)
                    .orElseThrow { WrongConfirmationCodeException() }
            if(userCode.code == code) {
                action(userCode)
                userCodesRepository.delete(userCode)
            }
        } else throw MalformedUserCodeException()
    }

    private fun encode(data: String): String {
        val secret = generateRandomHexToken(16)
        val toEncode = "$data:$secret";
        return Base64.getEncoder().encodeToString(toEncode.toByteArray());
    }

    private fun decode(code: String): String {
        try {
            val decoded = Base64.getDecoder().decode(code)
            return decoded.toString(Charset.forName("UTF-8"))
        } catch (e: Exception) {
            throw MalformedUserCodeException(e)
        }
    }

    private fun generateRandomHexToken(byteLength: Int): String {
        val token = ByteArray(byteLength)
        secureRandom.nextBytes(token)
        return BigInteger(1, token).toString(16)
    }

    class MalformedUserCodeException(e: Exception = IllegalArgumentException())
        : RuntimeException("User code is not readable.", e)

}