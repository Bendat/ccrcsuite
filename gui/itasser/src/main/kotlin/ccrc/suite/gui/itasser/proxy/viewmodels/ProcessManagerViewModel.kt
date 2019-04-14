package ccrc.suite.gui.itasser.proxy.viewmodels

import ccrc.suite.gui.itasser.proxy.controller.ProcessManagerController
import tornadofx.ItemViewModel
import java.util.*

class ProcessManagerViewModel
    : ItemViewModel<ProcessManagerController>(ProcessManagerController()) {
    val manager = bind(ProcessManagerController::managerProperty)

    fun pause(id: UUID){
        item?.manager?.pause(id)
    }
}