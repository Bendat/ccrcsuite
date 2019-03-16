package ccrc.suite.lib.store.database

import arrow.core.*
import ccrc.suite.commons.User
import ccrc.suite.commons.logger.Loggable
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.dizitart.no2.WriteResult
import org.dizitart.no2.objects.Cursor
import org.dizitart.no2.objects.ObjectFilter
import org.dizitart.no2.objects.ObjectRepository
import java.io.File
import java.util.*

interface DBObject {
    val id: UUID
}

sealed class Database : Loggable {
    abstract val db: Either<DBError, Nitrite>

    inline fun <reified TRepo : DBObject> create(vararg item: TRepo) = insert(*item)
    inline fun <reified TRepo : DBObject> read(filter: () -> ObjectFilter) = find<TRepo>(filter)

    inline fun <reified TRepo : Any> size(): Option<Long> {
        return db.map { it.getRepository<TRepo>().size() }.toOption().also {
            db.map { i -> i.getRepository<TRepo>().close() }
        }
    }

    inline fun <reified TRepo : DBObject> insert(vararg item: TRepo)
            : Either<DBError, WriteResult> {
        return db.map { it.getRepository<TRepo>().insert(item) }.also {
            db.map { i -> i.getRepository<TRepo>().close() }
        }
    }

    inline fun <reified TRepo : DBObject> context(op: ObjectRepository<TRepo>.() -> Unit) {
        db.map { it.getRepository<TRepo>().apply(op) }.also {
            db.map { i -> i.getRepository<TRepo>().close() }
        }
    }

    inline fun <reified TRepo : DBObject> update(obj: TRepo): Either<DBError, WriteResult> {
        return db.map {
            it.getRepository<TRepo>().update(DBObject::id eq obj.id, obj)
        }
    }

    inline fun <reified TRepo : DBObject> delete(obj: TRepo){
        db.map { it.getRepository<TRepo>().remove(DBObject::id eq obj.id) }
    }

    inline fun <reified T : Any> find(filter: () -> ObjectFilter)
            : Either<DBError, Cursor<T>> {
        return db.map { it.getRepository<T>().find(filter()) }.also {
            db.map { i -> i.getRepository<T>().close() }
        }
    }

    fun close() {
        db.map { it.close() }
    }

    abstract fun init(): Database

    abstract fun deleteDatabase(): Either<DbException, Boolean>
    abstract fun exists(): Either<DbException, Boolean>

    protected fun initAsEither(op: () -> Nitrite): Either<DbException, Nitrite> {
        val res = Try { op() }
        return when (res) {
            is Success -> Right(res.value)
            is Failure -> Left(InitError(res.exception.message))
        }
    }

    class MemoryDatabase : Database() {
        override val db = initAsEither { nitrite { } }
        override fun deleteDatabase(): Either<DbException, Boolean> {
            return db.map {
                it.close()
                true
            }
        }

        override fun exists(): Either<DbException, Boolean> {
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
            nitrite(user.username, user.password) {
                file = File(directory, name)
                autoCommitBufferSize = 2048
                compress = true
                autoCompact = false
            }
        }

        override fun deleteDatabase(): Either<DbException, Boolean> {
            info { "Deleting database [$db]" }
            return db.map { o ->
                o.close()
                dbFile.delete().also { info { "Deleted db: [$it]" } }
            }
        }

        override fun exists(): Either<DbException, Boolean> {
            return db.map { dbFile.exists() }
        }

        override fun init(): PersistentDatabase {
            db.map { it.close() }
            return this
        }
    }
}