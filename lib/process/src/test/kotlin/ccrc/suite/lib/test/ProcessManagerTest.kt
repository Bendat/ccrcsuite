package ccrc.suite.lib.test

import ccrc.suite.lib.process.ITasserProcess
import ccrc.suite.commons.PerlProcess
import ccrc.suite.lib.process.ProcessManager
import ccrc.suite.lib.process.ProcessRunner
import com.winterbe.expekt.should
import org.spekframework.spek2.Spek
import java.util.*
import java.util.UUID.randomUUID

class ProcessManagerTest : Spek({
    val pm by memoized { ProcessManager() }

    group("Priority Tests") {
        test("Adding process") {
            val id = randomUUID()
            val proc = getProcess(id)
            val runner = ProcessRunner(proc, listener {})
            pm[0] = runner
            pm.size.should.equal(1)
        }

        test("Retrieving processes by priority") {
            val proc1 = getRunner(randomUUID())
            val prco2 = getRunner(randomUUID())
            val proc3 = getRunner(randomUUID())
            val proc4 = getRunner(randomUUID())
            pm[0] = proc1
            pm[0] = prco2
            pm[1] = proc3
            pm[2] = proc4
            pm.size.should.equal(4)
            pm.size.should.equal(4)
            pm[0].size.should.equal(2)
            pm[0] = proc4
            pm.size.should.equal(4)
            pm[0].size.should.equal(3)
        }
    }
})

private fun getRunner(id: UUID): ProcessRunner {
    return ProcessRunner(getProcess(id), listener {})
}

private fun getProcess(id: UUID): ITasserProcess {
    return ITasserProcess(
        id,
        listOf(),
        System.currentTimeMillis(),
        randomUUID(),
        PerlProcess.ExecutionState.Queued
    )
}