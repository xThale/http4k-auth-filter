package info.thale.http4k.auth.filter.config

import info.thale.http4k.auth.filter.authorization.role.AuthorizedRoles
import info.thale.http4k.auth.filter.authorization.role.matcher.RoleMatchingLogic
import info.thale.http4k.auth.filter.endpoint.EndpointSignature
import org.http4k.core.Request

interface EndpointSecurityConfig<RoleType> {

    /**
     * Returns the required roles for a request, based on the registered [EndpointSignature].
     * If the endpoint is not registered, a empty list is returned instead.
     */
    fun getRolesForEndpoint(request: Request): AuthorizedRoles<RoleType>

    /**
     * Secures the authorization of an endpoint based on the provided roles.
     * @param endpointSignature Identifies one or more request depending on their attributes. See [EndpointSignature]
     * @param requiredRoles a list of roles required
     * @param matchingLogic how the user roles are matched with the required roles
     */
    fun registerRolesForEndpoint(endpointSignature: EndpointSignature, requiredRoles: List<RoleType>, matchingLogic: RoleMatchingLogic) : EndpointSecurityConfig<RoleType>

    /**
     * Secures the authorization of an endpoint based on the provided role.
     * @param endpointSignature Identifies one or more request depending on their attributes. See [EndpointSignature]
     * @param requiredRole the role required for this endpoint signature
     */
    fun registerRolesForEndpoint(endpointSignature: EndpointSignature, requiredRole: RoleType) : EndpointSecurityConfig<RoleType>

    /**
     * Secures the authorization of an endpoint based on the provided roles.
     * @param urlPath Identifies one or more request based on the path. See [EndpointSignature]
     * @param requiredRoles a list of roles required
     * @param matchingLogic how the user roles are matched with the required roles
     */
    fun registerRolesForEndpoint(urlPath: String, requiredRoles: List<RoleType>, matchingLogic: RoleMatchingLogic) : EndpointSecurityConfig<RoleType>

    /**
     * Secures the authorization of an endpoint based on the provided role.
     * @param urlPath Identifies one or more request based on the path. See [EndpointSignature]
     * @param requiredRole the role required for this endpoint signature
     */
    fun registerRolesForEndpoint(urlPath: String, requiredRole: RoleType) : EndpointSecurityConfig<RoleType>



    /**
     * Checks if endpoint is secured. The endpoint is not secured, if it is registered as unsecured. See [registerUnsecuredEndpoint].
     * @param request The to be checked request
     */
    fun isSecuredEndpoint(request: Request) : Boolean

    /**
     * Checks if endpoint is unsecured. The endpoint is not secured, if it is registered as such. See [registerUnsecuredEndpoint].
     * @param request The to be checked request
     */
    fun isUnsecuredEndpoint(request: Request) : Boolean

    /**
     * Registers an endpoint as unsecured. Every request against this endpoint will not be authenticated nor authorized.
     * @param endpointSignature Identifies the unsecured endpoint
     */
    fun registerUnsecuredEndpoint(endpointSignature: EndpointSignature) : EndpointSecurityConfig<RoleType>

    /**
     * Registers an endpoint as unsecured. Every request against this endpoint will not be authenticated nor authorized.
     * @param urlPath url path of the unsecured endpoint
     */
    fun registerUnsecuredEndpoint(urlPath: String) : EndpointSecurityConfig<RoleType>


}