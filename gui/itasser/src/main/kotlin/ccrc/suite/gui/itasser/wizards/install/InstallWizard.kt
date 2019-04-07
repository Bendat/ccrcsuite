package ccrc.suite.gui.itasser.wizards.install

import ccrc.suite.gui.itasser.wizards.install.controllers.InstallWizardController
import ccrc.suite.gui.itasser.wizards.install.viewmodels.InstallWizardViewModel
import ccrc.suite.gui.itasser.wizards.install.views.ITasserSetupPage
import ccrc.suite.gui.itasser.wizards.install.views.RegistrationPage
import tornadofx.Wizard

class InstallWizard : Wizard() {
    override val canFinish = allPagesComplete
    override val canGoNext = currentPageComplete

    private val controller = InstallWizardController()
    val model by inject<InstallWizardViewModel>(params = mapOf("controller" to controller))

    init {
        add(RegistrationPage::class)
        add(ITasserSetupPage::class)
    }
}