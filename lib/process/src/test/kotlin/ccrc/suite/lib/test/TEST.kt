package ccrc.suite.lib.test

import org.spekframework.spek2.Spek

object MyTest: Spek({
    group("a group") {
        test("a test") {

        }

        group("a nested group") {
            test("another test") {
            }
        }
    }
})