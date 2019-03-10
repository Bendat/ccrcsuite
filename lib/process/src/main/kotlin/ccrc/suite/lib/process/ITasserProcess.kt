package ccrc.suite.lib.process

import java.util.*

data class ITasserProcess(
    override val id: UUID,
    override val args: List<String>,
    override val createdAt: Long,
    override val createdBy: UUID,
    override var state: PerlProcess.ExecutionState,
    override val exitCode: Int
) : PerlProcess