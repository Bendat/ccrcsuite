package ccrc.suite.gui.settings

import ccrc.suite.commons.User
import ccrc.suite.commons.utils.uuid
import ccrc.suite.lib.store.database.DBObject
import java.util.*

class Users: ArrayList<User>(), DBObject {
    override val id = uuid
}