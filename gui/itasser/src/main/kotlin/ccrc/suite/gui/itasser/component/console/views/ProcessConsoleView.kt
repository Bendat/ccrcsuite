package ccrc.suite.gui.itasser.component.console.views

import ccrc.suite.commons.logger.Logger
import ccrc.suite.gui.itasser.component.console.viewmodels.ProcessConsoleViewViewModel
import ccrc.suite.gui.itasser.proxy.controller.ProcessManagerController
import tornadofx.*

class ProcessConsoleView : View("Process Output Console"), Logger {
    val model by inject<ProcessConsoleViewViewModel>()
    val pm by inject<ProcessManagerController>()
    override val root = vbox {
        vbox {
            textarea() {
                isEditable = false
                model.consoleTextArea = this
                cssclass("console")
            }
            textfield(model.seqData) {}
        }
        hbox {
            spacer { }
            button("First") {

            }
            button("Second") {}
            button("Third") { }
            spacer {}
        }
    }
}

