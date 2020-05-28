package ccrc.suite.lib.process.v2.details

import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.logger.Logger

enum class ExitCode(val code: Int, val state: PerlProcess.ExecutionState) : Logger {
    OK(0, PerlProcess.ExecutionState.Completed),
    CtrlC(130, PerlProcess.ExecutionState.Paused),
    SigTerm(143, PerlProcess.ExecutionState.Paused),
    SigKill(9, PerlProcess.ExecutionState.Paused),
    Error(1, PerlProcess.ExecutionState.Failed);

    companion object : Logger {
        fun fromInt(code: Int): ExitCode {
            return values().first { it.code == code }
        }
    }
}