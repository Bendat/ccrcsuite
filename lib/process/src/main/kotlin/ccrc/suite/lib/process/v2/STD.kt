package ccrc.suite.lib.process.v2

import ccrc.suite.commons.TrackingList

data class STD(
    val output: TrackingList<String> = TrackingList(),
    val err: TrackingList<String> = TrackingList()
)