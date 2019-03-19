package ccrc.suite.gui.views.test.views.processview

import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.PerlProcess.ExecutionState.*
import ccrc.suite.commons.logger.Loggable
import ccrc.suite.commons.utils.safeWait
import ccrc.suite.commons.utils.uuid
import ccrc.suite.gui.views.ProcessesView
import ccrc.suite.lib.process.ArgNames
import ccrc.suite.lib.process.ProcessManager
import ccrc.suite.lib.process.Wrapper
import com.winterbe.expekt.should
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import java.io.File

class ExploratoryProcessesViewAutomationTest : ApplicationTest(), Loggable {
    val primaryStage: Stage = FxToolkit.registerPrimaryStage()
    val manager = ProcessManager.FXProcessManager()
    @get:Synchronized
    val list = ProcessesView(manager)
    val file = javaClass.getResource("/KeepAlive.pl").file

    override fun init() {
        Platform.runLater {
            primaryStage.initStyle(StageStyle.UNIFIED)
            primaryStage.scene = Scene(list.root)
            super.start(primaryStage)
            primaryStage.show()
        }
        safeWait(1000)
    }

    @Test
    fun `verifying listview`() {

        val procs = PerlProcess.ExecutionState.values().map {
            info { "value of [$it]" }
            newProcess(0) { it.name }
        }
        safeWait(1000)
        manager[0, Completed] = procs[0]
        manager[0, Running] = procs[1]
        manager[0, Queued] = procs[2]
        manager[0, Paused] = procs[3]
        manager[0, Failed] = procs[4]

        safeWait(100000)
        info { "Lookup is: ${lookup(".Started")}" }
        lookup(".Started").queryListView<Wrapper>()
            .items.size.should.equal(1)
        lookup(".Failed").queryListView<Wrapper>()
            .items.size.should.equal(1)

    }

//    @Test
//    fun `executing test process`() {
//       val id = newProcess()
//        safeWait(1000)
//        val run = manager.run(id)
//        run.toEither { }.mapLeft { it.should.be.`null` }
//            .map {it.await()}
//        info { "things are [${list.things.map { it.items }}]" }
//        manager.size.should.equal(1)
//        safeWait(5000)
//        manager[Completed].size.should.equal(1)
//        safeWait(1000)
//        info{list.things.map { it.items.size }.sum()}
//        list.things.map { it.items.size }.sum().should.equal(1)
//        safeWait(5000)

//    }

    private fun newProcess(
        priority: Int, state:
        PerlProcess.ExecutionState = PerlProcess.ExecutionState.Running, name: () -> String
    ) =
        manager.new(0, File(file), name(), args(), uuid)

    private fun args() = listOf("perl", ArgNames.AutoFlush.toString())
}
