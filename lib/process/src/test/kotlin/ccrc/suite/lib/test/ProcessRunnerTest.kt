@file:Suppress("unused")

package ccrc.suite.lib.test

import arrow.core.None
import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.logger.Logger
import ccrc.suite.commons.utils.safeWait
import ccrc.suite.lib.process.ArgNames.AutoFlush
import ccrc.suite.lib.process.ExitCodes
import ccrc.suite.lib.process.ITasserProcess
import ccrc.suite.lib.process.ProcessRunner
import com.winterbe.expekt.should
import org.spekframework.spek2.Spek
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

val log = object : Logger {}

class ProcessRunnerTest : Spek({
    val os by memoized { LoggingAppender(UUID.randomUUID()) }
    val es by memoized { ErrorLoggingAppender(UUID.randomUUID()) }
    val autoflush by memoized {
        true
    }
    val hello by memoized {
        val file = javaClass.getResource("/HelloWorld.pl").file
        getProcess(file)
    }
    val loop5 by memoized {
        val file = javaClass.getResource("/Loop5.pl").file
        getProcess(file)
    }

    group("Creating Processes") {
        test("Hello World Test") {
            val listener = processListener(ExitCodes.Success)
            val runner = ProcessRunner(hello, listener)
                .outputTo(os).errorTo(es)
            runner.start()
            runner.future.map {
                val res = it.get()
                log.info { res }
            }
            runner.future.should.not.be.an.instanceof(None::class.java)
        }

        test("Waiting for Async Result") {
            val listener = processListener(ExitCodes.Success)
            val runner = ProcessRunner(loop5, listener)
                .outputTo(os).errorTo(es)
            runner.start()
            runner.future.map {
                val res = it.get()
                os.size.should.equal(6)
                log.info { res }
            }
            runner.future.should.not.be.an.instanceof(None::class.java)
        }
    }

    group("Stopping Processes") {
        test("Gracefully Stopping Process") {
            val listener = processListener(ExitCodes.SigTerm)
            val runner = ProcessRunner(loop5, listener)
                .outputTo(os).errorTo(es)
            runner.future.should.be.an.instanceof(None::class.java)
            runner.start()
            safeWait(1500)
            runner.stop()
            runner.future.should.not.be.an.instanceof(None::class.java)
        }

        test("Forcefully Stopping Process") {
            val listener = processListener(ExitCodes.SigKill)
            val runner = ProcessRunner(loop5, listener)
                .outputTo(os).errorTo(es)
            runner.future.should.be.an.instanceof(None::class.java)
            runner.start()
            safeWait(1500)
            runner.destroy()
            runner.future.map {
                val res = it.get()
                println("Res is [$res]")
                log.info { res }
            }
            runner.future.should.not.be.an.instanceof(None::class.java)
        }

    }

    group("Output Streams") {
        test("Printing to STDERR") {
            val file = javaClass.getResource("/Error.pl").file
            val proc = getProcess(file)
            val listener = processListener(ExitCodes.Success)
            val runner = ProcessRunner(proc, listener)
                .outputTo(os).errorTo(es)
            runner.future.should.be.an.instanceof(None::class.java)
            runner.start()
            runner.future.map {
                val res = it.get()
                es.size.should.equal(1)
                es.lines[0].should.equal("Error Message")
                log.info { res }
            }
            runner.future.should.not.be.an.instanceof(None::class.java)
        }
    }
})

val lg get() = object : Logger {}
private fun getProcess(file: String): ITasserProcess {
    val flush = System.getProperty("autoflush")
    lg.info { "Flush is [$flush]" }
    val params = listOf("perl", AutoFlush.toString(), file)
    return ITasserProcess(
        UUID.randomUUID(),
        File(""),
        "Test Program",
        params,
        System.currentTimeMillis(),
        UUID.randomUUID(),
        PerlProcess.ExecutionState.Queued
    )
}


fun processListener(exitCode: Int): ProcessListener {
    return listener {
        afterFinish { _, it2 ->
            it2.exitValue.should.equal(exitCode)
        }
    }
}

fun listener(op: TestListener.() -> Unit): ProcessListener {
    return TestListener().apply(op)
}

class TestListener : ProcessListener(), Logger {
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

class LoggingAppender(val processId: UUID) : Logger, LogOutputStream() {
    val lines = arrayListOf<String>()
    override fun processLine(p0: String) {
        info { "[$processId] processing line [$p0] " }
        lines += p0
    }

    val size get() = lines.size
}

class ErrorLoggingAppender(val processId: UUID) : Logger, LogOutputStream() {
    val lines = arrayListOf<String>()
    override fun processLine(p0: String) {
        error { "[$processId] processing line [$p0] " }
        lines += p0
    }

    val size get() = lines.size

}