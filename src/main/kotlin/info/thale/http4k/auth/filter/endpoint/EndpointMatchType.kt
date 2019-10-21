package info.thale.http4k.auth.filter.endpoint


/**
 * Deferments how the [Parameters] for [EndpointSignature] should be matched.
 */
enum class EndpointMatchType {
    /**
     * Matches the [Parameters] exactly, meaning no more or less. If no [Parameters] are given, then the request needs to have none as well.
     */
    EXACT,

    /**
     * Matches the [Parameters] with at least the passed parameters.
     * All parameters must be contained, but more can exist. If no [Parameters] are given, then the request can have none or more.
     */
    ATLEAST
}