package info.thale.http4k.auth.filter.config

/**
 * Supported Issuer. However custom issuer can be added via [AuthFilterConfiguration]
 */
enum class SupportedIssuer(val issuerText: String) {

    GOOGLE("https://accounts.google.com");

    override fun toString() = issuerText
}