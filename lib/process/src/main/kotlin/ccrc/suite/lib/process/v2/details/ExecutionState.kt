package ccrc.suite.lib.process.v2.details

sealed class ExecutionState {
    object Completed : ExecutionState()
    object Paused : ExecutionState()
    object Running : ExecutionState()
    object Queued : ExecutionState()
    object Failed : ExecutionState()

    val isRunnable get() = (this == Queued) or
            (this == Paused)
}