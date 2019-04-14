package ccrc.suite.gui.itasser.component.console.controllers

import ccrc.suite.commons.TrackedItem
import ccrc.suite.commons.logger.Logger
import ccrc.suite.lib.process.ProcessRunner
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import tornadofx.getValue
import tornadofx.setValue

class ProcessConsoleViewController : Logger {
    val processProperty = SimpleObjectProperty<ProcessRunner>()
    var process by processProperty
    val consoleTextProperty: SimpleListProperty<TrackedItem<String>> =
        SimpleListProperty(FXCollections.observableArrayList())
    var text by consoleTextProperty
    val seqDataProperty = SimpleStringProperty()
    var seqData by seqDataProperty


//    val text by process.std.output
}



