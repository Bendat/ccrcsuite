@file:Suppress("unused")

package ccrc.suite.lib.test

import ccrc.suite.commons.logger.Loggable
import ccrc.suite.lib.process.ITasserProcess
import ccrc.suite.lib.process.PerlProcess
import ccrc.suite.lib.process.ProcessRunner
import com.winterbe.expekt.should
import org.junit.platform.commons.annotation.Testable
import org.spekframework.spek2.Spek
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import java.util.*
import kotlin.collections.ArrayList

val log = object : Loggable {}

@Testable
object ProcessRunnerTest : Spek({
    group("Process Runner Tests") {
        val os by memoized { LoggingAppender(UUID.randomUUID()) }
        val es by memoized { ErrorLoggingAppender(UUID.randomUUID()) }
        val hello by memoized {
            val file = javaClass.getResource("/HelloWorld.pl").file
            getProcess(file)
        }
        val loop5 by memoized {
            val file = javaClass.getResource("/Loop5.pl").file
            getProcess(file)
        }

        test("Hello World Test") {
            val listener = processListener(ExitCodes.Success)
            val runner = ProcessRunner(hello, listener)
                .outputTo(os).errorTo(es)
            runner.start()
            val res = runner.future?.get()
            log.info { res }
        }

        test("Waiting for Async Result") {
            val listener = processListener(ExitCodes.Success)
            val runner = ProcessRunner(loop5, listener)
                .outputTo(os).errorTo(es)
            runner.start()
            val res = runner.future?.get()
            os.size.should.equal(6)
            log.info { res }
        }

        test("Gracefully Stopping Process") {
            val listener = processListener(ExitCodes.SigTerm)
            val runner = ProcessRunner(loop5, listener)
                .outputTo(os).errorTo(es)
            runner.start()
            safeWait(1500)
            runner.stop()
            os.size.should.equal(2)
        }

        test("Forcefully Stopping Process") {
            val listener = processListener(ExitCodes.SigKill)
            val runner = ProcessRunner(loop5, listener)
                .outputTo(os).errorTo(es)
            runner.start()
            safeWait(1500)
            runner.destroy()
            val res = runner.future?.get()
            os.size.should.equal(2)
            log.info { res }
        }

        test("Printing to STDERR") {
            val file = javaClass.getResource("/Error.pl").file
            val proc = getProcess(file)
            val listener = processListener(ExitCodes.Success)
            val runner = ProcessRunner(proc, listener)
                .outputTo(os).errorTo(es)
            runner.start()
            val res = runner.future?.get()
            es.size.should.equal(1)
            log.info { res }
        }

    }
})

private fun getProcess(file:String): ITasserProcess {
    return ITasserProcess(
        UUID.randomUUID(),
        listOf("perl", file),
        System.currentTimeMillis(),
        UUID.randomUUID(),
        PerlProcess.ExecutionState.Queued,
        0
    )
}

fun safeWait(millis:Long){
    val time = System.currentTimeMillis()
    while(true){
        if(System.currentTimeMillis() - time > millis)
            break
    }
}
private fun processListener(exitCode: Int): ProcessListener {
    return listener {
        afterFinish { _, it2 ->
            it2.exitValue.should.equal(exitCode)
        }
    }
}

fun listener(op: TestListener.() -> Unit): ProcessListener {
    return TestListener().apply(op)
}

class TestListener : ProcessListener(), Loggable {
    private val bstart = ArrayList<(ProcessExecutor) -> Unit>()
    private val astart = ArrayList<(Process, ProcessExecutor) -> Unit>()
    private val astop = ArrayList<(Process) -> Unit>()
    private val afinish = ArrayList<(Process, ProcessResult) -> Unit>()


    fun beforeStart(op: (ProcessExecutor) -> Unit) {
        bstart += op
    }

    fun afterStart(op: (Process, ProcessExecutor) -> Unit) {
        astart += op
    }

    fun afterStop(op: (Process) -> Unit) {
        astop += op
    }

    fun afterFinish(op: (Process, ProcessResult) -> Unit) {
        afinish += op
    }

    override fun beforeStart(executor: ProcessExecutor) {
        info { "About to start [$executor.]" }
        bstart.forEach { it(executor) }
    }

    override fun afterStop(process: Process) {
        info { "Stopped [$process]" }
        astop.forEach { it(process) }
    }

    override fun afterStart(process: Process, executor: ProcessExecutor) {
        info { "Started [$process] [$executor]" }
        astart.forEach { it(process, executor) }
    }

    override fun afterFinish(process: Process, result: ProcessResult) {
        info { "Finished [$process] result [${result.exitValue}]" }
        afinish.forEach { it(process, result) }
    }

    private enum class Events {
        BeforeStart,
        AfterStart,
        BeforeStop,
        AfterStop,
        AfterFinish
    }
}

class LoggingAppender(val processId: UUID) : Loggable, LogOutputStream() {
    private val lines = arrayListOf<String>()
    override fun processLine(p0: String) {
        info { "[$processId] processing line [$p0] " }
        lines += p0
    }
    val size get() = lines.size
}

class ErrorLoggingAppender(val processId: UUID) : Loggable, LogOutputStream() {
    private val lines = arrayListOf<String>()
    override fun processLine(p0: String) {
        error { "[$processId] processing line [$p0] " }
        lines += p0
    }
    val size get() = lines.size

}