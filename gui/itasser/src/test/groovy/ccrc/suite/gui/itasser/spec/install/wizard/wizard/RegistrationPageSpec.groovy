package ccrc.suite.gui.itasser.spec.install.wizard.wizard

import ccrc.suite.gui.itasser.test.apps.WizardApp
import com.github.javafaker.Faker
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec
import org.testfx.matcher.base.NodeMatchers

import static ccrc.suite.commons.utils.SafeWaitKt.safeWait

class RegistrationPageTestSpec extends ApplicationSpec {
    WizardApp app = null
    Stage stage

    private def faker = new Faker()

    @Override
    void start(Stage stage) {
        println("Stage is ${this.stage}")
        app = new WizardApp()
        if (this.stage == null) {
            FxToolkit.registerPrimaryStage()
            FxToolkit.setupFixture {
                this.stage = new Stage(StageStyle.UNIFIED)
                app.start(stage)
                stage.show()
            }
        }
    }

    @Override
    void stop() throws Exception {
        FxToolkit.hideStage()
        app.stop()
    }

    void "should not proceed due to bad name"() {
        given:
        def password = faker.internet().password(8, 10, true, true)
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write("")
        clickOn(".email").write(faker.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        safeWait(500)
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "should not proceed due to bad email"() {
        given:
        def password = faker.internet().password(8, 10, true, true)
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write(faker.name().fullName())
        clickOn(".email").write("bademail")
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        safeWait(500)
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "should not proceed due to bad password"() {
        given:
        def password = faker.internet().password(8, 10, true, true)
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write(faker.name().fullName())
        clickOn(".email").write(faker.internet().emailAddress())
        clickOn(".password").write("nogood")
        clickOn(".password-repeat").write(password)

        then:
        safeWait(500)
        FxAssert.verifyThat(next, NodeMatchers.isDisabled())
    }

    void "should not proceed due to mismatched password"() {
        given:
        def password = faker.internet().password(8, 10, true, true)
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write(faker.name().fullName())
        clickOn(".email").write(faker.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(faker.internet().password())

        then:
        safeWait(500)
        FxAssert.verifyThat(next,  NodeMatchers.isDisabled())
    }

    void "should validate to progression"() {
        given:
        def password = faker.internet().password(8, 10, true, true)
        def next = lookup(".button").nth(2).queryButton()

        when:
        clickOn(".name").write(faker.name().fullName())
        clickOn(".email").write(faker.internet().emailAddress())
        clickOn(".password").write(password)
        clickOn(".password-repeat").write(password)

        then:
        safeWait(15000)
        FxAssert.verifyThat(next, NodeMatchers.isEnabled())
    }

}
