package ccrc.suite.gui.itasser.component.console.viewmodels

import ccrc.suite.commons.TrackedItem
import ccrc.suite.commons.logger.Logger
import ccrc.suite.gui.itasser.component.console.controllers.ProcessConsoleViewController
import javafx.collections.ObservableList
import javafx.scene.control.TextArea
import javafx.util.StringConverter
import tornadofx.ItemViewModel
import tornadofx.bind
import tornadofx.onChange

class ProcessConsoleViewViewModel(controller: ProcessConsoleViewController = ProcessConsoleViewController()) :
    ItemViewModel<ProcessConsoleViewController>(controller), Logger {
    val text = bind(ProcessConsoleViewController::consoleTextProperty, autocommit = true)
    val process = bind(ProcessConsoleViewController::processProperty, autocommit = true).also {
        it.onChange { consoleTextArea.bind(item.process.std.output, converter = converter) }
    }

    lateinit var consoleTextArea: TextArea
    val converter
        get() = object : StringConverter<ObservableList<TrackedItem<String>>>() {
            override fun fromString(string: String?): ObservableList<TrackedItem<String>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun toString(item: ObservableList<TrackedItem<String>>): String {
                info { "Converting [$item]" }
                return item.map { "[${it.timestamp}]\t${it.item}" }.joinToString(separator = "\n")
            }
        }
}