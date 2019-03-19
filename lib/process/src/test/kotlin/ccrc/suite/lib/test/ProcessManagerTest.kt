package ccrc.suite.lib.test

import arrow.core.Some
import ccrc.suite.commons.PerlProcess.ExecutionState
import ccrc.suite.commons.PerlProcess.ExecutionState.*
import ccrc.suite.commons.utils.safeWait
import ccrc.suite.commons.utils.uuid
import ccrc.suite.lib.process.ArgNames
import ccrc.suite.lib.process.ITasserProcess
import ccrc.suite.lib.process.ProcessManager
import ccrc.suite.lib.process.ProcessRunner
import com.winterbe.expekt.should
import org.spekframework.spek2.Spek
import java.io.File
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.UUID.randomUUID

class ProcessManagerTest : Spek({
    val pm by memoized { ProcessManager.StandardProcessManager() }
    group("Units") {

        test("Verifying Accurate Size") {
            val id = pm.new(0, File(""), "Test Process 1", listOf(), uuid)
            pm.size.should.equal(1)
            pm.queues[Queued]?.size.should.equal(1)
            val found = pm.find(id)
            println(found)
        }

        test("Verifying Find Works") {
            val id = pm.new(0, File(""), "Test Process 1", listOf(), uuid)
            pm.size.should.equal(1)
            pm.queues[Queued]?.size.should.equal(1)
            val found = pm.find(id)
            (found is Some).should.be.`true`
            found as Some
            found.t.runner.process.name.should.equal("Test Process 1")
        }

    }

    group("Priority Tests") {
        test("Adding runner") {
            val proc = getRunner(uuid)
            val proc2 = getRunner(uuid, Completed)
            pm[0, proc.state] = proc
            pm[0, proc2.state] = proc2
            pm.size.should.equal(2)
            pm[Queued].size.should.equal(1)
            pm[Completed].size.should.equal(1)
            pm[Failed].size.should.equal(0)
        }

        test("Checking First is accurate") {
            val proc1 = getRunner(randomUUID())
            val proc2 = getRunner(randomUUID())
            val proc3 = getRunner(randomUUID())
            val proc4 = getRunner(randomUUID())
            pm[0, proc1.state] = proc1
            pm[0, proc2.state] = proc2
            pm[1, proc3.state] = proc3
            pm[2, proc4.state] = proc4
            pm.size.should.equal(4)
            pm[Queued, 0].size.should.equal(2)
            pm[Queued].first.map { it.runner.process.should.equal(proc4.process) }
        }

        test("Checking First is accurate after modification") {
            val proc1 = getRunner(uuid)
            val proc2 = getRunner(uuid)
            val proc3 = getRunner(uuid)
            val proc4 = getRunner(uuid)
            pm[0, proc1.state] = proc1
            pm[0, proc2.state] = proc2
            pm[1, proc3.state] = proc3
            pm[2, proc4.state] = proc4
            pm.size.should.equal(4)
            pm[Queued, 0].size.should.equal(2)
            pm[Queued].first.map { it.runner.process.should.equal(proc4.process) }
            val proc5 = getRunner(uuid)
            pm[5, proc5.state] = proc5
            pm[Queued].first.map { it.runner.process.should.equal(proc5.process) }
        }

        test("New Process Test") {
            pm.new(0, File(""), "Test Process 1", listOf(), uuid)
            pm.size.should.equal(1)
        }


        test("Running Process by ID") {
            val file = javaClass.getResource("/HelloWorld.pl").file
            val id = pm.new(0, File(file), "Test Process 1", args(), uuid)
            pm.size.should.equal(1)
            pm.queues[Queued]?.size.should.equal(1)
            val found = pm.find(id)
            (found is Some).should.be.`true`
            found as Some
            found.map { it.state.should.equal(Queued) }
            pm.run(id)
            safeWait(1000)
            val found2 = pm.find(id)
            log.info{ "queues are [${pm.queues}]" }
            pm.queues[Completed]?.size.should.equal(1)
            (found2 is Some).should.be.`true`
            found2 as Some
            found2.map { it.state.should.equal(Completed) }
        }

        test("Waiting for Process Synchronously") {
            val file = javaClass.getResource("/HelloWorld.pl").file
            val id = pm.new(0, File(file), "Test Process 1", args(), uuid)
            pm.size.should.equal(1)
            pm.queues[Queued]?.size.should.equal(1)
            val found = pm.find(id)
            (found is Some).should.be.`true`
            found as Some
            found.map { it.state.should.equal(Queued) }
            pm.run(id)
            pm.waitFor(id)
            val found2 = pm.find(id)
            pm.queues[Completed]?.size.should.equal(1)
            (found2 is Some).should.be.`true`
            found2 as Some
            found2.map { it.state.should.equal(Completed) }
        }

        test("Waiting for Process Synchronously") {
            val file = javaClass.getResource("/HelloWorld.pl").file
            val id = pm.new(0, File(file), "Test Process 1", args(), uuid)
            pm.size.should.equal(1)
            pm.queues[Queued]?.size.should.equal(1)
            val found = pm.find(id)
            (found is Some).should.be.`true`
            found as Some
            found.map { it.state.should.equal(Queued) }
            pm.run(id)
            pm.waitFor(id)
            safeWait(1000)
            val found2 = pm.find(id)
            log.info{ "queues are [${pm.queues}]" }
            pm.queues[Completed]?.size.should.equal(1)
            (found2 is Some).should.be.`true`
            found2 as Some
            found2.map { it.state.should.equal(Completed) }
        }
    }
})

private fun args() = listOf("perl", ArgNames.AutoFlush.toString())


private fun getRunner(id: UUID, state: ExecutionState = Queued): ProcessRunner {
    return ProcessRunner(getProcess(id, state), listener {})
}

private fun getProcess(
    id: UUID,
    state: ExecutionState = Queued,
    file: File = File("")
): ITasserProcess {
    return ITasserProcess(
        id,
        file,
        "Test Program",
        listOf(),
        currentTimeMillis(),
        randomUUID(),
        state
    )
}