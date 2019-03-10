package ccrc.suite.lib.process

import ccrc.suite.commons.PerlProcess
import org.dizitart.no2.objects.Id
import java.util.*
data class ITasserProcess(
    @Id
    override val id: UUID,
    override val args: List<String>,
    override val createdAt: Long,
    override val createdBy: UUID,
    override var state: PerlProcess.ExecutionState
) : PerlProcess