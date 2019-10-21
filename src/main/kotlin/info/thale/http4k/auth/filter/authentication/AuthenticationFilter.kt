package info.thale.http4k.auth.filter.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.interfaces.DecodedJWT
import info.thale.http4k.auth.filter.authorization.role.RoleBased
import info.thale.http4k.auth.filter.config.AuthFilterConfiguration
import info.thale.http4k.auth.filter.exception.AuthenticationException
import info.thale.http4k.auth.filter.exception.IssuerNotFoundException
import info.thale.http4k.auth.filter.exception.JWTParseException
import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextLens

/**
 * A Filter for validating OAuth tokens from the authentication header of a request.
 *
 * Parses the authentication header into a JWT-Token. Authentication is done based on the issuer in the token via a [IssuerTokenAuthenticator].
 * The required [IssuerTokenAuthenticator] have to be registered in the configuration.
 * If the issuer is not found in the configuration, it throws an [IssuerNotFoundException].
 *
 * @param ContextObjectType Type of the context object, which the token will be parsed in.
 * @property authFilterConfiguration Configuration for validating the request
 * @since 1.0.0
 */
class AuthenticationFilter<ContextObjectType : RoleBased<RoleType>, RoleType>(private val authFilterConfiguration : AuthFilterConfiguration<ContextObjectType, RoleType>) {

    /**
     * Validates a token. If the endpoint is configured to be unsecured, the access will be granted. If not,
     * the token will be parsed and validated based on the issuer field in the JWT-Token.
     *
     * @param context [RequestContextLens]
     */
    fun authenticate(context: RequestContextLens<ContextObjectType>): Filter {
        return Filter {
            next -> {
                request -> (
                    try {
                        validateRequestFromContext(context, next, request)
                    } catch (e: AuthenticationException) {
                        throw e
                    }catch (e: Exception) {
                        throw AuthenticationException(e.message ?: "Error occurred while authorizing request", e)
                    }

                )
            }
        }
    }

    private fun validateRequestFromContext(context: RequestContextLens<ContextObjectType>, next: HttpHandler, request: Request): Response {
        return if (endpointIsSecured(request)) {
            validateOAuthToken(context).then(next)(request)
        } else {
            next(request)
        }
    }

    private fun endpointIsSecured(request: Request): Boolean {
        return authFilterConfiguration.endpointSecurityConfig.isSecuredEndpoint(request)
    }

    private fun validateOAuthToken(context: RequestContextLens<ContextObjectType>): Filter {
        return ServerFilters.BearerAuth.invoke(context) { token ->
            tokenToContextObject(token)
        }
    }

    private fun tokenToContextObject(token: String): ContextObjectType {
        val jwt = decodeTokenToJWT(token)
        return getTokenAuthenticatorForIssuer(jwt.issuer)
                .authenticateToken(jwt)
                .tokenToContextObject(jwt)
    }

    private fun decodeTokenToJWT(token: String): DecodedJWT {
        return try {
            JWT.decode(token)
        } catch (e: JWTDecodeException) {
            throw JWTParseException(token)
        }
    }

    private fun getTokenAuthenticatorForIssuer(issuer: String): IssuerTokenAuthenticator<ContextObjectType> {
        return authFilterConfiguration.issuerTokenAuthenticators.getAuthenticator(issuer) ?: throw IssuerNotFoundException(issuer)
    }



}