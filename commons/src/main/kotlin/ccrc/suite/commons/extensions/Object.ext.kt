package ccrc.suite.commons.extensions

import ccrc.suite.commons.Serializer

val Any.json get() = Serializer.writeJson(this)