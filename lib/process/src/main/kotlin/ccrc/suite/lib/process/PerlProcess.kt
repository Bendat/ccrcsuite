package ccrc.suite.lib.process

import java.util.*

interface PerlProcess {
    val id: UUID
    val args: List<String>
    val createdAt: Long
    val createdBy: UUID
    var state: ExecutionState
    val exitCode: Int

    enum class ExecutionState {
        Completed,
        Paused,
        Running,
        Failed,
        Queued
    }
}