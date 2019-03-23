package ccrc.suite.gui.views.test.components.process

import ccrc.suite.gui.Styles
import ccrc.suite.gui.wizard.install.InstallWizard
import tornadofx.App
import tornadofx.UIComponent

class TestApp<T: UIComponent>(type: Class<T>) :
    App(type.kotlin, Styles::class){
}
class WizardApp: App(InstallWizard::class){
    val root by inject<InstallWizard>()
}