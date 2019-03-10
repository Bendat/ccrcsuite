package ccrc.suite.lib.process

import ccrc.suite.commons.logger.Loggable
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.StartedProcess
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.ProcessUtil.destroyForcefullyAndWait
import org.zeroturnaround.process.ProcessUtil.destroyGracefullyAndWait
import org.zeroturnaround.process.Processes
import org.zeroturnaround.process.SystemProcess
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ProcessRunner(
    val process: PerlProcess,
    listener: ProcessListener
) : Loggable {
    var future: Future<ProcessResult>? = null
    private var realProcess: StartedProcess? = null
    private var sysProcess: SystemProcess? = null
    private val processExecutor: ProcessExecutor = ProcessExecutor()
        .command(process.args).readOutput(true).addListener(listener)

    fun start() {
        realProcess = processExecutor.start()
        sysProcess = Processes.newStandardProcess(realProcess?.process)
        future = realProcess?.future
        process.state = PerlProcess.ExecutionState.Running
    }

    fun stop() {
        destroyGracefullyAndWait(sysProcess, 10, TimeUnit.SECONDS)
    }

    fun destroy(){
        destroyForcefullyAndWait(sysProcess)
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