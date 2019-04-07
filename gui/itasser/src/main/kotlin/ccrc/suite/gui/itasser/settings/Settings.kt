package ccrc.suite.gui.itasser.settings

import ccrc.suite.commons.DBObject
import ccrc.suite.commons.ID
import ccrc.suite.commons.utils.uuid
import java.io.File

data class Settings(
    val pkgDir: File,
    val libDir: File,
    val javaHome: File,
    val dataDIr: File,
    val runStyle: String,
    override val id: ID = ID(uuid)
) : DBObject