package ccrc.suite.commons

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.util.*

interface PerlProcess {
    val id: UUID
    val args: List<String>
    val createdAt: Long
    val createdBy: UUID
    var state: ExecutionState

    enum class ExecutionState {
        Completed,
        Paused,
        Running,
        Failed,
        Queued
    }
}