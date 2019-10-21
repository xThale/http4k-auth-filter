package info.thale.http4k.auth.filter.authorization.role.matcher

class AnyRoleMatcher : RoleMatcher {

    override fun <RoleType> match(requiredAuthorizedRoles: List<RoleType>, roles: List<RoleType>) : Boolean {
        return requiredAuthorizedRoles.isEmpty() || hasElement(requiredAuthorizedRoles, roles)
    }

    private fun <RoleType> hasElement(requiredAuthorizedRoles: List<RoleType>, roles: List<RoleType>) : Boolean {
        return requiredAuthorizedRoles.firstOrNull { roles.contains(it) }?.let { true } ?: false
    }

}