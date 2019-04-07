@file:Suppress("unused")

package ccrc.suite.lib.store.database

import arrow.core.*
import ccrc.suite.commons.DBObject
import ccrc.suite.commons.ErrorHandler
import ccrc.suite.commons.TrackingList
import ccrc.suite.commons.User
import ccrc.suite.commons.extensions.ifLeft
import ccrc.suite.commons.logger.Logger
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.dizitart.no2.WriteResult
import org.dizitart.no2.objects.Cursor
import org.dizitart.no2.objects.ObjectFilter
import org.dizitart.no2.objects.ObjectRepository
import java.io.File


sealed class Database : Logger, ErrorHandler<DBError> {
    abstract val db: Option<Nitrite>

    override val errors = TrackingList<DBError>()

    abstract fun init(): Database
    abstract fun deleteDatabase(): Option<Boolean>
    abstract fun exists(): Option<Boolean>

    inline fun <reified TRepo : DBObject> create(vararg item: TRepo) = insert(*item)
    inline fun <reified TRepo : DBObject> read(filter: () -> ObjectFilter) = find<TRepo>(filter)

    inline fun <reified TRepo : Any> size(): Option<Long> =
        db.map { it.getRepository<TRepo>().size() }
            .also { db.map { i -> i.getRepository<TRepo>().close() } }

    inline fun <reified TRepo : DBObject> insert(vararg item: TRepo): Option<WriteResult> {
        return db.map { it.getRepository<TRepo>().insert(item) }.also {
            db.map { i -> i.getRepository<TRepo>().close() }
        }
    }

    inline fun <reified TRepo : DBObject> context(op: ObjectRepository<TRepo>.() -> Unit) {
        db.map { it.getRepository<TRepo>().apply(op) }.also {
            db.map { i -> i.getRepository<TRepo>().close() }
        }
    }

    inline fun <reified TRepo : DBObject> update(obj: TRepo): Option<WriteResult> {
        return db.map { it.getRepository<TRepo>().update(DBObject::id eq obj.id, obj) }
    }

    inline fun <reified TRepo : DBObject> delete(obj: TRepo): Option<WriteResult> {
        return db.map { it.getRepository<TRepo>().remove(DBObject::id eq obj.id) }
    }

    inline fun <reified T : Any> find(filter: () -> ObjectFilter): Option<Cursor<T>> {
        return db.map { it.getRepository<T>().find(filter()) }.also {
            db.map { i -> i.getRepository<T>().close() }
        }
    }

    inline fun <reified T : Any> findFirst(): Option<T> {
        val db = db
        return when (db) {
            is Some -> db.t.getRepository<T>().find().firstOrNone()
            is None -> None
        }
    }

    fun close() {
        db.map { it.close() }
    }

    protected fun initAsEither(op: () -> Nitrite): Option<Nitrite> {
        val res = Try { op() }
        return when (res) {
            is Success -> Right(res.value)
            is Failure -> Left(InitError(res.exception.message))
        }.ifLeft { errors += it }.toOption()
    }

    class MemoryDatabase : Database() {
        override val db = initAsEither { nitrite { } }
        @Suppress("ReplaceSingleLineLet")
        override fun deleteDatabase(): Option<Boolean> {
            val db = db
            return when (db) {
                is Some -> db.t.close().let { Some(true) }
                is None -> false.some()
            }
        }

        override fun exists(): Option<Boolean> {
            return db.map { true }
        }

        override fun init(): MemoryDatabase {
            db.map { it.close() }
            return this
        }
    }

    class PersistentDatabase(
        private val directory: File,
        private val name: String,
        private val user: User
    ) : Database() {
        private val dbFile = File(directory, name)
        override val db = initAsEither {
            directory.mkdirs()
            nitrite(user.name.value, user.password.value) {
                file = File(directory, name)
                autoCommitBufferSize = 2048
                compress = true
                autoCompact = false
            }
        }

        override fun deleteDatabase(): Option<Boolean> {
            info { "Deleting database [$db]" }
            return db.map { o ->
                o.close()
                dbFile.delete().also { info { "Deleted db: [$it]" } }
            }
        }

        override fun exists(): Option<Boolean> =
            db.map { dbFile.exists() }

        override fun init(): PersistentDatabase {
            db.map { it.close() }
            return this
        }
    }
}