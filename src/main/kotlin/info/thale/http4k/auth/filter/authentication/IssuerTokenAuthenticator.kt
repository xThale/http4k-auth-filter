package info.thale.http4k.auth.filter.authentication

import com.auth0.jwt.interfaces.DecodedJWT
import info.thale.http4k.auth.filter.exception.AuthenticationException

/**
 * Authenticates and parses a JWT-Token into the required context object.
 *
 * @param ContextObjectType Type of the context object, the token will be parsed in.
 */
interface IssuerTokenAuthenticator<ContextObjectType> {

    /**
     * Checks if the JWT-Token is correct and still valid.
     *
     * @param jwt The [DecodedJWT]
     * @throws [AuthenticationException] The token is not valid
     */
    fun authenticateToken(jwt: DecodedJWT) : IssuerTokenAuthenticator<ContextObjectType>

    /**
     * Parses the JWT-Token into the context object which will be put into the request. This method is provided by the user.
     */
    fun tokenToContextObject (jwt: DecodedJWT) : ContextObjectType

}