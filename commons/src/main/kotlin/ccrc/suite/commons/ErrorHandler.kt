package ccrc.suite.commons

interface ErrorHandler<T> {
    val errors: TrackingList<T>
}