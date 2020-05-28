package ccrc.suite.lib.process

import ccrc.suite.commons.PerlProcess
import javafx.beans.property.SimpleObjectProperty
import org.dizitart.no2.objects.Id
import tornadofx.getValue
import tornadofx.setValue
import java.io.File
import java.util.*

interface ReactiveProcess: PerlProcess{
    val stateProperty:  SimpleObjectProperty<PerlProcess.ExecutionState>
}

data class ITasserProcess(
    @Id override val id: UUID,
    override val seq: File,
    override val name: String,
    override val args: List<String>,
    override val createdAt: Long,
    override val createdBy: UUID
) : ReactiveProcess {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            !is PerlProcess -> false
            else -> id == other.id
        }
    }

    constructor(
        id: UUID,
        seq: File,
        name: String,
        args: List<String>,
        createdAt: Long,
        createdBy: UUID,
        state: PerlProcess.ExecutionState
    ) : this(id, seq, name, args, createdAt, createdBy){
        this.state = state
    }

    override val stateProperty = SimpleObjectProperty<PerlProcess.ExecutionState>()
    override var state by stateProperty

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + createdBy.hashCode()
        return result
    }

    override fun toString(): String {
        return "[$name][${seq.absolutePath}][$id]"
    }

}