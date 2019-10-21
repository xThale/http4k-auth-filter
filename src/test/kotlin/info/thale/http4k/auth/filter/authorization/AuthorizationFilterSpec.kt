package info.thale.http4k.auth.filter.authorization

import info.thale.http4k.auth.filter.authorization.role.AuthorizedRoles
import info.thale.http4k.auth.filter.authorization.role.RoleBased
import info.thale.http4k.auth.filter.config.AuthFilterConfiguration
import info.thale.http4k.auth.filter.exception.AuthorizationException
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.DescribeSpec
import io.mockk.spyk
import io.mockk.verify
import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey

class AuthorizationFilterSpec : DescribeSpec({

    data class User(val name: String, val roles: List<String>) : RoleBased<String> {
        override fun getRoleList(): List<String> {
            return roles
        }
    }

    describe("AuthorizationFilterSpec-Test") {

        val requestContexts = RequestContexts()
        val context = RequestContextKey.required<User>(requestContexts)

        val user = User("user", listOf("Test"))
        val request = Request(Method.GET, "/home").header("Authorization", "Bearer token")

        fun buildAuthorizationFilterSpy(config: AuthFilterConfiguration<User, String>): AuthorizationFilter<User, String> {
            return spyk(AuthorizationFilter(config), recordPrivateCalls = true)
        }

        fun buildAuthenticationFilter(): Filter {
            return ServerFilters.InitialiseRequestContext(requestContexts)
                    .then(ServerFilters.BearerAuth(context) { if (it == "token") user else null})
        }

        fun buildAuthorizationFilter(filter: AuthorizationFilter<User, String>): HttpHandler {
            return filter.authorize(context).then { Response(Status.OK) }
        }

        fun buildFilterChain(filter: AuthorizationFilter<User, String>): HttpHandler {
            return buildAuthenticationFilter().then(filter.authorize(context)).then { Response(Status.OK) }
        }

        it("Endpoint is not secured") {
            val config = AuthFilterConfiguration<User, String>()
            config.endpointSecurityConfig.registerUnsecuredEndpoint("/home")
            val auth = buildAuthorizationFilterSpy(config)
            val filter = buildAuthorizationFilter(auth)

            val response = filter(request)

            verify(exactly = 1) {
                auth["endpointIsSecured"](any<Request>())
            }

            verify(exactly = 0) {
                auth["authorizedRoles"](any<Request>())
            }

            response.status.shouldBe(Status.OK)
        }

        it("Endpoint is secured with no roles configured") {
            val config = AuthFilterConfiguration<User, String>()
            val auth = buildAuthorizationFilterSpy(config)
            val filter = buildAuthorizationFilter(auth)

            val response = filter(request)

            verify(exactly = 1) {
                auth["endpointIsSecured"](any<Request>())
                auth["authorizedRoles"](any<Request>())
            }

            verify(exactly = 0) {
                auth["checkIfContextObjectHasAuthorizedRoles"](any<AuthorizedRoles<String>>(), any<RoleBased<String>>())
            }

            response.status.shouldBe(Status.OK)
        }

        it("Endpoint is secured with roles and user has the required roles") {
            val config = AuthFilterConfiguration<User, String>()
            config.endpointSecurityConfig.registerRolesForEndpoint("/home", "Test")
            val auth = buildAuthorizationFilterSpy(config)
            val filter = buildFilterChain(auth)

            val response = filter(request)

            verify(exactly = 1) {
                auth["endpointIsSecured"](any<Request>())
                auth["authorizedRoles"](any<Request>())
                auth["checkIfContextObjectHasAuthorizedRoles"](any<AuthorizedRoles<String>>(), any<RoleBased<String>>())
            }

            response.status.shouldBe(Status.OK)
        }

        it("Endpoint is secured with roles and user has not the required roles") {
            val config = AuthFilterConfiguration<User, String>()
            config.endpointSecurityConfig.registerRolesForEndpoint("/home", "Admin")
            val auth = buildAuthorizationFilterSpy(config)
            val filter = buildFilterChain(auth)

            shouldThrow<AuthorizationException> {
                filter(request)
            }.message.shouldBe("User lacks the necessary role for this endpoint")

            verify(exactly = 1) {
                auth["endpointIsSecured"](any<Request>())
                auth["authorizedRoles"](any<Request>())
                auth["checkIfContextObjectHasAuthorizedRoles"](any<AuthorizedRoles<String>>(), any<RoleBased<String>>())
            }
        }

    }

})