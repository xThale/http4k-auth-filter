package info.thale.http4k.auth.filter.config

import info.thale.http4k.auth.filter.authentication.IssuerTokenAuthenticator
import info.thale.http4k.auth.filter.authorization.role.RoleBased

/**
 * The configuration class for the authentication and authorization.
 * Via this class you register new [IssuerTokenAuthenticator], secure routes via roles and set unsecured routes.
 * @param AuthContextType your context type which will be put into the request, e. g. a user class.
 */
class AuthFilterConfiguration<AuthContextType : RoleBased<RoleType>, RoleType>(
        val endpointSecurityConfig: EndpointSecurityConfig<RoleType> = DefaultEndpointSecurityConfig(),
        val issuerTokenAuthenticators: RegisteredIssuerTokenAuthenticators<AuthContextType> = RegisteredIssuerTokenAuthenticators())