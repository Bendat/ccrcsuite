package ccrc.suite.gui.itasser.component.console.views

import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.logger.Logger
import ccrc.suite.gui.itasser.component.console.viewmodels.ProcessConsoleViewViewModel
import ccrc.suite.gui.itasser.proxy.viewmodels.ProcessManagerViewModel
import tornadofx.*

class ProcessConsoleView : View("Process Output Console"), Logger {
    val model by inject<ProcessConsoleViewViewModel>()
    val pm: ProcessManagerViewModel by inject()

    override val root = vbox {
        vbox {
            textarea {
                isEditable = false
                model.consoleTextArea = this
                cssclass("console")
            }
        }
        hbox {
            spacer()
            button("Run") {
                model.item.process.process.stateProperty.onChange {
                    text = when (it) {
                        is PerlProcess.ExecutionState.Running -> "Pause"
                        else -> "Run"
                    }
                }
                setOnAction {
                    when (text) {
                        "Run" -> pm.start(model.item.process?.process?.id)
                        else -> pm.pause(model.item.process?.process?.id)
                    }

                }
                cssclass("play_pause_button")
            }
            button("Stop") {
                cssclass("stop_button")

            }
            spacer()
        }
    }
}
