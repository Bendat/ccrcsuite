package ccrc.suite.gui.itasser.wizards.install.controllers

import ccrc.suite.commons.EmailAddress
import ccrc.suite.commons.Password
import ccrc.suite.commons.User
import ccrc.suite.commons.Username
import ccrc.suite.commons.logger.Logger
import ccrc.suite.gui.itasser.settings.Settings
import javafx.beans.property.SimpleStringProperty
import tornadofx.getValue
import tornadofx.setValue
import java.io.File

class InstallWizardController: Logger {
    val nameProperty = SimpleStringProperty()
    var name by nameProperty

    val emailProperty = SimpleStringProperty()
    var email by emailProperty

    val passwordProperty = SimpleStringProperty()
    var password by passwordProperty

    val repeatPasswordProperty = SimpleStringProperty()
    var repeatPassword by repeatPasswordProperty


    val pkgDirProperty = SimpleStringProperty()
    var pkgDir by pkgDirProperty

    val libDirProperty = SimpleStringProperty()
    var libDir by libDirProperty

    val javaHomeProperty = SimpleStringProperty()
    var javaHome by javaHomeProperty

    val dataDirProperty = SimpleStringProperty()
    var dataDir by dataDirProperty

    val runStyleProperty = SimpleStringProperty()
    var runStyle by runStyleProperty

    fun toSettings(): Settings {
        info{"datadir is [$dataDir]"}
        return Settings(
            pkgDir = File(pkgDir),
            libDir = File(libDir),
            javaHome = File(javaHome),
            runStyle = runStyle,
            dataDIr = File(dataDir)
        )
    }

    fun toUser(): User {
        return User(Username(name), Password(password), EmailAddress(email))
    }
}