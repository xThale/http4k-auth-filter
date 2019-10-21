package info.thale.http4k.auth.filter.authorization

import info.thale.http4k.auth.filter.authorization.role.AuthorizedRoles
import info.thale.http4k.auth.filter.authorization.role.RoleBased
import info.thale.http4k.auth.filter.config.AuthFilterConfiguration
import info.thale.http4k.auth.filter.exception.AuthorizationException
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.lens.RequestContextLens


/**
 * Base filter for the authorization of a request
 */
class AuthorizationFilter<ContextObjectType : RoleBased<RoleType>, RoleType>(private val authFilterConfiguration: AuthFilterConfiguration<ContextObjectType, RoleType>) {

    fun authorize(context: RequestContextLens<ContextObjectType>): Filter {
        return Filter { next ->
            { request -> (
                    try {
                        authorizeRequest(context, next, request)
                    } catch (e: AuthorizationException) {
                        throw e
                    }catch (e: Exception) {
                        throw AuthorizationException(e.message ?: "Error occurred while authorizing request", e)
                    }
                )
            }
        }
    }

    private fun authorizeRequest(context: RequestContextLens<ContextObjectType>, next: HttpHandler, request: Request): Response {
        if (endpointIsSecured(request)) {
            val authorizedRoles = authorizedRoles(request)

            if (authorizedRoles.requiredRoles.isNotEmpty()) {
                val contextObject = extractContextObjectFromRequest(context, request)
                checkIfContextObjectHasAuthorizedRoles(authorizedRoles, contextObject)
            }
        }
        return next(request)
    }

    private fun endpointIsSecured(request: Request): Boolean {
        return authFilterConfiguration.endpointSecurityConfig.isSecuredEndpoint(request)
    }

    private fun authorizedRoles(request: Request): AuthorizedRoles<RoleType> {
        return authFilterConfiguration.endpointSecurityConfig.getRolesForEndpoint(request)
    }

    private fun extractContextObjectFromRequest(context: RequestContextLens<ContextObjectType>, request: Request): ContextObjectType {
        return context(request)
    }

    private fun checkIfContextObjectHasAuthorizedRoles(authorizedRoles: AuthorizedRoles<RoleType>, contextObject: ContextObjectType) {
        authorizedRoles.authorize(contextObject.getRoleList())
    }
}