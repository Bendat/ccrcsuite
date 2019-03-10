package ccrc.suite.commons.extensions.file

import java.io.File
import kotlin.reflect.KClass

inline fun <reified T> resource(path:String): File {
    return File(T::class.java.getResource(path).file)
}
fun resource(path:String, clazz: KClass<Any>): File {
    return File(clazz::class.java.getResource(path).file)
}