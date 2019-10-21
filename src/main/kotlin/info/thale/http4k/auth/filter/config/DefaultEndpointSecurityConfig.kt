package info.thale.http4k.auth.filter.config

import info.thale.http4k.auth.filter.authorization.role.AuthorizedRoles
import info.thale.http4k.auth.filter.authorization.role.matcher.RoleMatchingLogic
import info.thale.http4k.auth.filter.endpoint.EndpointSignature
import org.http4k.core.Request

class DefaultEndpointSecurityConfig<RoleType> : EndpointSecurityConfig<RoleType> {

    private val authorizationSecuredEndpoints : LinkedHashMap<EndpointSignature, AuthorizedRoles<RoleType>> = linkedMapOf()

    private val unsecuredEndpoints: MutableList<EndpointSignature> = mutableListOf()

    override fun getRolesForEndpoint(request: Request): AuthorizedRoles<RoleType> {
        val endpoint = getEndpointSignatureForRequest(request)
        return authorizationSecuredEndpoints.getOrDefault(endpoint, AuthorizedRoles(emptyList()))
    }

    private fun getEndpointSignatureForRequest(request: Request): EndpointSignature? {
        return authorizationSecuredEndpoints.keys.find { it.matcher.match(request) }
    }


    override fun registerRolesForEndpoint(endpointSignature: EndpointSignature,
                                          requiredRoles: List<RoleType>,
                                          matchingLogic: RoleMatchingLogic) : EndpointSecurityConfig<RoleType> {
        return this.apply { authorizationSecuredEndpoints[endpointSignature] = AuthorizedRoles(requiredRoles, matchingLogic) }
    }

    override fun registerRolesForEndpoint(endpointSignature: EndpointSignature, requiredRole: RoleType)
            = registerRolesForEndpoint(endpointSignature, listOf(requiredRole), RoleMatchingLogic.EVERY)

    override fun registerRolesForEndpoint(urlPath: String, requiredRoles: List<RoleType>, matchingLogic: RoleMatchingLogic)
            = registerRolesForEndpoint(EndpointSignature(path = urlPath), requiredRoles, matchingLogic)

    override fun registerRolesForEndpoint(urlPath: String, requiredRole: RoleType)
            = registerRolesForEndpoint(EndpointSignature(path = urlPath), requiredRole)



    override fun isSecuredEndpoint(request: Request): Boolean {
        return isUnsecuredEndpoint(request).not()
    }

    override fun isUnsecuredEndpoint(request: Request): Boolean {
        return unsecuredEndpoints.any { it.matcher.match(request) }
    }


    override fun registerUnsecuredEndpoint(endpointSignature: EndpointSignature) : EndpointSecurityConfig<RoleType> {
        return this.apply { unsecuredEndpoints.add(endpointSignature) }
    }

    override fun registerUnsecuredEndpoint(urlPath: String) : EndpointSecurityConfig<RoleType> {
        return registerUnsecuredEndpoint(EndpointSignature(path = urlPath))
    }

}