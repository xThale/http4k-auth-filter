package info.thale.http4k.auth.filter.exception

/**
 * Thrown if the issuer of the token was not found in the configuration.
 */
class IssuerNotFoundException(issuer: String) : AuthenticationException("Issuer $issuer was not found for this bearer token")