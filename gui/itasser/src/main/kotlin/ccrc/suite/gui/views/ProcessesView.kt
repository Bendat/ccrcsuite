@file:Suppress("RedundantLambdaArrow")

package ccrc.suite.gui.views

import ccrc.suite.commons.Parameter
import ccrc.suite.commons.logger.Loggable
import ccrc.suite.gui.controllers.ProcessViewController
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

class ProcessesView(manager: ProcessManager) : View("Process List"), Loggable {
    val model = ProcessViewController(manager)

    constructor() : this(ProcessManager())

    override val root = vbox {
        borderpane {
            left {
                button("+") {
                    addClass("new-process")
                    action { text = "HI" }
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

    class NewProcessModal : View("Setup"), Loggable {
        val proc = find<ProcessesView>()
        val model by inject<NewProcessViewModel>()

        init {
            info { "proc is  $proc" }
            info { "model is $model" }
        }

        override val root = form {
            fieldset("Sequence Info") {
                field("name") {
                    textfield {
                        addClass("name-field-new-process")
                        model.nameProperty.bindBidirectional(textProperty())
                    }
                }
                field("File") {
                    textfield {
                        addClass("file-field-new-process")
                        textProperty().bindBidirectional(model.fileProperty)
                        setOnMouseClicked {
                            val filters = arrayOf(FileChooser.ExtensionFilter("ITasser Scripts", "*.pl"))
                            val files: List<File> = chooseFile("Select Script File", filters, FileChooserMode.Single)
                            this@textfield.text = files.firstOrNull()?.absolutePath ?: text

                        }
                    }

                }
            }
            fieldset("ITASSER params")
        }
    }

    class MandatoryParametersModal : View("Parameters"), Loggable {
        val model by inject<NewProcessViewModel>()
        override val root = form {
            fieldset("Mandatory") {
                field(Parameter.PkgDir.name) {
                    textfield {
                        promptText = Parameter.PkgDir.str
                        model.arguments[Parameter.PkgDir] = textProperty()
                    }
                }
                field(Parameter.SeqName.name) {
                    textfield {
                        promptText = Parameter.SeqName.str
                        model.arguments[Parameter.SeqName] = textProperty()
                    }
                }
            }
        }
    }

    class OptionalParametersModal : View("Opt. Parameters"), Loggable {
        val model by inject<NewProcessViewModel>()
        override val root = vbox {
            maxHeight = 400.0
            form {
                scrollpane {
                    fieldset("Mandatory") {
                        Parameter.values().filter { it != Parameter.SeqName && it != Parameter.PkgDir }.forEach {
                            field {
                                textfield(it.name) {
                                    promptText = it.str
                                    model.arguments[it] = textProperty()
                                }
                            }
                        }
                    }
                }
            }
            label("Leave empty for defaults") { }
        }
    }

    class NewProcessWizard : Wizard() {
        override val canGoNext = currentPageComplete
        override val canFinish = allPagesComplete

        init {
            add(ProcessesView.NewProcessModal::class)
            add(ProcessesView.MandatoryParametersModal::class)
            add(ProcessesView.OptionalParametersModal::class)
        }
    }

    class NewProcessViewModel : ViewModel() {
        val nameProperty = SimpleStringProperty()
        var name by nameProperty
        val fileProperty = SimpleObjectProperty<String>()
        var file by fileProperty

        val arguments = SimpleMapProperty<Parameter, StringProperty>(FXCollections.observableHashMap())
    }
}

