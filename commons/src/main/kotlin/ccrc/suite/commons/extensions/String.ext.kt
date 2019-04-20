package ccrc.suite.commons.extensions

fun String.remove(vararg character: String): String {
    var orig = this
    character.forEach { orig = orig.replace(it, "") }
    return orig
}

val String.flattened get() = remove("\n")
val List<String>.flatJoin get() = joinToString("")
