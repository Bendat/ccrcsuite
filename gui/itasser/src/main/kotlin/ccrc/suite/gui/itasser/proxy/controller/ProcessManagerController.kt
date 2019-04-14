package ccrc.suite.gui.itasser.proxy.controller

import ccrc.suite.lib.process.ProcessManager
import javafx.beans.property.SimpleObjectProperty
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue

class ProcessManagerController : Controller() {
    val managerProperty = SimpleObjectProperty<ProcessManager>()
    var manager by managerProperty
}