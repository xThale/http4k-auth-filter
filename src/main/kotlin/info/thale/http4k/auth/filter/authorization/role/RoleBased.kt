package info.thale.http4k.auth.filter.authorization.role

interface RoleBased<RoleType> {
    fun getRoleList() : List<RoleType>
}