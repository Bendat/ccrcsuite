package ccrc.suite.lib.process

import ccrc.suite.commons.logger.Loggable
import java.util.*


class ProcessManager : Loggable {
    private val queue = PriorityQueue<Wrapper>(PriorityComparator())
    val size get() = queue.size
    val printQueue get() = queue.toString()
    operator fun set(priority: Int, process: ProcessRunner) {
        val exists = queue.firstOrNull { it.process == process }
        exists?.let { queue -= it }
        queue += Wrapper(process, priority)
    }

    operator fun get(priority: Int): List<ProcessRunner> {
        return queue.filter { it.priority == priority }.map {
            it.process
        }
    }

    operator fun get(process: ProcessRunner): Int {
        return queue.filter { it.process == process }.map {
            it.priority
        }.first()
    }
}

data class Wrapper(
    val process: ProcessRunner,
    val priority: Int
)

private class PriorityComparator : Comparator<Wrapper> {
    override fun compare(x: Wrapper, y: Wrapper): Int {
        return if (x.priority > y.priority) 1
        else if (x.priority < y.priority) -1
        else 0
    }
}