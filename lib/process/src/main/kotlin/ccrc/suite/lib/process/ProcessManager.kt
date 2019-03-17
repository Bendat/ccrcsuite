@file:Suppress("unused")

package ccrc.suite.lib.process

import arrow.core.Option
import arrow.core.toOption
import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.PerlProcess.ExecutionState
import ccrc.suite.commons.PerlProcess.ExecutionState.*
import ccrc.suite.commons.logger.Loggable
import ccrc.suite.commons.utils.uuid
import ccrc.suite.lib.store.database.DBObject
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import org.dizitart.no2.Document
import org.dizitart.no2.mapper.Mappable
import org.dizitart.no2.mapper.NitriteMapper
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.listener.ProcessListener
import java.io.File
import java.lang.System.currentTimeMillis
import java.util.*

typealias NewValue = Wrapper
typealias OldValue = Wrapper

class ProcessManager(override val id: UUID = uuid) : Loggable, Mappable, DBObject {


    private var _queues: MutableMap<ExecutionState, ProcessQueue> =
        mutableMapOf(
            Completed to ProcessQueue(),
            Paused to ProcessQueue(),
            Running to ProcessQueue(),
            Failed to ProcessQueue(),
            Queued to ProcessQueue()
        )
    val queues get() = _queues
    val size get() = queues.map { it.value.size }.sum()
    val next get() = queues[Queued]!!.first
    val running get() = queues[Running]?.size ?: 0
    var max = 3

    override fun write(p0: NitriteMapper?) = Document().apply {
        put("queues", queues)
        put("max", max)
    }


    override fun read(p0: NitriteMapper?, p1: Document?) {
        p1?.let {
            val queues = it.get("queues") as LinkedHashMap<*, *>
            val cq = queues.mapKeys { k -> k.key as ExecutionState }
                .mapValues { k -> k.value as ProcessQueue }.toMutableMap()
            info { "Nitrite map is [${cq[Queued]}]" }
            info{cq.map { k->k.value::class }}
            val max = it.get("max") as Int
            _queues = cq
            this.max = max
        }
    }

    fun new(
        priority: Int,
        seqFile: File,
        name: String,
        args: List<String>,
        createdBy: UUID
    ): UUID {
        val id = uuid
        val runner = ProcessRunner(
            ITasserProcess(
                id = id,
                seq = seqFile,
                name = name,
                args = args,
                createdAt = currentTimeMillis(),
                createdBy = createdBy,
                state = Queued
            ), ProcessManagerListener(id)
        )
        this[priority, Queued] = runner
        return id.also { info { "Created runner [${runner.process}  with id [$id]" } }
    }

    fun find(processId: UUID): Option<Wrapper> {
        return queues.toList().flatMap { it.second }
            .firstOrNull { it.runner.process.id == processId }
            .toOption()
    }

    fun run(processId: UUID) {
        queues.map { entry -> entry.value.firstOrNull { it.runner.process.id == processId } }
            .map { it?.runner?.start() }
    }

    operator fun set(priority: Int, state: ExecutionState, process: ProcessRunner) {
        queues.map { e -> e.value.removeIf { it.runner.process.id == process.process.id } }
        if (state == Running && queues[state]!!.size >= max)
            queues[Queued]!! += Wrapper(process, priority, Queued)
        else queues[state]!!.add(Wrapper(process, priority, state))
    }

    fun waitFor(processId: UUID) {
        find(processId).map { it.runner.await() }
    }

    operator fun get(queue: ExecutionState, priority: Int): List<ProcessRunner> {
        return queues[queue]?.filter { it.priority == priority }
            ?.map { it.runner } ?: listOf()
    }

    operator fun get(queue: ExecutionState): ProcessQueue {
        return queues[queue]!!
    }

    operator fun get(queue: ExecutionState, process: ProcessRunner) =
        queues[queue]?.first { it.runner == process }.toOption()

    operator fun get(queue: ExecutionState, processId: UUID) =
        queues[queue]?.first { it.runner.process.id == processId }.toOption()

    operator fun set(processId: UUID, queue: ExecutionState) {
        find(processId).map { p ->
            info { "Removing [${p.runner.process.name}] from [${p.state}] for [$queue]" }
            queues[p.state]?.remove(p).also { info { "Removed: [$it][${p.state}][$queue]" } }
            this[p.priority, queue] = p.runner
        }
    }

    fun findQueue(processId: UUID): Option<Wrapper> =
        queues.entries.map { it.value.firstOrNull { q -> q.runner.process.id == processId } }
            .firstOrNull().toOption()


    override fun toString() =
        "ProcessManager(queues=${queues.map { "[${it.key}][${it.value.size}]" }.joinToString()})"

    inner class ProcessManagerListener(private val processId: UUID) : ProcessListener() {

        override fun afterStart(process: Process?, executor: ProcessExecutor?) {
            info { "Process [$processId] has started" }
            this@ProcessManager[processId] = Running
        }

        override fun afterFinish(process: Process, result: ProcessResult) {
            info { "Process [$processId] has completed" }
            this@ProcessManager[processId] =
                    PerlProcess.ExitCode.fromInt(result.exitValue).state
                        .also { info { "Found state was [$it]" } }
        }
    }
}


data class Wrapper(
    val runner: ProcessRunner,
    val priority: Int,
    val state: ExecutionState
) {
    override fun toString(): String {
        return "[$priority][$state][${runner.process}]"
    }
}

class ProcessQueue(
    val onAdd: ProcessQueue.(Wrapper) -> Unit = {},
    val onRemove: ProcessQueue.(Wrapper) -> Unit = {}
) : SimpleListProperty<Wrapper>(FXCollections.observableArrayList()), Loggable {
    operator fun get(id: UUID): Option<Wrapper> =
        firstOrNull { it.runner.process.id == id }.toOption()

    override fun add(element: Wrapper): Boolean {
        return super.add(element)
            .also { onAdd(element) }
            .also { sortByDescending { w -> w.priority } }
            .also { info { "Adding element [$element] size is now [$size]" } }
    }

    override fun remove(element: Wrapper): Boolean {
        return super.remove(element)
            .also { onRemove(element) }
            .also { sortByDescending { w -> w.priority } }
            .also { info { "Removing element [$element] size is now [$size]" } }
    }

    val first: Option<Wrapper>
        get() = this.sortedByDescending { it.priority }.firstOrNull().toOption()

    val last: Option<Wrapper>
        get() = this.sortedBy { it.priority }.firstOrNull().toOption()
}

