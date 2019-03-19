package ccrc.suite.gui.controllers

import ccrc.suite.commons.PerlProcess
import ccrc.suite.lib.process.ProcessManager
import tornadofx.ItemViewModel
import tornadofx.ViewModel

class ProcessViewController(manager: ProcessManager) : ItemViewModel<ProcessManager>(manager) {
    val queues = bind(ProcessManager::queues)
    val completed get() = queues.value[PerlProcess.ExecutionState.Completed]!!
    val running get() = queues.value[PerlProcess.ExecutionState.Running]!!
    val failed get() = queues.value[PerlProcess.ExecutionState.Failed]!!
    val paused get() = queues.value[PerlProcess.ExecutionState.Paused]!!
    val queued get() =  queues.value[PerlProcess.ExecutionState.Queued]!!
}