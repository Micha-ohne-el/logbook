package moe.micha.logbook

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

fun DescribeSpec.describe(member: KCallable<*>, test: suspend DescribeSpecContainerScope.() -> Unit) =
	describe(member.name, test)

fun DescribeSpec.describe(kClass: KClass<*>, test: suspend DescribeSpecContainerScope.() -> Unit) =
	describe(kClass.simpleName!!, test)

suspend fun DescribeSpecContainerScope.describe(member: KCallable<*>, test: suspend DescribeSpecContainerScope.() -> Unit) =
	describe(member.name, test)

suspend fun DescribeSpecContainerScope.describe(member: KClass<*>, test: suspend DescribeSpecContainerScope.() -> Unit) =
	describe(member.simpleName!!, test)
