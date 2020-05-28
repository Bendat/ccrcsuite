package ccrc.suite.lib.process.v2.process

import org.zeroturnaround.exec.listener.ProcessListener

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