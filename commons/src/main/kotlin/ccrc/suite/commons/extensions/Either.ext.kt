package ccrc.suite.commons.extensions

import arrow.core.Either

fun<L, R> Either<L, R>.ifLeft(op: (L)->Unit): Either<L, R> {
    return mapLeft {
        op(it)
        it
    }
}

fun<L, R> Either<L, R>.ifRight(op: (R)->Unit): Either<L, R> {
    return map {
        op(it)
        it
    }
}