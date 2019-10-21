package info.thale.http4k.auth.filter.config

import com.auth0.jwt.interfaces.DecodedJWT
import info.thale.http4k.auth.filter.authentication.GoogleTokenAuthenticator
import info.thale.http4k.auth.filter.authentication.IssuerTokenAuthenticator

class RegisteredIssuerTokenAuthenticators<AuthContextType> (
        private val issuerTokenAuthenticators : MutableMap<String, IssuerTokenAuthenticator<AuthContextType>> = mutableMapOf()) {

    /**
     * Returns the [IssuerTokenAuthenticator] for a issuer text
     */
    fun getAuthenticator(issuer: String)
            = issuerTokenAuthenticators[issuer]

    /**
     * Registers a [IssuerTokenAuthenticator] which is contained in the [SupportedIssuer]
     */
    fun registerTokenAuthenticator(issuer: SupportedIssuer, authenticator: IssuerTokenAuthenticator<AuthContextType>)
            = registerTokenAuthenticator(issuer.issuerText, authenticator)

    /**
     * Registers a [IssuerTokenAuthenticator] for a custom issuer
     */
    fun registerTokenAuthenticator(issuerText: String, authenticator: IssuerTokenAuthenticator<AuthContextType>)
            = this.apply { issuerTokenAuthenticators[issuerText] = authenticator }

    /**
     * Creates and registers a [GoogleTokenAuthenticator]
     */
    fun registerGoogleTokenAuthenticator(clientId: String, tokenToContextObjectFunction : ((DecodedJWT) -> AuthContextType))
            = registerTokenAuthenticator(SupportedIssuer.GOOGLE, GoogleTokenAuthenticator(clientId, tokenToContextObjectFunction))

}