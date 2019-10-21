package info.thale.http4k.auth.filter.exception

class JWTParseException(token: String) : Exception("Token '$token' could not be parsed")