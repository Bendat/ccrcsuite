package ccrc.suite.gui.itasser

import ccrc.suite.gui.itasser.component.main.views.MainView
import javafx.application.Application
import tornadofx.App

class MyApp: App(MainView::class, Styles::class)

/**
 * The main method is needed to support the mvn jfx:run goal.
 */
fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}