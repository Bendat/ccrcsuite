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


    val pkgDirProperty = SimpleStringProperty()
    val pkgDir by pkgDirProperty

    val libDirProperty = SimpleStringProperty()
    var libDir by libDirProperty

    val javaHomeProperty = SimpleStringProperty()
    var javaHome by javaHomeProperty

    val dataDirProperty = SimpleStringProperty()
    var dataDir by dataDirProperty

    val runStyleProperty = SimpleStringProperty()
    val runStyle by runStyleProperty
}