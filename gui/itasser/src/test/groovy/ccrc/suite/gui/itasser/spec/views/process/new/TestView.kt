package ccrc.suite.gui.itasser.spec.views.process.new

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