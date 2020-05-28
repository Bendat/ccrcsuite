package ccrc.suite.gui.itasser.proxy.viewmodels

import arrow.core.toOption
import ccrc.suite.gui.itasser.proxy.controller.ProcessManagerController
import tornadofx.ItemViewModel
import java.util.*

class ProcessManagerViewModel
    : ItemViewModel<ProcessManagerController>(ProcessManagerController()) {
    val manager = bind(ProcessManagerController::managerProperty)

    fun pause(id: UUID?) =
        id.toOption().map { id -> item?.manager?.pause(id) }

    fun start(id: UUID?) =
        id.toOption().map { id -> item?.manager?.run(id) }

}