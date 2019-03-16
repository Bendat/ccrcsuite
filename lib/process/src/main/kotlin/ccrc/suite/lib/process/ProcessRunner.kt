package ccrc.suite.lib.process

import arrow.core.None
import arrow.core.Option
import arrow.core.some
import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.logger.Loggable
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.StartedProcess
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import org.zeroturnaround.process.ProcessUtil.destroyForcefullyAndWait
import org.zeroturnaround.process.ProcessUtil.destroyGracefullyAndWait
import org.zeroturnaround.process.Processes
import org.zeroturnaround.process.SystemProcess
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit.SECONDS

@Suppress("UNUSED_EXPRESSION")
class ProcessRunner(
    val process: PerlProcess,
    listener: ProcessListener
) : Loggable {
    val state get() = process.state
    val isRunnable get() = process.state.isRunnable

    var future: Option<Future<ProcessResult>> = None
    val output = SimpleListProperty<String>(FXCollections.observableArrayList())
    val errors = SimpleListProperty<String>(FXCollections.observableArrayList())

    private val processExecutor: ProcessExecutor = ProcessExecutor()
        .command(process.args).readOutput(true).addListener(listener)
    private var realProcess: Option<StartedProcess> = None
    private var sysProcess: Option<SystemProcess> = None
    fun start(): ProcessRunner {
        realProcess = processExecutor.start().some()
        realProcess.map {
            sysProcess = Processes.newStandardProcess(it.process).some()
            future = it.future.some()
        }
        process.state = PerlProcess.ExecutionState.Running
        return this
    }

    init {
        outputTo(ProcessLogAppender(output))
        errorTo(ProcessLogAppender(errors))
    }

    fun await() {
        realProcess.map { it.future.get() }
    }

    fun await(timeout: Long) {
        sysProcess.map { it.waitFor(timeout, SECONDS) }
    }

    fun stop(): ProcessRunner {
        sysProcess.map { destroyGracefullyAndWait(it, 10, SECONDS) }
        return this
    }

    fun destroy(): ProcessRunner {
        sysProcess.map { destroyForcefullyAndWait(it) }
        return this
    }

    fun outputTo(stream: LogOutputStream): ProcessRunner {
        processExecutor.redirectOutput(stream)
        return this
    }

    fun errorTo(stream: LogOutputStream): ProcessRunner {
        processExecutor.redirectError(stream)
        return this
    }

    override fun toString(): String {
        return "ProcessRunner(runner=${process.id})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProcessRunner

        if (process != other.process) return false

        return true
    }

    override fun hashCode(): Int {
        return process.hashCode()
    }

    inner class ProcessLogAppender(val list: MutableList<String>) : LogOutputStream(), Loggable {
        override fun processLine(p0: String) {
            info { "Logging line [$p0] for runner [${process.name}][${process.id}]" }
            list += p0
        }

    }
}

fun itListener(op: ITasserListener.() -> Unit): ITasserListener {
    return ITasserListener().apply(op)
}

class ITasserListener : ProcessListener() {
    val beforeStart = arrayListOf<(ProcessExecutor) -> Unit>()
    val afterStart = arrayListOf<(Process, ProcessExecutor) -> Unit>()
    val afterStop = arrayListOf<(Process) -> Unit>()
    val afterFinish = arrayListOf<(Process, ProcessResult) -> Unit>()

    override fun beforeStart(executor: ProcessExecutor) {
        super.beforeStart(executor)
        beforeStart.forEach { it(executor) }
    }

    override fun afterStop(process: Process) {
        super.afterStop(process)
        afterStop.forEach { it(process) }
    }

    override fun afterStart(process: Process, executor: ProcessExecutor) {
        super.afterStart(process, executor)
        afterStart.forEach { it(process, executor) }
    }

    override fun afterFinish(process: Process, result: ProcessResult) {
        super.afterFinish(process, result)
        afterFinish.forEach { it(process, result) }
    }
}