package ccrc.suite.lib.store.test

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import ccrc.suite.commons.User
import ccrc.suite.lib.store.Database
import ccrc.suite.lib.store.Database.PersistentDatabase
import com.winterbe.expekt.should
import org.dizitart.kno2.filters.eq
import org.spekframework.spek2.Spek
import java.io.File
import java.util.*

class DatabaseTest : Spek({
    val to by memoized { TestObject(UUID.randomUUID(), "Paul Peterson") }

    group("In Memory Database Group") {
        val db by memoized { Database.MemoryDatabase() }
        test("Adding TestObject") {
            db.insert(to)
            val repo = db.size<TestObject>()
            repo.should.not.be.instanceof(None::class.java)
            repo as Some<Long>
            repo.t.should.equal(1)
        }
        test("Retrieving TestObject") {
            db.insert(to)
            val res = db.find<TestObject> { TestObject::id eq to.id }
            (res is Either.Left).should.be.`false`
            res.map { it.first().id.should.equal(to.id) }
        }
    }

    group("Real File Database Group") {
        val userHome = File(System.getProperty("user.home"))
        val dir = File(userHome, "ccrc-test")
        val file by memoized { "db.db" }
        val user by memoized { User("a@b.com", "12345") }
        val db by memoized { PersistentDatabase(dir, file, user) }

        afterEachTest { println(db.deleteDatabase()) }

        test("Creating Database") {
            db.insert(to)
            println(File(dir, file).exists())
            File(dir, file).exists().should.be.`true`
            File(dir, file).isFile.should.be.`true`
        }

        test("Deleting a Database") {
            val dbfile = File(dir, file)
            db.insert(to)
            dbfile.exists().should.be.`true`
            println { "DB is [$db]" }
            db.deleteDatabase()
            dbfile.exists().should.be.`false`
        }

        test("Closing and Reope" +
                "ning a Database") {
            val dbfile = File(dir, file)
            db.insert(to)
            dbfile.exists().should.be.`true`
            db.close()
            val db2 = PersistentDatabase(dir, file, user)
            (db2.db is Either.Right).should.be.`true`
        }

        test("Opening a Database with Bad Credentials") {
            val dbfile = File(dir, file)
            db.insert(to)
            dbfile.exists().should.be.`true`
            db.close()
            val db2 = PersistentDatabase(dir, file, User("1", "2"))
            println("Db2 is [${db2.db}]")
            (db2.db is Either.Left).should.be.`true`
        }

    }
})

data class TestObject(
    val id: UUID,
    val name: String
)