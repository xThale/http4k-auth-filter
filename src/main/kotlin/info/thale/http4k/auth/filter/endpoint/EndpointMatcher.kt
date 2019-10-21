package info.thale.http4k.auth.filter.endpoint

import org.http4k.core.Method
import org.http4k.core.Parameters
import org.http4k.core.Request

class EndpointMatcher(private val sourceSignature: EndpointSignature) {

    companion object {
        private val regexMatchingPlaceholder = Regex("\\{[^}]*}")
        private const val regexMatchingAnyMethod = "^([^\\s]+)"
        private const val regexMatchingAnyPathTillNextSlash = "([^/]*)"
    }

    private val methodRegex = methodToRegex(sourceSignature.method)
    private fun methodToRegex(method: Method?) = method?.name ?: regexMatchingAnyMethod

    private val pathRegex = pathToRegex(sourceSignature.path)
    private fun pathToRegex(path: String) = path.replace(regexMatchingPlaceholder, regexMatchingAnyPathTillNextSlash)

    private val signature : Regex = "$methodRegex $pathRegex".toRegex()

    fun match(request: Request): Boolean {
        return match(EndpointSignature(request))
    }

    fun match(endpointSignature: EndpointSignature) : Boolean {
        return when(sourceSignature.queries.isEmpty() && sourceSignature.endpointMatchType == EndpointMatchType.ATLEAST) {
            true -> matchSignature(endpointSignature)
            false -> matchSignature(endpointSignature) && matchQueries(endpointSignature)
        }
    }

    private fun matchSignature(endpointSignature: EndpointSignature) : Boolean {
        return signature.matches("${endpointSignature.method} ${endpointSignature.path}")
    }

    private fun matchQueries(endpointSignature: EndpointSignature) : Boolean {
        return when(sourceSignature.endpointMatchType) {
            EndpointMatchType.EXACT -> matchExactQueries(endpointSignature.queries)
            EndpointMatchType.ATLEAST -> matchAtLeastQueries(endpointSignature.queries)
        }
    }

    private fun matchExactQueries(queries: Parameters): Boolean {
        return queries.size == sourceSignature.queries.size && queries.containsAll(sourceSignature.queries)
    }

    private fun matchAtLeastQueries(queries: Parameters): Boolean {
        return queries.containsAll(sourceSignature.queries)
    }

}