package ccrc.suite.gui.itasser.spec.component.console

import ccrc.suite.gui.itasser.component.console.controllers.ProcessConsoleViewController
import ccrc.suite.gui.itasser.spec.install.wizard.GuiSpec
import ccrc.suite.gui.itasser.test.apps.ConsoleApp
import com.github.javafaker.Faker
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.testfx.api.FxToolkit
import static ccrc.suite.commons.utils.SafeWaitKt.safeWait

class ConsoleSpec extends GuiSpec {
    ConsoleApp app
    Stage stage
    Faker faker = new Faker()

    @Override
    void start(Stage stage) throws Exception {
        app = new ConsoleApp()
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

    void "We verify that output is correctly written to "() {
        given:
        def file = this.class.getResource("/Lorem.pl").file
        def args = new ArrayList<String>()
        def controller = new ProcessConsoleViewController()
        controller.text.item.add("Hello")
        args += file
        def process
        
        interact {
            app.model.item = controller
            process = app.pm.new(
                    UUID.randomUUID(),
                    0,
                    new File(file),
                    "Test Process",
                    args,
                    UUID.randomUUID()
            )
        }

        when:
        interact {
            app.pm.run(process)
            def proc = app.pm.get(process)
            proc.map { controller.process = it.runner }
        }

        then:
        safeWait(1000)
        interact { app.model.text.size() > 100 }
    }
}

