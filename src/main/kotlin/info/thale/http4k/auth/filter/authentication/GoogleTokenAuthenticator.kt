package info.thale.http4k.auth.filter.authentication

import com.auth0.jwt.interfaces.DecodedJWT
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import info.thale.http4k.auth.filter.exception.GoogleTokenAuthenticationException

/**
 * The [IssuerTokenAuthenticator] for authenticating a google jwt token.
 *
 * @param ContextObjectType The type of your context object, which can later be extracted from the request
 * @property clientId Validates if the client id is contained in the token
 * @property tokenToContextObjectFunction Method to convert a [DecodedJWT] into your context object [ContextObjectType]
 */
class GoogleTokenAuthenticator<ContextObjectType>(private val clientId: String,
                                  private val tokenToContextObjectFunction: ((DecodedJWT) -> ContextObjectType)) : IssuerTokenAuthenticator<ContextObjectType> {

    private val jsonFactory : JsonFactory = JacksonFactory()
    private val httpTransport : HttpTransport = NetHttpTransport()

    /**
     * Checks if the audience is correct and if the token is still valid.
     *
     * @param jwt The JWT token
     * @throws [GoogleTokenAuthenticationException] If the token is not valid
     */
    override fun authenticateToken(jwt: DecodedJWT): GoogleTokenAuthenticator<ContextObjectType> {
        try {
            tokenToIdToken(jwt).let { idToken ->
                verifyAudience(idToken)
                verifyToken(idToken)
            }
            return this
        } catch (e : Exception) {
            throw GoogleTokenAuthenticationException(jwt, e.message ?: "Unexpected error")
        }
    }

    private fun tokenToIdToken(jwt: DecodedJWT): GoogleIdToken {
        return GoogleIdToken.parse(jsonFactory, jwt.token)
    }

    private fun verifyAudience(idToken: GoogleIdToken) {
        if (!idToken.verifyAudience(listOf(clientId))) {
            throw Exception("Token is not intended for this application")
        }
    }

    private fun verifyToken(idToken: GoogleIdToken) {
        if (!GoogleIdTokenVerifier(httpTransport, jsonFactory).verify(idToken)) {
            throw Exception("Token is not valid for google authentication")
        }
    }


    override fun tokenToContextObject(jwt: DecodedJWT): ContextObjectType = tokenToContextObjectFunction(jwt)

}