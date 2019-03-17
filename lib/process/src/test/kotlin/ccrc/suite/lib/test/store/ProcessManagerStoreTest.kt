package ccrc.suite.lib.test.store

import arrow.core.None
import arrow.core.Some
import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.logger.KLog
import ccrc.suite.commons.logger.klog
import ccrc.suite.commons.utils.uuid
import ccrc.suite.lib.process.ArgNames
import ccrc.suite.lib.process.ProcessManager
import ccrc.suite.lib.store.database.Database

import com.winterbe.expekt.should
import org.spekframework.spek2.Spek
import java.io.File

class Tests : Spek({
    val log = klog<Tests>()
    val db by memoized { Database.MemoryDatabase() }
    val pm by memoized { ProcessManager() }
    group("Serialization") {

        test("Storing ProcessManager") {
            val file = javaClass.getResource("/HelloWorld.pl").file
            val id = pm.new(0, File(file), "Test Process 1", args, uuid)
            pm.size.should.equal(1)
            pm.queues[PerlProcess.ExecutionState.Queued]?.size.should.equal(1)
            val found = pm.find(id)
            log.info(found)
            db.create(pm)
            val repo = db.size<ProcessManager>()
            repo.should.not.be.instanceof(None::class.java)
            repo as Some<Long>
            repo.t.should.equal(1)
            val res = db.findFirst<ProcessManager>()
            log.info{res}
            res.map { it.size.should.equal(1) }
            (res is None).should.be.`false`
        }
    }

    group("Deserialization") {


    }
})


private val args = listOf("perl", ArgNames.AutoFlush.toString())