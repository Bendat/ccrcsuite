package ccrc.suite.gui.itasser.spec.install.wizard

import arrow.core.Either
import ccrc.suite.gui.itasser.settings.GuiDB
import ccrc.suite.gui.itasser.test.apps.WizardApp
import com.github.javafaker.Faker
import javafx.scene.control.Button
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.testfx.api.FxToolkit

import static ccrc.suite.commons.utils.SafeWaitKt.safeWait
import static org.testfx.assertions.api.Assertions.assertThat

class StorageTest extends GuiSpec {
    WizardApp app = null
    Stage stage
    Button finish
    Button next
    String password
    File testDir = new File(new File(System.getProperty("user.home")), "ccrc-test")
    private def faker = new Faker()

    @Override
    void start(Stage stage) {
        app = new WizardApp()
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupFixture {
            this.stage = new Stage(StageStyle.UNIFIED)
            app.start(stage)
            stage.show()
            app.dbStart()
        }
    }

    void "should successfully save the install wizard to settings"() {
        given:
        finish = lookup(".button").nth(1).queryButton()
        next = lookup(".button").nth(2).queryButton()
        password = getPassword()
        proceedToSetup(password)
        assertThat(next).isEnabled()

//        FxAssert.verifyThat(next, NodeMatchers.isEnabled())
        def pkg = new File(testDir, "runI-TASSER.pl")
        pkg.createNewFile()
        pkg.deleteOnExit()
        safeWait(200)

        when:
        clickOn(next)
        clickOn(".pkgdir").write(testDir.absolutePath)
        clickOn(".datadir").write(testDir.absolutePath)
        clickOn(".libdir").write(testDir.absolutePath)
        clickOn(".java_home ").write(testDir.absolutePath)

        then:
        safeWait(500)
        assertThat(finish).isEnabled()
//        FxAssert.verifyThat(finish, NodeMatchers.isEnabled())
        clickOn(finish)
        safeWait(500)

        interact {
            when:
            app.root.model.dataDir != null
            def dbRes = app.dbStart()
            def model = app.root.model.save(GuiDB.db)

            expect:
            dbRes as Either.Right
            model as Either.Right
        }
    }

    private String getPassword() {
        String p = faker.internet().password(8, 10, true, true)
        p += "tT%"
        info("Using password [$p]")
        return p
    }

    private void proceedToSetup(String password) {
        clickOn(".name").write(faker.name().fullName())
        clickOn(".email").write(faker.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)
        safeWait(500)
    }
}

