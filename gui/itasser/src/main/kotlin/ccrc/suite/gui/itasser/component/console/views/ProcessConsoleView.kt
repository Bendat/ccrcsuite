package ccrc.suite.gui.itasser.component.console.views

import ccrc.suite.commons.logger.Logger
import ccrc.suite.gui.itasser.component.console.viewmodels.ProcessConsoleViewViewModel
import tornadofx.*

class ProcessConsoleView : View("Process Output Console"), Logger {
    val model by inject<ProcessConsoleViewViewModel>()
    override val root = vbox {
        vbox {
            textarea {
                isEditable = false
                model.consoleTextArea = this
                cssclass("console")
            }
            textfield { }
        }
        hbox {
            spacer { }
            button("First") { }
            button("Second") {}
            button("Third") { }
            spacer {}
        }
    }
}

