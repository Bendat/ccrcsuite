package ccrc.suite

import ccrc.suite.commons.extensions.addUpdatable
import com.winterbe.expekt.should
import lk.kotlin.observable.list.filtering
import lk.kotlin.observable.list.observableListOf
import lk.kotlin.observable.property.StandardObservableProperty
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class Sandbox : Spek({
    val a = A(1)
    val list = observableListOf<A>().apply {
        addUpdatable(a) { a.iprop }
        addUpdatable(A(2)) { it.iprop }
    }
    val evens = list.filtering { it.i2 % 2 == 0 }
    describe("it") {
        it("Checks the filtered size") {
            evens.size.should.equal(1)
            a.i2 = 2
        }

        it("Adds to list") {
            evens.size.should.equal(2)
        }
    }
})

class A(i: Int) {
    val iprop = StandardObservableProperty(i)
    var i2 by iprop
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as A
        if (i2 != other.i2) return false
        return true
    }
}
