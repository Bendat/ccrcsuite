package ccrc.suite.commons

import java.util.*
inline class ID(val value: UUID)
/**
 * Represents an object which is expected to be
 * stored in the database.
 */
interface DBObject {
    /**
     * The [UUID] id of this item.
     */
    val id: ID
}
