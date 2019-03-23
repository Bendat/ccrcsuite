package ccrc.suite.lib.store.test

import ccrc.suite.commons.logger.Logger
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.objects.ObjectRepository
import org.junit.platform.commons.annotation.Testable
import org.spekframework.spek2.Spek

@Testable
class NitriteTests: Spek({
    val log = object: Logger {}
    group("Initial Investigation Group"){
        val db by memoized{nitrite {}}
        lateinit var repo: ObjectRepository<DocItem>

        beforeEachTest {
            repo = db.getRepository<DocItem>("test"){
                insert(DocItem("a", "b"))
            }
        }

        test("Retrieving an Item"){
            val res = repo.find(DocItem::a eq "a")
            log.info{"Result is [${res.first()}]"}
        }
    }

})

data class DocItem(val a:String, val b: String)