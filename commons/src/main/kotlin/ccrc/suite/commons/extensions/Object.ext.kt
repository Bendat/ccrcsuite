package ccrc.suite.commons.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.toOption
import ccrc.suite.commons.Serializer

val Any.json get() = Serializer.writeJson(this)

fun <T> T?.toEither(): Either<None, T> {
    return this.toOption().toEither { None }
}