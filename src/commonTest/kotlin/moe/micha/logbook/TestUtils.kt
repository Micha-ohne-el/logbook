package moe.micha.logbook

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

fun DescribeSpec.describe(member: KCallable<*>, test: suspend DescribeSpecContainerScope.() -> Unit) =
	describe(member.name.normalize(), test)

fun DescribeSpec.describe(kClass: KClass<*>, test: suspend DescribeSpecContainerScope.() -> Unit) =
	describe(kClass.simpleName!!.normalize(), test)

suspend fun DescribeSpecContainerScope.describe(member: KCallable<*>, test: suspend DescribeSpecContainerScope.() -> Unit) =
	describe(member.name.normalize(), test)

suspend fun DescribeSpecContainerScope.describe(member: KClass<*>, test: suspend DescribeSpecContainerScope.() -> Unit) =
	describe(member.simpleName!!.normalize(), test)

private fun String.normalize() = replace("<", "").replace(">", "")
