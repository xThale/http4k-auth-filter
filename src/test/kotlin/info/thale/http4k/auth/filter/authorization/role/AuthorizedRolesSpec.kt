package info.thale.http4k.auth.filter.authorization.role

import info.thale.http4k.auth.filter.authorization.role.matcher.RoleMatchingLogic
import info.thale.http4k.auth.filter.exception.AuthorizationException
import io.kotlintest.shouldNotThrow
import io.kotlintest.shouldThrow
import io.kotlintest.specs.DescribeSpec

class AuthorizedRolesSpec : DescribeSpec({

    describe("AuthorizedRolesSpec-Test") {

        it("Positive tests") {
            val authorizedRoles = AuthorizedRoles(listOf("Admin"), RoleMatchingLogic.EVERY)
            shouldNotThrow<AuthorizationException> {
                authorizedRoles.authorize(listOf("Admin"))
            }
        }

        it("Negative tests") {
            val authorizedRoles = AuthorizedRoles(listOf("Admin"), RoleMatchingLogic.EVERY)
            shouldThrow<AuthorizationException> {
                authorizedRoles.authorize(listOf("User"))
            }
        }

    }

})