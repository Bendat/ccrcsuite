package ccrc.suite.gui.wizard.install

import ccrc.suite.gui.wizard.install.controllers.InstallWizardController
import ccrc.suite.gui.wizard.install.viewmodels.InstallWizardViewModel
import ccrc.suite.gui.wizard.install.views.RegistrationPage
import tornadofx.Wizard

class InstallWizard : Wizard() {
    override val canFinish = allPagesComplete
    override val canGoNext = currentPageComplete

    val controller = InstallWizardController()
    val model by inject<InstallWizardViewModel>(params = mapOf("controller" to controller))

    init {
        add(RegistrationPage::class)
    }
}