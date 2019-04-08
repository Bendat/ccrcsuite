package ccrc.suite.lib.process

import arrow.core.None
import arrow.core.Option
import arrow.core.Try
import arrow.core.some
import ccrc.suite.commons.ErrorHandler
import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.TrackingList
import ccrc.suite.commons.logger.Logger
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
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

@Suppress("UNUSED_EXPRESSION")
class ProcessRunner(
    val process: PerlProcess,
    listener: ProcessListener
) : Logger, ErrorHandler<ProcessError> {
    override val errors = TrackingList<ProcessError>()

    val state get() = process.state
    val isRunnable get() = process.state.isRunnable

    var future: Option<Future<ProcessResult>> = None

    val std = STD()
    private val processExecutor: ProcessExecutor = ProcessExecutor()
        .command(process.args).readOutput(true).addListener(listener)

    private var realProcess: Option<StartedProcess> = None
    private var sysProcess: Option<SystemProcess> = None

    fun start(): ProcessRunner {
        if (!isRunnable) return this
        realProcess = processExecutor.start().some()
        realProcess.map {
            sysProcess = Processes.newStandardProcess(it.process).some()
            future = it.future.some()
        }
        process.state = PerlProcess.ExecutionState.Running
        return this
    }

    init {
        outputTo(ProcessLogAppender(std.output))
        errorTo(ProcessLogAppender(std.err))
    }

    fun await(): Option<ProcessResult> = realProcess.flatMap { p ->
        Try { p.future.get() }.toEither()
            .mapLeft { errors += Timeout("[${process.name}] timeoutd out", it) }
            .toOption()
    }


    fun await(timeout: Long): Option<ProcessResult> = realProcess.flatMap { p ->
        Try { p.future.get(timeout, MILLISECONDS) }.toEither()
            .mapLeft { errors += Timeout("[${process.name}] timeoutd out", it) }
            .toOption()
    }


    fun stop(): Option<ProcessRunner> = sysProcess.flatMap { p ->
        Try { destroyGracefullyAndWait(p, 10, SECONDS) }.toEither()
            .mapLeft { errors += Timeout("[${process.name}] timeoutd out", it) }
            .map { this }.toOption()
    }


    fun destroy(): Option<ProcessRunner> = sysProcess.flatMap { p ->
        Try { destroyForcefullyAndWait(p) }.toEither()
            .mapLeft { errors += Timeout("[${process.name}] timeoutd out", it) }
            .map { this }.toOption()
    }

    fun outputTo(stream: LogOutputStream): ProcessRunner {
        info { "Redirecting output" }
        processExecutor.redirectOutput(stream)
        return this
    }

    fun errorTo(stream: LogOutputStream): ProcessRunner {
        info { "Redirecting error" }
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

    inner class ProcessLogAppender(val list: TrackingList<String>) : LogOutputStream(), Logger {
        override fun processLine(p0: String) {
            info { "Logging line [$p0] for runner [${process.name}][${process.id}]" }
            list += p0
        }
    }

    inner class STD {
        val output = TrackingList<String>()
            get () {
                info { "Output accessed" }
                return field
            }
        val err = TrackingList<String>()
    }
}

fun itListener(op: ITasserListener.() -> Unit): ITasserListener {
    return ITasserListener().apply(op)
}

class ITasserListener : ProcessListener(), Logger {
    val beforeStart = arrayListOf<(ProcessExecutor) -> Unit>()
    val afterStart = arrayListOf<(Process, ProcessExecutor) -> Unit>()
    val afterStop = arrayListOf<(Process) -> Unit>()
    val afterFinish = arrayListOf<(Process, ProcessResult) -> Unit>()

    override fun beforeStart(executor: ProcessExecutor) {
        super.beforeStart(executor)
        debug { "About to start process" }
        beforeStart.forEach { it(executor) }
    }

    override fun afterStop(process: Process) {
        super.afterStop(process)
        debug { "Stopped process" }
        afterStop.forEach { it(process) }
    }

    override fun afterStart(process: Process, executor: ProcessExecutor) {
        super.afterStart(process, executor)
        debug { "Started process" }
        afterStart.forEach { it(process, executor) }
    }

    override fun afterFinish(process: Process, result: ProcessResult) {
        super.afterFinish(process, result)
        debug { "Finished with [$result]" }
        afterFinish.forEach { it(process, result) }
    }
}