package ccrc.suite.commons.utils

fun safeWait(millis: Long) {
    val time = System.currentTimeMillis()
    while (true) {
        if (System.currentTimeMillis() - time > millis)
            break
    }
}
