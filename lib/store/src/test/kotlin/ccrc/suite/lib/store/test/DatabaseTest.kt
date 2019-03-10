package ccrc.suite.lib.store.test

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import ccrc.suite.commons.User
import ccrc.suite.commons.utils.safeWait
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
        val dir by memoized { File(DatabaseTest::class.java.getResource("/").file) }
        val file by memoized { "db.db" }
        val user by memoized { User("a@b.com", "12345") }
        val db by memoized { PersistentDatabase(dir, file, user) }
        afterGroup { db.deleteDatabase() }

        test("Creating Database") {
            val dbfile = File(dir, file)
            dbfile.exists().should.be.`true`
        }

        test("Deleting a Database"){
            val dbfile = File(dir, file)
            dbfile.exists().should.be.`true`
            db.deleteDatabase()
            safeWait(2000)
            dbfile.exists().should.be.`false`
        }

    }
})

data class TestObject(
    val id: UUID,
    val name: String
)

fun dirTree(f: File): String? {
    var output: String? = null
    for (file in f.listFiles()!!) {
        if (file.isDirectory) {
            output += file.name + "\n"
            dirTree(file)
        } else {
            output += file.name + " (" + file.length() / 1024 + " kB)" + "\n"
        }
    }
    return output
}