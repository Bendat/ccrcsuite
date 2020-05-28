@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package ccrc.suite.lib.process.v2.process

import arrow.core.*
import ccrc.suite.lib.process.ProcessError
import ccrc.suite.lib.process.Timeout
import ccrc.suite.lib.process.v2.STD
import ccrc.suite.lib.process.v2.details.ExecutionState
import ccrc.suite.lib.process.v2.details.ExecutionState.Queued
import javafx.beans.property.SimpleObjectProperty
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.StartedProcess
import org.zeroturnaround.process.SystemProcess
import tornadofx.getValue
import tornadofx.setValue
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

class ITasser(val process: CCRCProcess) {
    val stateProperty = SimpleObjectProperty<ExecutionState>(Queued)
    var state: ExecutionState by stateProperty
    val priorityProperty = SimpleObjectProperty(0)
    var priority: Int by priorityProperty
    val std = STD()

    inner class ExecutionContext {
        var future: Option<Future<ProcessResult>> = None
        private var realProcess: Option<StartedProcess> = None
        private var sysProcess: Option<SystemProcess> = None
        private val processExecutor: ProcessExecutor = ProcessExecutor()
            .command(process.args).readOutput(true).addListener(listener)

        fun await(): Either<ProcessError, ProcessResult> =
            realProcess.toEither {
                ProcessError.NoProcessError("realProcess")
            }.flatMap { p ->
                Try {
                    p.future.get()
                }.toEither {
                    Timeout("[${process.name}] timed out", it)
                }
            }
    }

}