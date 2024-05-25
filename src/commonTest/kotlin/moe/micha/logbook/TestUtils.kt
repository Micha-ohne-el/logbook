package moe.micha.logbook

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope
import kotlin.reflect.KCallable

/**
 * For specifying describe-blocks by reference instead of by name, so that automatic refactoring works with it.
 */
fun DescribeSpec.describe(member: KCallable<*>, test: suspend DescribeSpecContainerScope.() -> Unit) =
	describe(member.name, test)
