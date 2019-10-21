package info.thale.http4k.auth.filter.authorization.role

import info.thale.http4k.auth.filter.authorization.role.matcher.RoleMatchingLogic
import info.thale.http4k.auth.filter.authorization.role.matcher.RoleMatchingStrategy
import info.thale.http4k.auth.filter.exception.AuthorizationException

/**
 * Holds a list of required roles with the corresponding [RoleMatchingLogic]
 */
class AuthorizedRoles<RoleType> (val requiredRoles: List<RoleType>,
                                 private val roleMatchingLogic: RoleMatchingLogic = RoleMatchingLogic.ANY) {

    fun authorize(rolesOfContextObject: List<RoleType>) {
        val unauthorized = RoleMatchingStrategy.getMatcher(roleMatchingLogic).match(requiredRoles, rolesOfContextObject).not()

        if (unauthorized) {
            throw AuthorizationException("User lacks the necessary role for this endpoint")
        }
    }

}

