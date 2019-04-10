package ccrc.suite.commons.extensions

import com.winterbe.expekt.ExpectAny


inline fun <reified T> ExpectAny<in T>.type() {
    instanceof(T::class.java)
}


