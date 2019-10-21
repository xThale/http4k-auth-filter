package info.thale.http4k.auth.filter.authorization.role.matcher

class EveryRoleMatcher : RoleMatcher {

    override fun <RoleType> match(requiredAuthorizedRoles: List<RoleType>, roles: List<RoleType>) : Boolean {
        return emptyRequiredRoles(requiredAuthorizedRoles) ||
                (requiredAuthorizedRoles.size == roles.size && requiredAuthorizedRoles.containsAll(roles))
    }

    private fun <RoleType> emptyRequiredRoles(requiredAuthorizedRoles: List<RoleType>): Boolean {
        return requiredAuthorizedRoles.isEmpty()
    }

}