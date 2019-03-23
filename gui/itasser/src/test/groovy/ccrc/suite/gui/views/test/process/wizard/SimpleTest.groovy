package ccrc.suite.gui.views.test.process.wizard

import ccrc.suite.gui.views.test.components.process.WizardApp
import ccrc.suite.gui.wizard.install.InstallWizard
import com.github.javafaker.Faker
import groovy.test.GroovyAssert
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec
import org.testfx.matcher.base.NodeMatchers
import com.winterbe.expekt.*
import static ccrc.suite.commons.utils.SafeWaitKt.safeWait

class SimpleTest extends ApplicationSpec {
    InstallWizard view = null
    def app = new WizardApp()
    Stage stage

    private def faker = new Faker()
    private String password

    @Override
    void start(Stage stage) {
        println("Stage is ${this.stage}")
        if (this.stage == null) {
            FxToolkit.registerPrimaryStage()
            FxToolkit.setupFixture {
                this.stage = new Stage(StageStyle.UNIFIED)
                app.start(stage)
                view = new InstallWizard()
                stage.scene = new Scene(view.root)
                stage.show()
            }
        }
    }

    @Override
    void stop() throws Exception {
        FxToolkit.hideStage()
    }

    void "should wait a long time"() {
        given:
        safeWait(1000)
        password = faker.internet().password()
        when:
        def finish = lookup(".button").nth(1).queryButton()

        FxAssert.verifyThat(finish, NodeMatchers.isDisabled())
        clickOn(".name").write(faker.name().fullName())
        clickOn(".email").write(faker.internet().emailAddress())
        FxAssert.verifyThat(finish, NodeMatchers.isDisabled())


        FxAssert.verifyThat(finish, NodeMatchers.isEnabled())

//        clickOn(".email").write(faker.internet().emailAddress())
//        clickOn(".password").write(password)
//        clickOn(".password-repeat").write(password)
        then:
        safeWait(15000)
    }

}
