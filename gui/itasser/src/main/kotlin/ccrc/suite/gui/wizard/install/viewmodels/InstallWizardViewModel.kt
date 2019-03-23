package ccrc.suite.gui.wizard.install.viewmodels

import ccrc.suite.gui.wizard.install.controllers.InstallWizardController
import tornadofx.ItemViewModel

class InstallWizardViewModel() :
    ItemViewModel<InstallWizardController>(InstallWizardController()) {

    val name = bind(InstallWizardController::nameProperty, autocommit = true)
    val email  = bind(InstallWizardController::emailProperty, autocommit = true)
    val password = bind(InstallWizardController::passwordProperty, autocommit = true)
    val passwordRepeat = bind(InstallWizardController::repeatPasswordProperty, autocommit = true)
}