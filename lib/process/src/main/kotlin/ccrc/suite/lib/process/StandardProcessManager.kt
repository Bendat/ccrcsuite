@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ccrc.suite.lib.process

import arrow.core.*
import ccrc.suite.commons.DBObject
import ccrc.suite.commons.ID
import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.PerlProcess.ExecutionState
import ccrc.suite.commons.PerlProcess.ExecutionState.*
import ccrc.suite.commons.logger.Logger
import ccrc.suite.commons.utils.uuid
import javafx.application.Platform
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleMapProperty
import javafx.collections.FXCollections
import org.dizitart.no2.Document
import org.dizitart.no2.mapper.Mappable
import org.dizitart.no2.mapper.NitriteMapper
import org.dizitart.no2.objects.Id
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.listener.ProcessListener
import tornadofx.getValue
import tornadofx.setValue
import java.io.File
import java.lang.System.currentTimeMillis
import java.util.*

open class ProcessManager(@Id override val id: ID = ID(uuid)) : Logger, Mappable, DBObject {
    val queues get() = _queues
    val size get() = queues.map { it.value.size }.sum()
    val next get() = queues[Queued]!!.first
    val running get() = queues[Running]?.size ?: 0
    open var max = 3
    @get:Synchronized
    @set:Synchronized
    @Volatile
    private var _queues: MutableMap<ExecutionState, ProcessQueue> =
        SimpleMapProperty<ExecutionState, ProcessQueue>(FXCollections.observableHashMap()).apply {
            put(Completed, ProcessQueue())
            put(Paused, ProcessQueue())
            put(Running, ProcessQueue())
            put(Failed, ProcessQueue())
            put(Queued, ProcessQueue())
        }

