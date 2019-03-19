package ccrc.suite.gui.views

import ccrc.suite.commons.logger.Loggable
import ccrc.suite.gui.controllers.ProcessViewController
import ccrc.suite.lib.process.ProcessManager
import ccrc.suite.lib.process.ProcessQueue
import ccrc.suite.lib.process.Wrapper
import javafx.scene.control.ListView
import tornadofx.*

class ProcessesView(val manager: ProcessManager) : View("Process List"), Loggable {
    val model = ProcessViewController(manager)
    override val root = vbox {
        squeezebox {
            procFold("Started", model.running)
            procFold("Completed", model.completed)
            procFold("Paused", model.paused)
            procFold("Queued", model.queued)
            procFold("Failed", model.failed)
        }
    }

    private fun SqueezeBox.procFold(name: String, queue: ProcessQueue) {
        fold(name, true) {
            listview<Wrapper>(queue) {
                addClass(name)
                cellFormat {
                    graphic = hbox {
                        button(">")
                        label(name)
                        button("☐")
                    }

                }

            }
        }
    }


    private fun SqueezeBox.processFold(
        name: String,
        queue: ProcessQueue,
        op: ListView<Wrapper>.() -> Unit = {}
    ) = ProcessFold(name, queue).apply { root.apply(op) }

    inner class ProcessFold(
        val name: String,
        val queue: ProcessQueue
    ) : Fragment() {
        override val root = listview<Wrapper>(queue) {
            addClass(name)
            cellFormat {
                text = null
                graphic = hbox {
                    button(">") { }
                    label(it.runner.process.name) { }
                    button("☐")
                }
            }

        }
    }
}

