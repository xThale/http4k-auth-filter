package info.thale.http4k.auth.filter.exception

import com.auth0.jwt.interfaces.DecodedJWT

/**
 * Thrown if an exception occurred in the authentication of a google jwt token.
 */
class GoogleTokenAuthenticationException(jwt: DecodedJWT, message: String) :
        AuthenticationException("Jwt token $jwt could not be successfully authenticated. Maybe it is invalid or expired. Message: $message")