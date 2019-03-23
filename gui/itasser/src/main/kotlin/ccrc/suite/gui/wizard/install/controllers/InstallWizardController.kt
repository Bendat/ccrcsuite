package ccrc.suite.gui.wizard.install.controllers

import javafx.beans.property.SimpleStringProperty
import tornadofx.*
class InstallWizardController {
    val nameProperty = SimpleStringProperty()
    var name by nameProperty

    val emailProperty = SimpleStringProperty()
    var email by emailProperty

    val passwordProperty = SimpleStringProperty()
    var password by passwordProperty

    val repeatPasswordProperty = SimpleStringProperty()
    var repeatPassword by repeatPasswordProperty


}