package ccrc.suite.lib.store.test

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import ccrc.suite.commons.User
import ccrc.suite.lib.store.database.DBObject
import ccrc.suite.lib.store.database.Database
import ccrc.suite.lib.store.database.Database.PersistentDatabase
import com.github.javafaker.Faker
import com.winterbe.expekt.should
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.objects.Id
import org.junit.platform.commons.annotation.Testable
import org.spekframework.spek2.Spek
import java.io.File
import java.util.*


@Testable
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
            (res is None).should.be.`false`
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

        group("Database Actions") {
            test("Creating Database") {
                db.db.map {}
                println(File(dir, file).exists())
                File(dir, file).exists().should.be.`true`
                File(dir, file).isFile.should.be.`true`
            }

            test("Deleting a Database") {
                val dbfile = File(dir, file)
                db.db.map {}
                dbfile.exists().should.be.`true`
                println { "DB is [$db]" }
                db.deleteDatabase()
                dbfile.exists().should.be.`false`
            }

            test("Closing and Reopening a Database") {
                val dbfile = File(dir, file)
                db.db.map {}
                dbfile.exists().should.be.`true`
                db.close()
                val db2 = PersistentDatabase(dir, file, user)
                (db2.db is  Some).should.be.`true`
            }

            test("Opening a Database with Bad Credentials") {
                val dbfile = File(dir, file)
                db.db.map {}
                dbfile.exists().should.be.`true`
                db.close()
                val db2 = PersistentDatabase(dir, file, User("1", "2"))
                println("Db2 is [${db2.db}]")
                (db2.db is None).should.be.`true`
            }
        }

        group("Crud Operations") {
            test("Creating TestObject") {
                db.insert(to)
                val repo = db.size<TestObject>()
                repo.should.not.be.instanceof(None::class.java)
                repo as Some<Long>
                repo.t.should.equal(1)
            }

            test("Reading TestObject") {
                db.insert(to)
                db.insert(TestObject())
                db.insert(TestObject())
                db.insert(TestObject())

                val repo = db.size<TestObject>()
                repo.should.not.be.instanceof(None::class.java)
                repo as Some<Long>
                repo.t.should.equal(4)
                val found = db.find<TestObject> { TestObject::id eq to.id }
                found as Some
                found.t.size().should.equal(1)
                found.t.first().id.should.equal(to.id)
            }


            test("Updating ObjectRepository item") {
                db.insert(to)
                db.context<TestObject> {
                    insert(TestObject())
                    insert(TestObject())
                }
                val size = db.size<TestObject>()
                size as Some<Long>
                size.t.should.equal(3)
                val newObj = TestObject(to.id)
                db.update(newObj)
                val size2 = db.size<TestObject>()
                size2 as Some<Long>
                size.t.should.equal(3)
                val ret = db.read<TestObject> { DBObject::id eq newObj.id }
                ret as Some
                ret.t.first().should.equal(newObj)
                ret.t.first().should.not.equal(to)
            }

            test("Deleting ObjectRepository item") {
                db.insert(to)
                db.context<TestObject> {
                    insert(TestObject())
                    insert(TestObject())
                }
                val size = db.size<TestObject>()
                size as Some<Long>
                size.t.should.equal(3)
                db.delete(to)
                val size2 = db.size<TestObject>()
                size2 as Some
                size2.t.should.equal(2)
            }

            test("Using ObjectRepository Context") {
                db.insert(to)
                db.context<TestObject> {
                    insert(TestObject())
                    insert(TestObject())
                }
                val size = db.size<TestObject>()
                size as Some<Long>
                size.t.should.equal(3)
            }
        }
    }

})

var faker = Faker()

data class TestObject(
    @Id override val id: UUID = UUID.randomUUID(),
    val name: String = faker.name().fullName()
) : DBObject
