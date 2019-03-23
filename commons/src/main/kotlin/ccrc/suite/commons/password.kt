package ccrc.suite.commons

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.Validated

private val spcharset = """!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~"""
private val specialChars = spcharset.toList()
fun String.validPassword(
    minLength: Int = 8,
    maxLength: Int = 1024,
    capitals: Boolean = true,
    specials: Boolean = true
): Validated<BadPassword, String> =
    when {
        length > maxLength -> Invalid(LongPassword(length, maxLength))
        length < minLength -> Invalid(ShortPassword(length, minLength))
        !any { it.isUpperCase() } && capitals -> Invalid(MissingUpper("Requires at least one uppercase character"))
        !any { it.isLowerCase() } && capitals -> Invalid(MissingLower("Requires at least one uppercase character"))
        !any { specialChars.contains(it) } && specials -> Invalid(MissingCharset(spcharset))
        else -> Valid(this)
    }

typealias BadPassword = PasswordValidationError
typealias BadLength = PasswordValidationError.PasswordLengthError
typealias ShortPassword = PasswordValidationError.PasswordLengthError.ShortPasswordError
typealias LongPassword = PasswordValidationError.PasswordLengthError.LongPasswordError
typealias MissingCharset = PasswordValidationError.SpecialCharsMissingError
typealias MissingUpper = PasswordValidationError.MissingUppercaseChars
typealias MissingLower = PasswordValidationError.MissingLowercaseChars

sealed class PasswordValidationError() {
    abstract val message: Any?

    sealed class PasswordLengthError : PasswordValidationError() {
        abstract val length: Int
        abstract val threshold: Int

        data class ShortPasswordError(
            override val length: Int,
            override val threshold: Int = 6
        ) : PasswordLengthError() {
            override val message = "Password too short [$length/$threshold]"
        }

        data class LongPasswordError(
            override val length: Int,
            override val threshold: Int = 6,
            override val message: String = "Password too long [$length/$threshold]"
        ) : PasswordLengthError()
    }

    data class SpecialCharsMissingError(
        val charset: String,
        override val message: String = "Requires at least one special character from [$charset]"
    ) : PasswordValidationError()

    data class MissingUppercaseChars(override val message: String) : PasswordValidationError()
    data class MissingLowercaseChars(override val message: String) : PasswordValidationError()
}