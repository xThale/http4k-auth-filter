package info.thale.http4k.auth.filter.authentication

import info.thale.http4k.auth.filter.authorization.role.RoleBased
import info.thale.http4k.auth.filter.config.AuthFilterConfiguration
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.mockk.every
import io.mockk.spyk
import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey

class AuthenticationFilterSpec : DescribeSpec({

    class User(val name: String) : RoleBased<String> {
        override fun getRoleList(): List<String> {
            return emptyList()
        }
    }

    val contexts = RequestContexts()
    val contextKey = RequestContextKey.required<User>(contexts)
    val plainRequest = Request(Method.GET, "/home")
    val authenticatedRequest = Request(Method.GET, "/home").header("Authorization", "Bearer token")

    describe("AuthenticationFilterSpec-Test") {

        it("Request with empty authentication header returns unauthorized response") {
            val config = AuthFilterConfiguration<User, String>()
            val http = AuthenticationFilter(config).authenticate(contextKey).then {Response(Status.OK)}

            val response = http(plainRequest)

            response.status.shouldBe(Status.UNAUTHORIZED)
        }

        it("Request to an unsecured endpoint returns ok response") {
            val config = AuthFilterConfiguration<User, String>()
            config.endpointSecurityConfig.registerUnsecuredEndpoint("/home")
            val http = AuthenticationFilter(config).authenticate(contextKey).then {Response(Status.OK)}

            val response = http(plainRequest)

            response.status.shouldBe(Status.OK)
        }

        it("Request with authentication header returns ok response") {
            val config = AuthFilterConfiguration<User, String>()
            val spy = spyk(AuthenticationFilter(config))
            val http = ServerFilters.InitialiseRequestContext(contexts)
                    .then(spy.authenticate(contextKey))
                    .then { request -> Response(Status.OK).body(contextKey(request).name) }

            every {
                spy["tokenToContextObject"](any<String>())
            } answers {
                User("TestUser")
            }

            val response = http(authenticatedRequest)


            response.status.shouldBe(Status.OK)
            response.bodyString().shouldBe("TestUser")
        }

    }

})