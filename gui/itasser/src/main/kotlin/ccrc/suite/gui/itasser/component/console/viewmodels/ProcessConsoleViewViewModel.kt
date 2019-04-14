package ccrc.suite.gui.itasser.component.console.viewmodels

import ccrc.suite.commons.TrackedItem
import ccrc.suite.commons.logger.Logger
import ccrc.suite.gui.itasser.component.console.controllers.ProcessConsoleViewController
import ccrc.suite.lib.file.test.seq.SeqParser
import javafx.collections.ObservableList
import javafx.scene.control.TextArea
import javafx.util.StringConverter
import tornadofx.ItemViewModel
import tornadofx.bind
import tornadofx.onChange

class ProcessConsoleViewViewModel(controller: ProcessConsoleViewController = ProcessConsoleViewController()) :
    ItemViewModel<ProcessConsoleViewController>(controller), Logger {
    val consoleText: String? get() = consoleTextArea.text
    val seqData = bind(ProcessConsoleViewController::seqDataProperty)
    val process = bind(ProcessConsoleViewController::processProperty, autocommit = true).also {
        it.onChange {
            consoleTextArea.bind(item.process.std.output, converter = converter)
            seqData.value = SeqParser.parse(item.process.process.seq).sequences.firstOrNull()?.body?.value
        }
    }

    lateinit var consoleTextArea: TextArea
    private val converter
        get() = object : StringConverter<ObservableList<TrackedItem<String>>>() {

            override fun fromString(string: String?): ObservableList<TrackedItem<String>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun toString(item: ObservableList<TrackedItem<String>>): String {
                return item.joinToString(separator = "\n") { "[${it.timestamp}]\t${it.item}" }
            }
        }
}