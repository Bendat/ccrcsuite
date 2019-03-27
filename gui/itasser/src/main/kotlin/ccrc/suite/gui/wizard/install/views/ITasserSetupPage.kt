package ccrc.suite.gui.wizard.install.views

import ccrc.suite.gui.wizard.install.viewmodels.InstallWizardViewModel
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Parent
import tornadofx.*
import java.io.File

fun ValidationContext.validateDirectory(file: File): ValidationMessage? {
    return if (!file.exists()) error("Directory does not exist")
    else if (!file.isDirectory) error("Must be a directory")
    else null
}

class ITasserSetupPage : View("ITasser setup") {
    val model by inject<InstallWizardViewModel>()
    override val complete = model.valid(model.pkgDir, model.libDir, model.javaHome, model.dataDir)
    private val nullMessage get() = "Must not be empty"
    override val root: Parent = form() {
        fieldset("ITasser Parameters", labelPosition = Orientation.VERTICAL) {
            field("Package Dir") {
                textfield(model.pkgDir) {
                    addClass("pkgdir")
                    promptText = "Directory containing the runITASSER.pl script"
                    validator {
                        if (text == null) {
                            error(nullMessage)
                        } else {
                            val chosen = File(text)
                            val script = File(chosen, "runI-TASSER.pl")
                            validateDirectory(chosen)
                                ?: if (!script.exists())
                                    error("Could not find 'runI-TASSER.pl'")
                                else null
                        }

                    }
                }
            }


            field("Lib Dir") {
                textfield(model.libDir) {
                    addClass("libdir")
                    promptText = "Directory of the ITASSER template library"
                    validator {
                        if (text == null) {
                            error(nullMessage)
                        } else {
                            val chosen = File(text)
                            validateDirectory(chosen)
                        }
                    }
                }
            }
            field("Data Dir") {
                textfield(model.dataDir) {
                    addClass("datadir")
                    promptText = "Directory where datadirs will be created for sequences"
                    validator {
                        if (text == null) {
                            error(nullMessage)
                        } else {
                            val chosen = File(text)
                            validateDirectory(chosen)
                        }
                    }
                }
            }
            field("Java Home") {
                textfield(model.javaHome) {
                    addClass("java_home")
                    promptText = "The directory containing bin/java"

                    validator {
                        if (text == null) {
                            error(nullMessage)
                        } else {
                            val chosen = File(text)
                            validateDirectory(chosen)
                        }
                    }
                }
            }

            field("Run Style") {
                val items = FXCollections.observableArrayList(
                    arrayListOf("serial", "parallel", "gnuparallel")
                )
                model.runStyle.value = "gnuparallel"
                combobox<String>(model.runStyle, items) {
                    validator {
                        if(text == null){
                            error(nullMessage)
                        }
                        else{
                            if (this@combobox.selectedItem.isNullOrBlank())
                                error("Combobox must be Selected")
                            else null
                        }
                    }

                }
            }
        }

    }
}