package ccrc.suite

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import org.spekframework.spek2.Spek


fun main(args: Array<String>) {
    val request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectClass(Test::class.java))
        .build()
    val launcher = LauncherFactory.create()
    val listener = SummaryGeneratingListener()

    launcher.registerTestExecutionListeners(listener)
    launcher.execute(request)

    val summary = listener.summary
    val testFoundCount = summary.testsFoundCount
    val failures = summary.failures
    println("getTestsSucceededCount() - " + summary.testsSucceededCount)
    failures.forEach { failure -> System.out.println("failure - " + failure.exception) }
    println(testFoundCount)
}

class Hello {
    @Test
    fun doTest() {
        assertEquals(1, 1)
    }

    @Test
    fun dontTest() {
        assertEquals(1, 2)
    }
}

object Test : Spek({
    group("It") {
        test("it real good") {
            assertEquals(1, 1)
        }
        test("it real good") {
            assertEquals(1, 2)
        }

    }
})