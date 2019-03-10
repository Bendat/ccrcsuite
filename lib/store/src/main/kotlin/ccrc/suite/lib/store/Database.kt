package ccrc.suite.lib.store

import arrow.core.*
import ccrc.suite.commons.User
import ccrc.suite.commons.logger.Loggable
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.dizitart.no2.WriteResult
import org.dizitart.no2.objects.Cursor
import org.dizitart.no2.objects.ObjectFilter
import java.io.File

sealed class Database : Loggable {
    abstract val db: Either<DBError, Nitrite>

    inline fun <reified T : Any> size(): Option<Long> {
        return db.map { it.getRepository<T>().size() }.toOption().also {
            db.map { i -> i.getRepository<T>().close() }
        }
    }

    inline fun <reified T : Any> insert(vararg item: T)
            : Either<DBError, WriteResult> {
        return db.map { it.getRepository<T>().insert(item) }.also {
            db.map { i -> i.getRepository<T>().close() }
        }
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
        val directory: File,
        val name: String,
        val user: User
    ) : Database() {
        private val dbFile = File(directory, name)
        override val db = initAsEither {
            directory.mkdirs()
            nitrite(user.email, user.password) {
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