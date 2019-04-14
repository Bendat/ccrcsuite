@file:Suppress("RedundantLambdaArrow")

package ccrc.suite.gui.itasser.component.process.views

import ccrc.suite.commons.Parameter
import ccrc.suite.commons.logger.Logger
import ccrc.suite.gui.itasser.component.process.controllers.ProcessViewController
import ccrc.suite.lib.process.ProcessManager
import ccrc.suite.lib.process.ProcessQueue
import ccrc.suite.lib.process.Wrapper
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleMapProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.stage.FileChooser
import javafx.util.converter.NumberStringConverter
import tornadofx.*
import java.io.File

class ProcessesView(manager: ProcessManager) : View("Process List"), Logger {
    val model = ProcessViewController(manager)

    constructor() : this(ProcessManager())

    override val root = vbox {
        borderpane {
            left {
                button("+") {
                    addClass("new-process")
                    action {
                        NewProcessWizard().openModal()
                    }
                }
            }
            top { label("Sequences") }
            bottom {
                form {
                    textfield("Max Executing") {
                        Bindings.bindBidirectional(
                            textProperty(),
                            model.maxSizeProperty,
                            NumberStringConverter()
                        )
                    }
                }
            }
            right {
                button("☐") {
                    setOnAction {
                        model.manager.shutdown()
                    }
                }
            }
        }
        squeezebox {
            procFold("Started", model.running)
            procFold("Completed", model.completed)
            procFold("Paused", model.paused)
            procFold("Queued", model.queued)
            procFold("Failed", model.failed)
        }
    }

    @Synchronized
    private fun SqueezeBox.procFold(name: String, queue: ProcessQueue) {
        fold(name, true) {
            vbox {
                hbox {
                    label("Size: ")
                    label(queue.sizeProperty())
                }
                listview<Wrapper>(queue) {
                    addClass(name)
                    cellFormat {
                        addClass(item.runner.process.id.toString())
                        graphic = hbox {
                            button(">") {
                                addClass("sequence-${it.runner.process.id}").also { k ->
                                    info { "Added class [${k}]" }
                                }
                                setOnMouseClicked { _ ->
                                    model.manager.run(it.runner.process.id)
                                }
                            }
                            label(name)
                            button("☐")
                        }

                    }
                }
            }
        }
    }

    class NewProcessModal : View("Setup"), Logger {
        val proc = find<ProcessesView>()
        val model by inject<NewProcessViewModel>()

        init {
            info { "proc is  $proc" }
            info { "model is $model" }
        }

        override val root = form {
            fieldset("SequenceChain Info") {
                field("Seq File ") {
                    textfield(model.name) {
                        addClass("name-field-new-process")
                        validator {
                            if (it.isNullOrBlank()) error("The name field is required") else null
                        }
                    }//.required()
                }
                field("SequenceChain Name") {
                    textfield(model.file) {
                        addClass("file-field-new-process")
                        setOnMouseClicked {
                            val filters = arrayOf(FileChooser.ExtensionFilter("ITasser Scripts", "*.pl"))
                            val files: List<File> = chooseFile("Select Script File", filters, FileChooserMode.Single)
                            this@textfield.text = files.firstOrNull()?.absolutePath ?: text
                        }
//                        validator {
//                            if (it.isNullOrBlank()) error("The name field is required") else null
//                        }
                    }//.required()

                }
            }
            fieldset("ITASSER params")
        }
    }

    class OptionalParametersModal : View("Opt. Parameters"), Logger {
        val model by inject<NewProcessViewModel>()
        override val root = scrollpane {
            maxHeight = 200.0
            minWidth = 350.0
            form {

                fieldset("Mandatory") {
                    Parameter.values().filter { it != Parameter.SeqName && it != Parameter.PkgDir }.forEach {
                        field(it.name) {
                            textfield {
                                addClass(it.name)
                                promptText = it.str
                                model.arguments[it] = textProperty()
                            }
                        }
                    }
                }
            }

        }
    }

    class NewProcessWizard : Wizard() {
        override val canGoNext = currentPageComplete
        override val canFinish = allPagesComplete

        init {
            add(NewProcessModal::class)
//            add(ProcessesView.MandatoryParametersModal::class)
            add(OptionalParametersModal::class)
        }
    }

    class NewProcess : Controller() {
        val nameProperty = SimpleStringProperty()
        var name by nameProperty
        val fileProperty = SimpleObjectProperty<String>()
        var file by fileProperty

        val arguments = SimpleMapProperty<Parameter, StringProperty>(FXCollections.observableHashMap())
    }

    class NewProcessViewModel(newProcess: NewProcess) : ItemViewModel<NewProcess>(newProcess) {
        var name by bind(NewProcess::nameProperty)
        var file by bind(NewProcess::fileProperty)
        var arguments by bind(NewProcess::arguments)
    }
}

