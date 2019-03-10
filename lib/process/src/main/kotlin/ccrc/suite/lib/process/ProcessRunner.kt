package ccrc.suite.lib.process

import arrow.core.None
import arrow.core.Option
import arrow.core.some
import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.logger.Loggable
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
import java.util.concurrent.TimeUnit.*

class ProcessRunner(
    val process: PerlProcess,
    listener: ProcessListener
) : Loggable {
    var future: Option<Future<ProcessResult>> = None
    private var realProcess: Option<StartedProcess> = None
    private var sysProcess: Option<SystemProcess> = None
    private val processExecutor: ProcessExecutor = ProcessExecutor()
        .command(process.args).readOutput(true).addListener(listener)

    fun start() {
        realProcess = processExecutor.start().some()
        realProcess.map {
            sysProcess = Processes.newStandardProcess(it.process).some()
            future = it.future.some()
        }
        process.state = PerlProcess.ExecutionState.Running
    }

    fun stop() {
        sysProcess.map { destroyGracefullyAndWait(it, 10, SECONDS) }
    }

    fun destroy(){
        sysProcess.map { destroyForcefullyAndWait(it) }

    }

    fun outputTo(stream: LogOutputStream): ProcessRunner {
        processExecutor.redirectOutput(stream)
        return this
    }

    fun errorTo(stream: LogOutputStream): ProcessRunner {
        processExecutor.redirectError(stream)
        return this
    }
}