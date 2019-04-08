package ccrc.suite.gui.itasser.test.apps

import arrow.core.Either
import ccrc.suite.commons.*
import ccrc.suite.commons.logger.Logger
import ccrc.suite.gui.itasser.Styles
import ccrc.suite.gui.itasser.component.console.viewmodels.ProcessConsoleViewViewModel
import ccrc.suite.gui.itasser.component.console.views.ProcessConsoleView
import ccrc.suite.gui.itasser.settings.GuiDB
import ccrc.suite.gui.itasser.wizards.install.InstallWizard
import ccrc.suite.lib.process.ProcessManager
import ccrc.suite.lib.store.database.DBError
import ccrc.suite.lib.store.database.Database
import com.github.javafaker.Faker
import tornadofx.App
import tornadofx.UIComponent
import java.io.File

class TestApp<T : UIComponent>(type: Class<T>) : App(type.kotlin, Styles::class)

class WizardApp : App(InstallWizard::class), Logger {
    val root by inject<InstallWizard>()
    val db by lazy { GuiDB }

    fun dbStart(): Either<TrackingList<DBError>, Database> {
        val faker = Faker()

        val name = Username(faker.name().fullName())
        val password = Password(faker.internet().password(8, 9, true, true) + ">tT")
        val email = EmailAddress(faker.internet().emailAddress())
        val user = User(name, password, email)

        val dir = File(System.getProperty("user.home"))
        val testDir = File(dir, "ccrc-test")

        return db.login(testDir, "testdb.db", user).also{
            info{"Database initialization was [$it]"}
        }
    }
}

class ConsoleApp: App(ProcessConsoleView::class){
    val pm = ProcessManager()
    val model by inject<ProcessConsoleViewViewModel>()
}