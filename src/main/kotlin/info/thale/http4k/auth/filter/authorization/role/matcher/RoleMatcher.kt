package info.thale.http4k.auth.filter.authorization.role.matcher

interface RoleMatcher {
    fun <RoleType> match(requiredAuthorizedRoles: List<RoleType>, roles: List<RoleType>): Boolean
}