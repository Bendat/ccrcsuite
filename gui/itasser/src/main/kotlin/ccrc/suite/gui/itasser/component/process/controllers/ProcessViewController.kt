package ccrc.suite.gui.itasser.component.process.controllers

import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.utils.uuid
import ccrc.suite.gui.itasser.component.process.views.ProcessesView
import ccrc.suite.lib.process.ArgNames
import ccrc.suite.lib.process.ProcessManager
import ccrc.suite.lib.process.ProcessManager.FXProcessManager
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue
import java.io.File
import java.util.*

class ProcessViewController(val manager: ProcessManager) : ItemViewModel<ProcessManager>(manager) {
    val queues = bind(ProcessManager::queues)
    val completed get() = queues.value[PerlProcess.ExecutionState.Completed]!!
    val running get() = queues.value[PerlProcess.ExecutionState.Running]!!
    val failed get() = queues.value[PerlProcess.ExecutionState.Failed]!!
    val paused get() = queues.value[PerlProcess.ExecutionState.Paused]!!
    val queued get() = queues.value[PerlProcess.ExecutionState.Queued]!!

    val maxSizeProperty = SimpleIntegerProperty(this, "max", 3)
        .also { m -> manager.let { it<FXProcessManager> { m.bind(it.maxProperty) } } }

    var max by maxSizeProperty

    fun newProcess(new: ProcessesView.NewProcessViewModel): UUID {
        val args = listOf("perl", ArgNames.AutoFlush.toString(), new.file)
        return manager.new(0, File(new.file), new.name, args, uuid)
    }

    fun run(id: UUID){
        manager.run(id)
    }
}