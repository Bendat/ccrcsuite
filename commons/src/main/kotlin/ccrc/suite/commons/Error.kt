package ccrc.suite.commons

typealias BadYaml = Error.BadYamlError
typealias ApiFailure = Error.ApiFailureError
typealias BadInitError = Error.BadInitialization
typealias Unknown = Error.UnknownError
sealed class Error {
    abstract val message: Any?
    data class BadYamlError(override val message: Any?) : Error()
    data class ApiFailureError(override val message: Any?) : Error()
    data class UnknownError(override val message: Any? = "Unknown error occured.") : Error()
    data class BadInitialization(override val message: Any?) : Error()
}