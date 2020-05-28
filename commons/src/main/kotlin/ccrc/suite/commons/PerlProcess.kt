package ccrc.suite.commons

import ccrc.suite.commons.logger.Logger
import java.io.File
import java.util.*

interface PerlProcess {
    val id: UUID
    val name: String
    val args: List<String>
    val createdAt: Long
    val createdBy: UUID
    var state: ExecutionState
    val seq: File

    sealed class ExecutionState {
        object Completed : ExecutionState()
        object Paused : ExecutionState()
        object Running : ExecutionState()
        object Queued : ExecutionState()
        object Failed : ExecutionState()

        val isRunnable get() = this == Queued
    }

    enum class ExitCode(val code: Int, val state: ExecutionState) : Logger {
        OK(0, ExecutionState.Completed),
        CtrlC(130, ExecutionState.Paused),
        SigTerm(143, ExecutionState.Paused),
        SigKill(9, ExecutionState.Paused),
        Error(1, ExecutionState.Failed);

        companion object : Logger {
            fun fromInt(code: Int): ExitCode {
                return values().first { it.code == code }.also {
                    info { "Selecting ExitCode [$it] for [$code]" }
                }
            }
        }
    }
}