package info.thale.http4k.auth.filter.authorization.role.matcher



/**
 * How the required roles are matched with the actual roles
 */
enum class RoleMatchingLogic {

    /**
     * The user must have all roles in the list
     */
    EVERY,

    /**
     * The user must have any role in the list
     */
    ANY

}