    val exitCodes = hashMapOf<UUID, Int>()
    inline operator fun <reified T : ProcessManager> invoke(op: (T) -> Unit): Boolean {
        return if (this is T) {
            op(this)
            true
        } else false

    }

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
            info { cq.map { k -> k.value::class } }
            val max = it.get("max") as Int
            _queues = cq
            this.max = max
        }
    }

    open fun new(
        priority: Int,
        seqFile: File,
        name: String,
        args: List<String>,
        createdBy: UUID
    ) = new(uuid, priority, seqFile, name, args, createdBy)

    open fun new(
        processId: UUID,
        priority: Int,
        seqFile: File,
        name: String,
        args: List<String>,
        createdBy: UUID
    ): UUID {
        val runner = ProcessRunner(
            ITasserProcess(
                id = processId,
                seq = seqFile,
                name = name,
                args = args,
                createdAt = currentTimeMillis(),
                createdBy = createdBy,
                state = Queued
            ), ProcessManagerListener(processId)
        )
        this[priority, Queued] = runner
        return processId.also { info { "Created runner [${runner.process}  with id [$id]" } }
    }

    open fun removeAll() {
        queues.map { it.value }.flatMap { it.map { w -> remove(w.runner.process.id) } }
    }

    open fun remove(processId: UUID): Option<Boolean> {
        stop(processId)
        return findQueue(processId)
            .flatMap { q -> find(processId).map { q.remove(it) } }
    }

    fun find(processId: UUID): Option<Wrapper> {
        info { "Qeueus are [$queues]" }
        return queues.toList().flatMap { it.second }
            .firstOrNull { it.runner.process.id == processId }
            .toOption()
    }

    fun run(processId: UUID): Option<ProcessRunner> =
        queues.map { entry -> entry.value.firstOrNull { it.runner.process.id == processId } }
            .map { it?.runner?.start() }.firstOrNull().toOption()

    fun stop(processId: UUID) {
        find(processId).flatMap { it.runner.stop() }
    }

    fun shutdown(onStopped: (PerlProcess) -> Unit = {}) {
        queues.flatMap { it.value }.forEach {
            it.runner.stop()
            onStopped(it.runner.process)
        }
    }

    @Synchronized
    internal operator fun set(priority: Int, state: ExecutionState, process: ProcessRunner) {
        queues.map { e -> e.value.removeIf { it.runner.process.id == process.process.id } }
        if (state == Running && queues[state]!!.size >= max)
            this[Queued].add(Wrapper(process, priority, Queued))
        else this[state].add(Wrapper(process, priority, state))
    }

    open operator fun set(priority: Int, state: ExecutionState, processID: UUID) {
        find(processID).map {
            this[priority, state] = it.runner
        }
    }

    fun waitFor(processId: UUID): Option<Wrapper> {
        return find(processId).map { it.runner.await(); it }
    }

    operator fun get(queue: ExecutionState, priority: Int): List<ProcessRunner> =
        queues[queue]?.filter { it.priority == priority }?.map { it.runner } ?: listOf()

    operator fun get(queue: ExecutionState): ProcessQueue = queues[queue]!!
    operator fun get(queue: ExecutionState, process: ProcessRunner) =
        queues[queue]?.first { it.runner == process }.toOption()

    operator fun get(queue: ExecutionState, processId: UUID) =
        queues[queue]?.first { it.runner.process.id == processId }.toOption()


    open operator fun set(processId: UUID, queue: ExecutionState) {
        find(processId).map { p ->
            info { "Removing [${p.runner.process.name}] from [${p.state}] for [$queue]" }
            queues[p.state]?.remove(p).also { info { "Removed: [$it][${p.state}][$queue]" } }
            this@ProcessManager[p.priority, queue] = p.runner
        }
    }

    fun findQueue(processId: UUID): Option<ProcessQueue> {
        val p = find(processId)
        return when (p) {
            is None -> None
            is Some -> Some(this[p.t.state])
        }
    }


    override fun toString() =
        "ProcessManager(queues=${queues.map { "[${it.key}][${it.value.size}]" }.joinToString()})"

    inner class ProcessManagerListener(private val processId: UUID) : ProcessListener() {

        override fun afterStart(process: Process, executor: ProcessExecutor?) {
            info { "Process [$processId] has started" }
            this@ProcessManager[processId] = Running
        }

        override fun afterFinish(process: Process, result: ProcessResult) {
            info { "Result is [${result.output.lines.joinToString(",\n")}]" }
            exitCodes[processId] = result.exitValue
            val procname = this@ProcessManager.find(processId).getOrElse { null }

            this@ProcessManager[processId] =
                PerlProcess.ExitCode.fromInt(result.exitValue).state
                    .also { info { "Found state was [$it] for [$processId][$procname][${result.exitValue}]" } }
        }

    }

    class StandardProcessManager(id: UUID = uuid) : ProcessManager(ID(id))
    class FXProcessManager(id: UUID = uuid) : ProcessManager(ID(id)) {
        val maxProperty = SimpleIntegerProperty(super.max)
        override var max by maxProperty

        override fun set(processId: UUID, queue: ExecutionState) {
            Platform.runLater { super.set(processId, queue) }
        }

        override fun set(priority: Int, state: ExecutionState, processID: UUID) {
            info { "Setting [$state], [$processID]" }
            Platform.runLater {
                find(processID).map {
                    info { "Found [$state][$processID" }
                    this[it.runner.process.id] = state
                }.toEither { }.mapLeft { info { "Not Found [$processID]" } }
            }
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
) : SimpleListProperty<Wrapper>(FXCollections.observableArrayList()), Logger {

    operator fun get(id: UUID): Option<Wrapper> =
        firstOrNull { it.runner.process.id == id }.toOption()

    @Synchronized
    override fun add(element: Wrapper): Boolean {
        return super.add(element)
            .also { onAdd(element) }
            .also { sortByDescending { w -> w.priority } }
            .also { info { "Adding element [$element] size is now [$size]" } }

    }

    @Synchronized
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

