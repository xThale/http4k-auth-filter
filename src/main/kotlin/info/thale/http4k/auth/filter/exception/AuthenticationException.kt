package info.thale.http4k.auth.filter.exception

/**
 * Thrown if an exception occurred in the authentication of the request.
 */
open class AuthenticationException : Exception {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

}