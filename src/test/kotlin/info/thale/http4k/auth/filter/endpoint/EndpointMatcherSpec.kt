package info.thale.http4k.auth.filter.endpoint

import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.DescribeSpec
import org.http4k.core.Method
import org.http4k.core.Request

class EndpointMatcherSpec : DescribeSpec({

    describe("EndpointMatcherSpec-Test") {

        it("positive basic matching") {
            val endpointSignature = EndpointSignature(path = "/test/app")
            val request = Request(Method.GET, "/test/app")
            endpointSignature.match(request).shouldBeTrue()
        }
        it("negative basic matching") {
            val endpointSignature = EndpointSignature(path = "/test/app")
            val request = Request(Method.GET, "/here/app")
            endpointSignature.match(request).shouldBeFalse()
            val request2 = Request(Method.GET, "/test/app2")
            endpointSignature.match(request2).shouldBeFalse()
        }

        it("positive basic regex matching") {
            val endpointSignature = EndpointSignature(path = "/test/{var}/app")
            val request = Request(Method.GET, "/test/sagitahwtokha/app")
            endpointSignature.match(request).shouldBeTrue()
            val endpointSignature2 = EndpointSignature(path = "/test/{var}/app/{foo}")
            val request2 = Request(Method.GET, "/test/sagitahwtokha/app/safa")
            endpointSignature2.match(request2).shouldBeTrue()
        }
        it("negative basic regex matching") {
            val endpointSignature = EndpointSignature(path = "/test/{var}/app")
            val request = Request(Method.GET, "/test/sagitahwtokha/var2/app")
            endpointSignature.match(request).shouldBeFalse()
            val request2 = Request(Method.GET, "/test2/sagitahwtokha/app")
            endpointSignature.match(request2).shouldBeFalse()
        }

        it("positive basic method regex matching") {
            val endpointSignature = EndpointSignature(path = "/test/{var}/app", method = Method.GET)
            val request = Request(Method.GET, "/test/sagitahwtokha/app")
            endpointSignature.match(request).shouldBeTrue()
            val endpointSignature2 = EndpointSignature(path = "/test/{var}/other", method = Method.POST)
            val request2 = Request(Method.POST, "/test/sagitahwtokha/other")
            endpointSignature2.match(request2).shouldBeTrue()
        }
        it("negative basic method regex matching") {
            val endpointSignature = EndpointSignature(path = "/test/{var}/app", method = Method.GET)
            val request = Request(Method.POST, "/test/sagitahwtokha/app")
            endpointSignature.match(request).shouldBeFalse()
            val endpointSignature2 = EndpointSignature(path = "/test/{var}/other", method = Method.POST)
            val request2 = Request(Method.GET, "/test/sagitahwtokha/other")
            endpointSignature2.match(request2).shouldBeFalse()
        }

        it("positive basic query matching") {
            val endpointSignature = EndpointSignature(path = "/test/app")
            val request = Request(Method.GET, "/test/app?test=20")
            endpointSignature.match(request).shouldBeFalse()
            val endpointSignature1 = EndpointSignature(path = "/test/app", endpointMatchType = EndpointMatchType.ATLEAST)
            val request1 = Request(Method.GET, "/test/app?test=20")
            endpointSignature1.match(request1).shouldBeTrue()
            val endpointSignature2 = EndpointSignature(path = "/test/app", queries = listOf(Pair("test", "20")), endpointMatchType = EndpointMatchType.EXACT)
            val request2 = Request(Method.POST, "/test/app?test=20")
            endpointSignature2.match(request2).shouldBeTrue()
            val endpointSignature3 = EndpointSignature(path = "/test/app", queries = listOf(Pair("test", "20")), endpointMatchType = EndpointMatchType.ATLEAST)
            val request3 = Request(Method.DELETE, "/test/app?tests=20&test=20&test=10")
            endpointSignature3.match(request3).shouldBeTrue()
            val endpointSignature4 = EndpointSignature(path = "/test/app", queries = listOf(Pair("test", "20"), Pair("var", "test"), Pair("par", "user")), endpointMatchType = EndpointMatchType.EXACT)
            val request4 = Request(Method.PATCH, "/test/app?test=20&par=user&var=test")
            endpointSignature4.match(request4).shouldBeTrue()
        }
        it("negative basic query matching") {
            val endpointSignature = EndpointSignature(path = "/test/app", queries = listOf(Pair("test", "20")), endpointMatchType = EndpointMatchType.EXACT)
            val request = Request(Method.GET, "/test/app")
            endpointSignature.match(request).shouldBeFalse()
            val endpointSignature2 = EndpointSignature(path = "/test/app", queries = listOf(Pair("test", "20")), endpointMatchType = EndpointMatchType.EXACT)
            val request2 = Request(Method.GET, "/test/app?test=20&var=test")
            endpointSignature2.match(request2).shouldBeFalse()
            val endpointSignature3 = EndpointSignature(path = "/test/app", queries = listOf(Pair("test", "20")), endpointMatchType = EndpointMatchType.ATLEAST)
            val request3 = Request(Method.GET, "/test/app?tests=20&test=10")
            endpointSignature3.match(request3).shouldBeFalse()
            val endpointSignature4 = EndpointSignature(path = "/test/app", queries = listOf(Pair("test", "20"), Pair("var", "tes"), Pair("par", "user")), endpointMatchType = EndpointMatchType.EXACT)
            val request4 = Request(Method.GET, "/test/app?test=20&par=user&var=test")
            endpointSignature4.match(request4).shouldBeFalse()
        }
        it("positive complex matching") {
            val endpointSignature = EndpointSignature(method = Method.GET, path = "/test/{name}/roles", queries = listOf(Pair("app", "adminchannel"), Pair("page", "2")), endpointMatchType = EndpointMatchType.EXACT)
            val request = Request(Method.GET, "/test/username/roles?page=2&app=adminchannel")
            endpointSignature.match(request).shouldBeTrue()
            val request2 = Request(Method.GET, "/test/username/roles?page=2&app=adminchannel&wrongparam=false")
            endpointSignature.match(request2).shouldBeFalse()
            val request3 = Request(Method.POST, "/test/username/roles?page=2&app=adminchannel")
            endpointSignature.match(request3).shouldBeFalse()

            val endpointSignature2 = EndpointSignature(method = Method.GET, path = "/test/{name}", queries = listOf(Pair("app", "adminchannel"), Pair("page", "2")), endpointMatchType = EndpointMatchType.EXACT)
            val request4 = Request(Method.GET, "/test/testusername?page=2&app=adminchannel")
            endpointSignature2.match(request4).shouldBeTrue()
        }

    }

})

private fun EndpointSignature.match(request: Request) : Boolean {
    return this.matcher.match(request)
}