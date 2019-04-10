package ccrc.suite.commons

import ccrc.suite.commons.logger.KLog
import ccrc.suite.commons.utils.uuid
import com.fasterxml.jackson.annotation.JsonCreator
import org.apache.commons.validator.routines.EmailValidator
import org.mindrot.jbcrypt.BCrypt
import java.util.*

inline class Username(val value: String)
inline class Password(val value: String) {
    val hashed get() = Password(BCrypt.hashpw(value, BCrypt.gensalt(10))).also{
        KLog(Password::class).info{"Hashing [$value] to [$it]"}
    }

    val isValid get() = value.validPassword()
}

inline class EmailAddress(val value: String) {
    val isValid get() = EmailValidator.getInstance().isValid(value)
}

fun hasheduser(
    name: Username,
    password: Password,
    email: EmailAddress,
    id: ID = ID(uuid)
) = User(name, password.hashed, email, id)

fun hasheduser(user: User) = hasheduser(user.name, user.password, user.email, user.id)

data class User(
    val name: Username,
    val password: Password,
    val email: EmailAddress,
    override val id: ID = ID(uuid)
) : DBObject {
    /*
    Workaround for lack of serialization support in jackson for inline classes
     */
    @JsonCreator
    constructor(
        name: String,
        password: String,
        email: String,
        id: UUID = uuid,
        @Suppress("UNUSED_PARAMETER") bool: Boolean = true
    ) : this(Username(name), Password(password), EmailAddress(email), ID(id))
}

