package ccrc.suite.gui.wizard.install.views

import ccrc.suite.commons.logger.Logger
import ccrc.suite.gui.wizard.install.viewmodels.InstallWizardViewModel
import javafx.geometry.Orientation.VERTICAL
import org.apache.commons.validator.routines.EmailValidator
import tornadofx.*

class RegistrationPage : View("Create Admin Account"), Logger {
    val model by inject<InstallWizardViewModel>()
    override val complete = model.valid(model.name)

    override val root = form {
        minWidth = 250.0
        fieldset("Create an Administrator Account", labelPosition = VERTICAL) {
            field("Name") {
                textfield(model.name) {
                    addClass("name")
                    validator {
                        if (text.isNullOrBlank())
                            error()
                        else null
                    }
                }.required(message = "Name cannot be empty.")
            }
            field("Email") {
                textfield(model.email) {
                    addClass("email")
                    validator {
                        val vld = EmailValidator.getInstance()
                        if (vld.isValid(text) or text.isNullOrBlank()) null
                        else error()
                    }
                }.required(message = "Email address is optional but must be valid")
            }
            field("Password") {
                passwordfield(model.password) {
                    addClass("password")
                }
                validator{
                    if(text.isNullOrBlank() or text.length < 6)
                }
            }
            field("Repeat") {
                passwordfield(model.passwordRepeat) {
                    addClass("password-repeat")
                }
            }
        }
    }
}