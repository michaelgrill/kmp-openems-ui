package io.openems.kmp.edge.api.model

import kotlin.jvm.JvmInline

sealed interface ChannelValue

@JvmInline
value class IntChannelValue(val value: Int) : ChannelValue

@JvmInline
value class DoubleChannelValue(val value: Double) : ChannelValue

@JvmInline
value class StringChannelValue(val value: String) : ChannelValue