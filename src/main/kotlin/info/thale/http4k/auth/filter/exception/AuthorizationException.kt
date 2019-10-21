package info.thale.http4k.auth.filter.exception

/**
 * Thrown if an exception occurred in the authorization of the request
 */
class AuthorizationException : Exception {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

}