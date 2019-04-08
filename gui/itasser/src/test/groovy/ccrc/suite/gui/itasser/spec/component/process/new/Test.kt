package ccrc.suite.gui.itasser.spec.component.process.new

import ccrc.suite.commons.logger.Logger
import ccrc.suite.commons.utils.safeWait
import ccrc.suite.gui.itasser.Styles
import ccrc.suite.gui.itasser.component.process.views.ProcessesView
import ccrc.suite.lib.process.ArgNames
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit.ApplicationTest
import tornadofx.*

class Tests: ApplicationTest(), Logger {
    private var app = TestApp()
    lateinit var primaryStage: Stage
    @Before
    fun setupApplication() {
        primaryStage = FxToolkit.registerPrimaryStage()
        // Stage objects must be constructed and modified on the JavaFX App Thread
        primaryStage.initStyle(StageStyle.UNIFIED)
        FxToolkit.setupFixture {
            app.start(primaryStage)
            primaryStage.show()
            primaryStage.toFront()
        }
    }

    @Test
    fun test(){
        clickOn(".new-process")
//        info{"Root is [${app.root}]"}
//        interact{ app.root.butt.text = "Whoa" }
        safeWait(15000)
    }
    private fun args() = listOf("perl", ArgNames.AutoFlush.toString())
    private class TestApp: App(ProcessesView::class, Styles::class){
        val root by inject<ProcessesView>()
    }
}
