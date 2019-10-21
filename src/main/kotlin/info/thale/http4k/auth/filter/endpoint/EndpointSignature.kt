package info.thale.http4k.auth.filter.endpoint

import org.http4k.core.Method
import org.http4k.core.Parameters
import org.http4k.core.Request
import org.http4k.core.queries

/**
 * Identifies one or more endpoints via their attributes (http method, path, query parameters).
 * @param method Passing null matches any [Method], passing anything else, then only this [Method] is matched by a request
 * @param path This is the url path without queries, e.g. /user/1/roles. This matching is done via regex. You can write /user/{id}/roles to match anything in the place of {id}
 * @param queries the [Parameters] (queries) of the signature, e.g. foo?queryone=one&querytwo=two => two pairs of (key, value)
 * @param endpointMatchType determines how the [Parameters] of the signature are matched with the actual request.
 */
data class EndpointSignature (val method: Method? = null,
                              val path: String,
                              val queries: Parameters = mutableListOf(),
                              val endpointMatchType: EndpointMatchType = EndpointMatchType.EXACT) {

    constructor(request: Request) : this(request.method, request.uri.path, request.uri.queries())

    constructor(urlPath: String) : this(path = urlPath)

    val matcher = EndpointMatcher(this)
}

