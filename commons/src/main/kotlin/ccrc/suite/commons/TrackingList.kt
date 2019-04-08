package ccrc.suite.commons

import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import org.joda.time.DateTime

class TrackingList<T>: SimpleListProperty<TrackedItem<T>>(FXCollections.observableArrayList()) {
    fun add(item: T){
        add(TrackedItem(item))
    }

    operator fun plusAssign(item: T) = add(item)
    override fun toString(): String {
        val str = joinToString (separator = ", ")
        return "TrackingList{$str})"
    }

}
data class TrackedItem<T>(
    val item: T,
    val timestamp: DateTime = DateTime.now()
){
    override fun toString(): String {
        return "{$timestamp: $item}"
    }
}