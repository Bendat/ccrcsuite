package ccrc.suite.commons

import ccrc.suite.commons.logger.Loggable
import java.awt.SystemColor.info
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

    enum class ExecutionState {
        Completed,
        Paused,
        Running,
        Queued,
        Failed;

        val isRunnable get() = this == Queued
    }

    enum class ExitCode(val code: Int, val state: ExecutionState) : Loggable{
        OK(0, ExecutionState.Completed),
        CtrlC(130, ExecutionState.Paused),
        SigTerm(143, ExecutionState.Paused),
        SigKill(9, ExecutionState.Paused),
        Error(1, ExecutionState.Failed);

        companion object: Loggable {
            fun fromInt(code: Int): ExitCode {
                return values().first { it.code == code }.also{
                    info{"Selecting ExitCode [$it] for [$code]"}
                }
            }
        }
    }
}