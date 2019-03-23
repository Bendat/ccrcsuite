package ccrc.suite.gui.views.test.components.process.view

import javafx.scene.control.Button
import tornadofx.*
import tornadofx.vbox

class TestView: View() {
    lateinit var butt: Button
    override val root = vbox{
        butt = button("Hi"){
            addClass("hi")
            setOnAction {
                Modal().openModal()
            }
        }
    }
}

class Modal: View() {
    override val root = button("Yo")
}