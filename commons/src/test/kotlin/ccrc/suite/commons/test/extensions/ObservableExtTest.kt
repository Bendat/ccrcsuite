package ccrc.suite.commons.test.extensions

import ccrc.suite.commons.extensions.addUpdatable
import com.winterbe.expekt.should
import lk.kotlin.observable.list.filtering
import lk.kotlin.observable.list.observableListOf
import lk.kotlin.observable.property.StandardObservableProperty
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ObservableExtTest : Spek({
    describe("ObservableList extensions") {
        val list = observableListOf<TestAddable>()
        val evens = list.filtering { it.value % 2 == 0 }
        val updatableTestAddable = TestAddable(1)
        describe("Verify Add Updatable") {
            it("Populates the list") {
                list.addUpdatable(updatableTestAddable) { it.property }
                list.add(TestAddable(2))
            }

            it("Checks the filtered size") {
                evens.size.should.equal(1)
            }

            it("Updates the property of $updatableTestAddable") {
                updatableTestAddable.value = 4
            }

            it("Verifies the update propagated"){
                evens.size.should.equal(2)
            }
        }
    }
})

class TestAddable(i: Int) {
    val property = StandardObservableProperty(i)
    var value by property
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TestAddable
        if (value != other.value) return false
        return true
    }

    override fun toString(): String {
        return "TestAddable(property=$value)"
    }

    override fun hashCode(): Int {
        return property.hashCode()
    }

}
