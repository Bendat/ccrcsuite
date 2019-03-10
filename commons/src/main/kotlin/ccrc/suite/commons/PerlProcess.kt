package ccrc.suite.commons

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