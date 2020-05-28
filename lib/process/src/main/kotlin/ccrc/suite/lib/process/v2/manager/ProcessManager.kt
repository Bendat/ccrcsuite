package ccrc.suite.lib.process.v2.manager

import ccrc.suite.lib.process.v2.process.ITasser
import lk.kotlin.observable.list.observableListOf

class ProcessManager(var maxExecuting: Int) {
    val process = Processes()

    class Processes {
        val queued = observableListOf<ITasser>()
        val paused = observableListOf<ITasser>()
        val completed = observableListOf<ITasser>()
        val running = observableListOf<ITasser>()
        val failed = observableListOf<ITasser>()

        val size
            get() = queued.size +
                    paused.size +
                    completed.size +
                    running.size +
                    failed.size
    }


}