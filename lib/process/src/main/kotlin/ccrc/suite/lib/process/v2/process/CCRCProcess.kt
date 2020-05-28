package ccrc.suite.lib.process.v2.process

import java.io.File
import java.util.*

data class CCRCProcess(
    val id: UUID,
    val name: String,
    val args: List<String>,
    val createdAt: Date,
    val createdBy: UUID,
    val seq: File
)