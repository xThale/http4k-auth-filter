package info.thale.http4k.auth.filter.config

import info.thale.http4k.auth.filter.authorization.role.matcher.RoleMatchingLogic
import info.thale.http4k.auth.filter.endpoint.EndpointMatchType
import info.thale.http4k.auth.filter.endpoint.EndpointSignature
import io.kotlintest.data.forall
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.kotlintest.tables.row
import org.http4k.core.Method
import org.http4k.core.Request

class DefaultEndpointSecurityConfigSpec : DescribeSpec({

    describe("Register roles for endpoints") {

        it("Roles for endpoint are returned correctly") {
            val config = createNewConfig()
            val roleName = "Role"
            val expected = listOf(roleName)

            config.registerRolesForEndpoint("/test/1", "Role")
            val actual = config.getRolesForEndpoint(Request(Method.GET, "/test/1")).requiredRoles

            actual.shouldBe(expected)
        }

        it("Roles should be null if the endpoint is not registered") {
            val config = createNewConfig()

            config.registerRolesForEndpoint("/test/1", "Role")
            val actual = config.getRolesForEndpoint(Request(Method.GET, "/test/2")).requiredRoles

            actual.shouldBeEmpty()
        }

        it("Excessive regex testing") {
            val config = createNewConfig()
            val deRoles = listOf("DE", "ADMIN")
            val esRoles = listOf("ES", "ADMIN")
            val beRoles = listOf("BE", "ADMIN")
            val trRoles = listOf("TR", "ADMIN")
            val etcRoles = listOf("ADMIN")

            forall(
                    row(null, "/country/(?i)de", emptyList(), EndpointMatchType.ATLEAST, deRoles),
                    row(null, "/country/es", emptyList(), EndpointMatchType.ATLEAST, esRoles),
                    row(Method.POST, "/country/be", emptyList(), EndpointMatchType.ATLEAST, beRoles),
                    row(null, "/country/tr", listOf(Pair("index", "3")), EndpointMatchType.EXACT, trRoles),
                    row(null, "/country/{country}", emptyList(), EndpointMatchType.ATLEAST, etcRoles)
            ) { method, urlPath, queries, matchType, roles ->
                config.registerRolesForEndpoint(EndpointSignature(method = method, path = urlPath, queries = queries, endpointMatchType = matchType), roles, RoleMatchingLogic.ANY)
            }

            config.getRolesForEndpoint(Request(Method.GET, "/country/tr")).requiredRoles.shouldBe(etcRoles)
            config.getRolesForEndpoint(Request(Method.DELETE, "/country/tr")).requiredRoles.shouldBe(etcRoles)
            config.getRolesForEndpoint(Request(Method.DELETE, "/country/tr?index=4")).requiredRoles.shouldBe(etcRoles)
            config.getRolesForEndpoint(Request(Method.DELETE, "/country/tr?index=4&page=3")).requiredRoles.shouldBe(etcRoles)
            config.getRolesForEndpoint(Request(Method.DELETE, "/country/tr?index=3")).requiredRoles.shouldBe(trRoles)

            config.getRolesForEndpoint(Request(Method.GET, "/country/be")).requiredRoles.shouldBe(etcRoles)
            config.getRolesForEndpoint(Request(Method.POST, "/country/be")).requiredRoles.shouldBe(beRoles)

            config.getRolesForEndpoint(Request(Method.GET, "/country/es")).requiredRoles.shouldBe(esRoles)
            config.getRolesForEndpoint(Request(Method.GET, "/country/es?test=2")).requiredRoles.shouldBe(esRoles)

            config.getRolesForEndpoint(Request(Method.GET, "/country/de")).requiredRoles.shouldBe(deRoles)
            config.getRolesForEndpoint(Request(Method.GET, "/country/De")).requiredRoles.shouldBe(deRoles)
            config.getRolesForEndpoint(Request(Method.GET, "/country/Dee")).requiredRoles.shouldBe(etcRoles)

            config.getRolesForEndpoint(Request(Method.GET, "/country/different")).requiredRoles.shouldBe(etcRoles)
        }
    }


    describe("DefaultEndpointSecurityConfigSpec: Register unsecured endpoints") {

        it("Register endpoint as unsecured, then check if the endpoint is unsecured") {
            val config = DefaultEndpointSecurityConfig<String>()
            config.registerUnsecuredEndpoint("/home")
            config.isUnsecuredEndpoint(Request(Method.GET, "/home")).shouldBeTrue()
            config.isSecuredEndpoint(Request(Method.GET, "/home")).shouldBeFalse()
        }

        it("Register endpoint as unsecured with regex, then check if the endpoint is unsecured") {
            val config = DefaultEndpointSecurityConfig<String>()
            config.registerUnsecuredEndpoint("/home/{page}")
            config.isUnsecuredEndpoint(Request(Method.GET, "/home/test")).shouldBeTrue()
        }

    }

})

private fun createNewConfig() : DefaultEndpointSecurityConfig<String> {
    return DefaultEndpointSecurityConfig()
}