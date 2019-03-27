package ccrc.suite.gui.settings

import ccrc.suite.commons.utils.uuid
import ccrc.suite.lib.store.database.DBObject
import java.io.File
import java.util.*

data class ITasserSettings(
    val pkgDir: File,
    val libDir: File,
    val javaHome: File,
    val dataDir: File
): DBObject {
    override val id = uuid
}