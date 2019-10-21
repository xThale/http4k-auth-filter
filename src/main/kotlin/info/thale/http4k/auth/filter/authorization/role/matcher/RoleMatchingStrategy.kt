package info.thale.http4k.auth.filter.authorization.role.matcher

class RoleMatchingStrategy {

    companion object {
        fun getMatcher(matchLogic: RoleMatchingLogic) : RoleMatcher {
            return when(matchLogic) {
                RoleMatchingLogic.EVERY -> {
                    EveryRoleMatcher()
                }
                RoleMatchingLogic.ANY -> {
                    AnyRoleMatcher()
                }
            }
        }
    }

}