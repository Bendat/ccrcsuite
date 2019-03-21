package ccrc.suite.gui.views.test.views.processview

import ccrc.suite.commons.logger.Loggable
import ccrc.suite.commons.utils.safeWait
import ccrc.suite.gui.views.ProcessesView
import ccrc.suite.lib.process.ArgNames
import ccrc.suite.lib.process.ProcessManager
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.util.WaitForAsyncUtils
import java.util.concurrent.Callable


class ExploratoryProcessesViewAutomationTest : FxRobot(), Loggable {
    val manager = ProcessManager.FXProcessManager()
    lateinit var primaryStage: Stage
    @get:Synchronized
    lateinit var view: ProcessesView


    @Before
    fun setup() {
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupFixture {
            val stage1 = Stage(StageStyle.UNIFIED)
            view = ProcessesView(manager)
            stage1.scene = Scene(view.root)
            stage1.show()
        }
    }

    @Test
    fun test() {
        Platform.runLater {
            val proc = ProcessesView.NewProcessViewModel().apply {
                name = "Short Lived Process"
                file = javaClass.getResource("/Loop5.pl").file
            }
            val proc2 = ProcessesView.NewProcessViewModel().apply {
                name = "Long lived Process"
                file = javaClass.getResource("/KeepAlive.pl").file
            }
           val proc3 = ProcessesView.NewProcessViewModel().apply {
                name = "Error Process"
                file = javaClass.getResource("/Error.pl").file
            }

            Platform.runLater {
                val new = view.model.newProcess(proc)
                val new2 = view.model.newProcess(proc2)
                val new3 = view.model.newProcess(proc3)
                info { "New result is $new" }
                safeWait(1000)
//                view.model.manager.find(new).map { view.model.run(new) }
//                view.model.manager.find(new2).map { view.model.run(new2) }
                 view.model.run(new3)
            }
            safeWait(1000)
        }
        safeWait(60000)
    }

    private fun args() = listOf("perl", ArgNames.AutoFlush.toString())
}
