package ccrc.suite.lib.store.database
typealias DBError = DbException
typealias InitError = DbException.InitializationException
sealed class DbException(open val message:Any?): Ru {
   class InitializationException(override val message:Any?): DbException(message)
}