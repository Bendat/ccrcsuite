package ccrc.suite.gui.itasser.settings

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import ccrc.suite.commons.TrackingList
import ccrc.suite.commons.User
import ccrc.suite.commons.logger.Logger
import ccrc.suite.lib.store.database.DBError
import ccrc.suite.lib.store.database.Database
import java.io.File

object GuiDB : Logger {
    var db: Option<Database> = None

    fun login(directory: File, name: String, user: User): Either<TrackingList<DBError>, Database> {
        val _db = Database.PersistentDatabase(
            directory = directory,
            name = name,
            user = user
        )
        db = Some(_db)
        return db.toEither { _db.errors }
    }

    operator fun <T> invoke(op: Database.() -> Option<T>): Option<T> {
        return db.flatMap { op(it) }
    }
}