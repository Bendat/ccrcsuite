package ccrc.suite.lib.process

import ccrc.suite.commons.logger.Loggable
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.listener.ProcessListener
import java.util.*

class ProcessManager() {
    val queue = PriorityQueue<ProcessRunner>()
    fun makeProcess(){

    }
    inner class ExecutionListener : ProcessListener(), Loggable {

        override fun afterFinish(process: Process, result: ProcessResult) {
        }

        override fun afterStop(process: Process?) {
        }

        override fun afterStart(process: Process?, executor: ProcessExecutor?) {
        }
    }
}
