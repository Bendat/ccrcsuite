package ccrc.suite.gui.itasser.test.viewmodels.wizard.new

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import ccrc.suite.commons.User
import ccrc.suite.commons.extensions.type
import ccrc.suite.commons.hasheduser
import ccrc.suite.commons.logger.KLog
import ccrc.suite.gui.itasser.wizards.install.controllers.InstallWizardController
import ccrc.suite.lib.store.database.Database
import com.github.javafaker.Faker
import com.winterbe.expekt.should
import org.dizitart.no2.WriteResult
import org.mindrot.jbcrypt.BCrypt
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature


class StorageTests : Spek({
    val faker = Faker()
    val log = KLog(StorageTests::class)
    Feature("Storing Admin User") {
        val db by memoized { Database.MemoryDatabase() }
        val controller by memoized {
            InstallWizardController().apply {
                dataDir = faker.file().fileName()
                javaHome = faker.file().fileName()
                libDir = faker.file().fileName()
                pkgDir = faker.file().fileName()
                runStyle = "parallel"
                password = "1234"
                name = faker.name().username()
                repeatPassword = password
                email = faker.internet().emailAddress()
            }
        }

        Scenario("We save the admin user to the database") {
            lateinit var user: User
            lateinit var hashedUser: User
            lateinit var userWrite: Option<WriteResult>
            lateinit var retrievedUser: Option<User>
            Given("The controllers user") {
                user = controller.toUser()
            }

            And("The same user with a BCrypt hashed password") {
                hashedUser = hasheduser(user)
            }

            When("We save the user") {
                userWrite = db.create(hashedUser)
            }

            Then("The write result should be successful") {
                log.info { userWrite }
                userWrite.should.be.of.type<Some<WriteResult>>()
            }

            Given("We retrieved user") {
                retrievedUser = db.findFirst()
                log.info { retrievedUser }
            }

            When("User is not $None") {
                retrievedUser.should.be.of.type<Some<User>>()
            }

            Then("The password Hashes should match") {
                retrievedUser.map { user ->
                    log.info { "Controller password is [${controller.toUser().password.value}]" }
                    log.info { "Retrieved password is [${user.password.value}]" }
                    val x: String = controller.toUser().password.value
                    val y: String = user.password.value

                    BCrypt.checkpw(x, y).should.be.`true`
                }
            }

        }

        Scenario("We save the settings to the database") {
            lateinit var settingsResult: Option<WriteResult>
            Given("We save controllers settings values") {
                settingsResult = db.create(controller.toSettings())
            }

            Then("The write result was successful") {
                settingsResult.should.not.be.of.type<None>()
            }

        }
    }
})