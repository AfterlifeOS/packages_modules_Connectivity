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
import com.android.net.module.util.ArrayTrackRecord
import com.android.net.module.util.TrackRecord
import java.util.function.IntConsumer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val TIMEOUT_MS = 10_000L
private const val NO_CALLBACK_TIMEOUT_MS = 500L

private val DEFAULT_IP_CONFIGURATION = IpConfiguration(
        IpConfiguration.IpAssignment.DHCP,
        IpConfiguration.ProxySettings.NONE,
        null,
        null
)

/**
 * A listener for both ethernet interface state and ethernet feature state.
 *
 * This class can be used to track both onInterfaceStateChanged and onEthernetStateChanged
 * callbacks from EthernetManager.
 */
open class EthernetStateListener private constructor(
    private val history: ArrayTrackRecord<Event>
) : InterfaceStateListener, IntConsumer, TrackRecord<EthernetStateListener.Event> by history {
    constructor() : this(ArrayTrackRecord())

    val events = history.newReadHead()

    sealed class Event {
       data class InterfaceStateChanged(
               val iface: String,
               val state: Int,
               val role: Int,
               val configuration: IpConfiguration?
       ) : Event() {
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

        data class EthernetStateChanged(val state: Int) : Event() {
            override fun toString(): String {
                val stateString = when (state) {
                    ETHERNET_STATE_ENABLED -> "ETHERNET_STATE_ENABLED"
                    ETHERNET_STATE_DISABLED -> "ETHERNET_STATE_DISABLED"
                    else -> state.toString()
                }
                return "EthernetStateChanged(state=$stateString)"
            }
        }
    }

    override fun onInterfaceStateChanged(
            iface: String,
            state: Int,
            role: Int,
            cfg: IpConfiguration?
    ) {
        add(Event.InterfaceStateChanged(iface, state, role, cfg))
    }

    override fun accept(state: Int) {
        add(Event.EthernetStateChanged(state))
    }

    fun <T : Event> expectCallback(expected: T): T {
        val event = events.poll(TIMEOUT_MS)
        assertEquals(expected, event)
        return event as T
    }

    fun expectCallback(ifaceName: String, state: Int, role: Int) {
        expectCallback(createChangeEvent(ifaceName, state, role))
    }

    fun expectCallback(state: Int) {
        expectCallback(Event.EthernetStateChanged(state))
    }

    private fun createChangeEvent(iface: String, state: Int, role: Int) =
            Event.InterfaceStateChanged(
                    iface,
                    state,
                    role,
                    if (state != STATE_ABSENT) DEFAULT_IP_CONFIGURATION else null
            )

    fun eventuallyExpect(expected: Event) {
        val cb = events.poll(TIMEOUT_MS) { it == expected }
        assertNotNull(cb, "Never received expected $expected. Received: ${events.backtrace()}")
    }

    fun eventuallyExpect(ifaceName: String, state: Int, role: Int) {
        eventuallyExpect(createChangeEvent(ifaceName, state, role))
    }

    fun eventuallyExpect(state: Int) {
        eventuallyExpect(Event.EthernetStateChanged(state))
    }

    fun assertNoCallback() {
        val cb = events.poll(NO_CALLBACK_TIMEOUT_MS)
        assertNull(cb, "Expected no callback but got $cb")
    }
}
