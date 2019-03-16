package ccrc.suite.lib.process

import ccrc.suite.commons.PerlProcess
import org.dizitart.no2.objects.Id
import java.io.File
import java.util.*
data class ITasserProcess(
    @Id override val id: UUID,
    override val seq: File,
    override val name: String,
    override val args: List<String>,
    override val createdAt: Long,
    override val createdBy: UUID,
    override var state: PerlProcess.ExecutionState
) : PerlProcess{
    override fun equals(other: Any?): Boolean {
        return when(other){
            !is PerlProcess -> false
            else -> id == other.id
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + createdBy.hashCode()
        return result
    }

    override fun toString(): String {
        return "[$name][${seq.absolutePath}][$id]"
    }
}