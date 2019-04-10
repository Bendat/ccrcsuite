package ccrc.suite.gui.itasser.spec.install.wizard

import ccrc.suite.gui.itasser.test.apps.WizardApp
import com.github.javafaker.Faker
import javafx.scene.control.Button
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.matcher.base.NodeMatchers
import static ccrc.suite.commons.utils.SafeWaitKt.safeWait

class ITasserSetupPageTestSpec extends GuiSpec {
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
        }
    }


    @Override
    void stop() throws Exception {
        FxToolkit.hideStage()
        app.stop()
    }

    void "should fail to proceed due to bad pkgdir"() {
        given:
        finish = lookup(".button").nth(1).queryButton()
        next = lookup(".button").nth(2).queryButton()
        password = getPassword()
        proceedToSetup(password)
        FxAssert.verifyThat(next, NodeMatchers.isEnabled())
        def pkg = new File(testDir, "runI-TASSER.pl")
        pkg.createNewFile()
        pkg.deleteOnExit()
        safeWait(200)

        when:
        clickOn(next)
        clickOn(".pkgdir").write("/badpackage")
        clickOn(".datadir").write(testDir.absolutePath)
        clickOn(".libdir").write(testDir.absolutePath)
        clickOn(".java_home ").write(testDir.absolutePath)

        then:
        safeWait(500)
        FxAssert.verifyThat(finish, NodeMatchers.isDisabled())
        clickOn(finish)
        safeWait(500)
    }


    void "should fail to proceed due to bad libdir"() {
        given:
        finish = lookup(".button").nth(1).queryButton()
        next = lookup(".button").nth(2).queryButton()
        password = getPassword()
        proceedToSetup(password)
        FxAssert.verifyThat(next, NodeMatchers.isEnabled())
        def pkg = new File(testDir, "runI-TASSER.pl")
        pkg.createNewFile()
        pkg.deleteOnExit()
        safeWait(500)

        when:
        clickOn(next)
        clickOn(".pkgdir").write(testDir.absolutePath)
        clickOn(".datadir").write(testDir.absolutePath)
        clickOn(".libdir").write("/badpackage")
        clickOn(".java_home ").write(testDir.absolutePath)

        then:
        safeWait(500)
        FxAssert.verifyThat(finish, NodeMatchers.isDisabled())
        clickOn(finish)
        safeWait(500)
    }


    void "should fail to proceed due to bad datadir"() {
        given:
        finish = lookup(".button").nth(1).queryButton()
        next = lookup(".button").nth(2).queryButton()
        password = getPassword()
        proceedToSetup(password)
        safeWait(200)
        FxAssert.verifyThat(next, NodeMatchers.isEnabled())
        def pkg = new File(testDir, "runI-TASSER.pl")
        pkg.createNewFile()
        pkg.deleteOnExit()
        safeWait(200)

        when:
        clickOn(next)
        clickOn(".pkgdir").write(testDir.absolutePath)
        clickOn(".datadir").write("/badpackage")
        clickOn(".libdir").write(testDir.absolutePath)
        clickOn(".java_home ").write(testDir.absolutePath)

        then:
        safeWait(500)
        FxAssert.verifyThat(finish, NodeMatchers.isDisabled())
        clickOn(finish)
        safeWait(500)
    }


    void "should fail to proceed due to bad java_home"() {
        given:
        finish = lookup(".button").nth(1).queryButton()
        next = lookup(".button").nth(2).queryButton()
        password = getPassword()
        proceedToSetup(password)
        safeWait(200)
        FxAssert.verifyThat(next, NodeMatchers.isEnabled())
        def pkg = new File(testDir, "runI-TASSER.pl")
        pkg.createNewFile()
        pkg.deleteOnExit()
        safeWait(200)

        when:
        clickOn(next)
        clickOn(".pkgdir").write(testDir.absolutePath)
        clickOn(".datadir").write(testDir.absolutePath)
        clickOn(".libdir").write(testDir.absolutePath)
        clickOn(".java_home ").write("/badpackage")

        then:
        safeWait(500)
        FxAssert.verifyThat(finish, NodeMatchers.isDisabled())
        clickOn(finish)
        safeWait(500)
    }

    void "should fail to proceed due to lack of itasser script"() {
        given:
        finish = lookup(".button").nth(1).queryButton()
        next = lookup(".button").nth(2).queryButton()
        password = getPassword()
        proceedToSetup(password)
        safeWait(200)
        FxAssert.verifyThat(next, NodeMatchers.isEnabled())
        safeWait(200)

        when:
        clickOn(next)
        clickOn(".pkgdir").write(testDir.absolutePath)
        clickOn(".datadir").write(testDir.absolutePath)
        clickOn(".libdir").write(testDir.absolutePath)
        clickOn(".java_home ").write("/badpackage")

        then:
        safeWait(500)
        FxAssert.verifyThat(finish, NodeMatchers.isDisabled())
        clickOn(finish)
        safeWait(500)
    }


    void "should validate to progression"() {
        given:
        finish = lookup(".button").nth(1).queryButton()
        next = lookup(".button").nth(2).queryButton()
        password = getPassword()
        proceedToSetup(password)
        FxAssert.verifyThat(next, NodeMatchers.isEnabled())
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
        FxAssert.verifyThat(finish, NodeMatchers.isEnabled())
        clickOn(finish)
        safeWait(500)
    }


    private String getPassword() {
        String p = faker.internet().password(8, 10, true, true)
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