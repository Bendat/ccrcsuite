package ccrc.suite.gui.wizard.install.viewmodels

import ccrc.suite.gui.wizard.install.controllers.InstallWizardController
import tornadofx.ItemViewModel

class InstallWizardViewModel:
    ItemViewModel<InstallWizardController>(InstallWizardController()) {

    val name = bind(InstallWizardController::nameProperty, autocommit = true)
    val email  = bind(InstallWizardController::emailProperty, autocommit = true)
    val password = bind(InstallWizardController::passwordProperty, autocommit = true)
    val passwordRepeat = bind(InstallWizardController::repeatPasswordProperty, autocommit = true)

    val pkgDir = bind(InstallWizardController::pkgDirProperty, autocommit = true)
    val libDir = bind(InstallWizardController::libDirProperty, autocommit = true)
    val javaHome = bind(InstallWizardController::javaHomeProperty, autocommit = true)
    val dataDir = bind(InstallWizardController::dataDirProperty)
    val runStyle = bind(InstallWizardController::runStyleProperty, autocommit = true)
}