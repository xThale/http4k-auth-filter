package info.thale.http4k.auth.filter.authorization.role.matcher

import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.specs.DescribeSpec

class RoleMatchingStrategySpec : DescribeSpec({

    describe("${RoleMatchingStrategy::javaClass.name}-Test") {

        it("Return correct matcher for matching type") {
            RoleMatchingStrategy.getMatcher(RoleMatchingLogic.ANY).shouldBeInstanceOf<AnyRoleMatcher> {}
            RoleMatchingStrategy.getMatcher(RoleMatchingLogic.EVERY).shouldBeInstanceOf<EveryRoleMatcher> {}
        }

    }

})