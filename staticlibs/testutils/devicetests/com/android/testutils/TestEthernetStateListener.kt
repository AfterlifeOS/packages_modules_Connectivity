/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.testutils

import android.net.EthernetManager.ETHERNET_STATE_DISABLED
import android.net.EthernetManager.ETHERNET_STATE_ENABLED
import android.net.EthernetManager.InterfaceStateListener
import android.net.EthernetManager.ROLE_CLIENT
import android.net.EthernetManager.ROLE_NONE
import android.net.EthernetManager.ROLE_SERVER
import android.net.EthernetManager.STATE_ABSENT
import android.net.EthernetManager.STATE_LINK_DOWN
import android.net.EthernetManager.STATE_LINK_UP
import android.net.IpConfiguration
import com.android.net.module.util.TestableCallback
import com.android.testutils.TestEthernetStateListener.EthernetStateChanged
import com.android.testutils.TestInterfaceStateListener.InterfaceStateChanged
import java.util.function.IntConsumer

private val DEFAULT_IP_CONFIGURATION = IpConfiguration(
        IpConfiguration.IpAssignment.DHCP,
        IpConfiguration.ProxySettings.NONE,
        null,
        null
)

/**
 * A test listener for interface state.
 */
class TestInterfaceStateListener : TestableCallback<InterfaceStateChanged>(),
        InterfaceStateListener {
    data class InterfaceStateChanged(
            val iface: String,
            val state: Int,
            val role: Int,
            val configuration: IpConfiguration?
    ) {
        override fun toString(): String {
            val stateString = when (state) {
                STATE_ABSENT -> "STATE_ABSENT"
                STATE_LINK_UP -> "STATE_LINK_UP"
                STATE_LINK_DOWN -> "STATE_LINK_DOWN"
                else -> state.toString()
            }
            val roleString = when (role) {
                ROLE_NONE -> "ROLE_NONE"
                ROLE_CLIENT -> "ROLE_CLIENT"
                ROLE_SERVER -> "ROLE_SERVER"
                else -> role.toString()
            }
            return ("InterfaceStateChanged(iface=$iface, state=$stateString, " +
                    "role=$roleString, ipConfig=$configuration)")
        }
    }

    override fun onInterfaceStateChanged(
            iface: String,
            state: Int,
            role: Int,
            configuration: IpConfiguration?,
    ) {
        history.add(InterfaceStateChanged(iface, state, role, configuration))
    }

    fun expectInterfaceStateChanged(
        iface: String,
        state: Int,
        role: Int,
        timeoutMs: Long = defaultTimeoutMs
    ): InterfaceStateChanged = expect(timeoutMs = timeoutMs) {
        val expectedConfig = if (state != STATE_ABSENT) DEFAULT_IP_CONFIGURATION else null
        it.iface == iface && it.state == state && it.role == role &&
                it.configuration == expectedConfig
    }

    fun eventuallyExpectInterfaceStateChanged(
        iface: String,
        state: Int,
        role: Int,
        timeoutMs: Long = defaultTimeoutMs
    ): InterfaceStateChanged = eventuallyExpect(timeoutMs = timeoutMs) {
        val expectedConfig = if (state != STATE_ABSENT) DEFAULT_IP_CONFIGURATION else null
        it.iface == iface && it.state == state && it.role == role &&
                it.configuration == expectedConfig
    }
}

/**
 * A test listener for ethernet state.
 */
class TestEthernetStateListener : TestableCallback<EthernetStateChanged>(), IntConsumer {
    data class EthernetStateChanged(val state: Int) {
        override fun toString(): String {
            val stateString = when (state) {
                ETHERNET_STATE_ENABLED -> "ETHERNET_STATE_ENABLED"
                ETHERNET_STATE_DISABLED -> "ETHERNET_STATE_DISABLED"
                else -> state.toString()
            }
            return "EthernetStateChanged(state=$stateString)"
        }
    }

    override fun accept(state: Int) {
        history.add(EthernetStateChanged(state))
    }
}
