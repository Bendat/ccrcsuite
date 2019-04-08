package ccrc.suite.gui.itasser.component.console.controllers

import ccrc.suite.commons.logger.Logger
import ccrc.suite.lib.process.ProcessRunner
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import tornadofx.getValue
import tornadofx.onChange
import tornadofx.setValue

class ProcessConsoleViewController : Logger {

    val processProperty = SimpleObjectProperty<ProcessRunner>()
    var process by processProperty
    val consoleTextProperty: SimpleListProperty<String> = SimpleListProperty(FXCollections.observableArrayList())
    var text by consoleTextProperty

    init {
        processProperty.onChange {
            info { "Property changed [${it?.std?.output}]" }

        }
    }
//    val text by process.std.output
}


