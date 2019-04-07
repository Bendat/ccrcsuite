package ccrc.suite.gui.itasser.spec.views.process.new

import ccrc.suite.commons.utils.safeWait
import ccrc.suite.gui.itasser.views.ProcessesView.NewProcessWizard
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit

class NewProcessSimpleCompletion: FxRobot() {
    lateinit var view: NewProcessWizard

    @Before
    fun setup() {
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupFixture {
            val stage1 = Stage(StageStyle.UNIFIED)
            view = NewProcessWizard()
            stage1.scene = Scene(view.root)
            stage1.show()
        }
    }

//    @Test
//    fun `

    @Test
    fun test() = safeWait(100000)

}