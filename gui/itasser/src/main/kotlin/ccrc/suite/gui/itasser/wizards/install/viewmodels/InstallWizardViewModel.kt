package ccrc.suite.gui.itasser.wizards.install.viewmodels

import arrow.core.*
import ccrc.suite.commons.EmptyOption
import ccrc.suite.commons.Error
import ccrc.suite.gui.itasser.settings.GuiDB
import ccrc.suite.gui.itasser.wizards.install.controllers.InstallWizardController
import ccrc.suite.lib.store.database.Database
import org.dizitart.no2.WriteResult
import tornadofx.ItemViewModel

private typealias SettingsUserResult = Pair<Option<WriteResult>, Option<WriteResult>>

class InstallWizardViewModel :
    ItemViewModel<InstallWizardController>(InstallWizardController()) {

    val name = bind(InstallWizardController::nameProperty, autocommit = true)
    val email = bind(InstallWizardController::emailProperty, autocommit = true)
    val password = bind(InstallWizardController::passwordProperty, autocommit = true)
    val passwordRepeat = bind(InstallWizardController::repeatPasswordProperty, autocommit = true)

    val pkgDir = bind(InstallWizardController::pkgDirProperty, autocommit = true)
    val libDir = bind(InstallWizardController::libDirProperty, autocommit = true)
    val javaHome = bind(InstallWizardController::javaHomeProperty, autocommit = true)
    val dataDir = bind(InstallWizardController::dataDirProperty, autocommit = true)
    val runStyle = bind(InstallWizardController::runStyleProperty, autocommit = true)

    fun save(db: Option<Database> = GuiDB.db): Either<Error, SettingsUserResult> {
        return when (db) {
            is None -> Left(EmptyOption("Database object is of type [$None]"))
            is Some -> Right(db.t.create(toSettings()) to db.t.create(toUser()))
        }
    }

    fun toUser() = item.toUser()
    fun toSettings() = item.toSettings()
}