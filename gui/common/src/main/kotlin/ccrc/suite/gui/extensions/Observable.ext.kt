package ccrc.suite.gui.extensions

import javafx.beans.property.ObjectProperty
import lk.kotlin.observable.property.MutableObservableProperty
import lk.kotlin.observable.property.ObservableProperty
import lk.kotlin.observable.property.plusAssign

fun <T> ObjectProperty<T>.bind(to: MutableObservableProperty<T>) {
    to += {
        this.set(it)
    }
}

fun <T> ObservableProperty<T>.bind(to: ObjectProperty<T>) {
    to.addListener { _, _, new ->
        to.value = new
    }
}

