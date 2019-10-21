package info.thale.http4k.auth.filter.authorization.role.matcher

import io.kotlintest.data.forall
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.DescribeSpec
import io.kotlintest.tables.row

class EveryRoleMatcherSpec : DescribeSpec({

    val matcher = EveryRoleMatcher()

    data class Role(val number: Int)

    describe("${EveryRoleMatcher::javaClass.name}-Test") {

        it("Positive Matching") {

            forall(
                    row(emptyList(), emptyList<Any>()),
                    row(emptyList(), listOf("User")),
                    row(listOf("User"), listOf("User")),
                    row(listOf("User", "Admin", "Maintenance"), listOf("User", "Admin", "Maintenance"))
            ) { anyRequiredRoles, possessedRoles ->
                matcher.match(anyRequiredRoles, possessedRoles).shouldBeTrue()
            }

        }

        it("Negative Matching") {

            forall(
                    row(listOf("User"), emptyList()),
                    row(listOf("User", "Admin", "Maintenance"), emptyList()),
                    row(listOf("User"), listOf("Admin")),
                    row(listOf("User", "Admin", "Maintenance"), listOf("Test")),
                    row(listOf("User"), listOf("user")),
                    row(listOf("User", "Admin", "Maintenance"), listOf("Test", "Admin", "Maintenance"))
            ) { anyRequiredRoles, possessedRoles ->
                matcher.match(anyRequiredRoles, possessedRoles).shouldBeFalse()
            }

        }

        it("Positive Own Role Class matching") {

            forall(
                    row(emptyList(), listOf(Role(1))),
                    row(listOf(Role(1)), listOf(Role(1))),
                    row(listOf(Role(1), Role(2), Role(3)), listOf(Role(1), Role(2), Role(3)))
            ) { anyRequiredRoles, possessedRoles ->
                matcher.match(anyRequiredRoles, possessedRoles).shouldBeTrue()
            }

        }

        it("Negative Own Role Class matching") {

            forall(
                    row(listOf(Role(1)), emptyList()),
                    row(listOf(Role(1)), listOf(Role(2))),
                    row(listOf(Role(1), Role(2)), listOf(Role(1), Role(3)))
            ) { anyRequiredRoles, possessedRoles ->
                matcher.match(anyRequiredRoles, possessedRoles).shouldBeFalse()
            }

        }
    }

})