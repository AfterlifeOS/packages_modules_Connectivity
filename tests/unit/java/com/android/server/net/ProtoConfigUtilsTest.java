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

package com.android.server.net;

import static org.junit.Assert.assertEquals;

import android.net.EthernetConfiguration;
import android.net.LinkAddress;
import android.os.Build;

import com.android.server.network.configstore.proto.NetworkConfigStoreProto.LinkAddressProto;
import com.android.server.network.configstore.proto.NetworkConfigStoreProto.MeteredOverrideProto;
import com.android.testutils.DevSdkIgnoreRule;
import com.android.testutils.DevSdkIgnoreRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for {@link ProtoConfigUtils}
 */
@RunWith(DevSdkIgnoreRunner.class)
@DevSdkIgnoreRule.IgnoreUpTo(Build.VERSION_CODES.S_V2)
public class ProtoConfigUtilsTest {
    private static final String LINK_ADDRESS_STRING = "192.168.1.10/24";
    private static final String IP_ADDRESS_STRING = "192.168.1.10";
    private static final int PREFIX_LENGTH = 24;

    @Test
    public void testconvertMeteredOverrideToProto() {
        assertEquals(MeteredOverrideProto.METERED_OVERRIDE_FORCE_METERED,
                ProtoConfigUtils.convertMeteredOverrideToProto(
                        EthernetConfiguration.METERED_OVERRIDE_FORCE_METERED));
        assertEquals(MeteredOverrideProto.METERED_OVERRIDE_FORCE_UNMETERED,
                ProtoConfigUtils.convertMeteredOverrideToProto(
                        EthernetConfiguration.METERED_OVERRIDE_FORCE_UNMETERED));
        assertEquals(MeteredOverrideProto.METERED_OVERRIDE_NONE,
                ProtoConfigUtils.convertMeteredOverrideToProto(
                        EthernetConfiguration.METERED_OVERRIDE_NONE));
    }

    @Test
    public void testConvertMeteredOverrideFromProto() {
        assertEquals(EthernetConfiguration.METERED_OVERRIDE_FORCE_METERED,
                ProtoConfigUtils.convertMeteredOverrideFromProto(
                        MeteredOverrideProto.METERED_OVERRIDE_FORCE_METERED));
        assertEquals(EthernetConfiguration.METERED_OVERRIDE_FORCE_UNMETERED,
                ProtoConfigUtils.convertMeteredOverrideFromProto(
                        MeteredOverrideProto.METERED_OVERRIDE_FORCE_UNMETERED));
        assertEquals(EthernetConfiguration.METERED_OVERRIDE_NONE,
                ProtoConfigUtils.convertMeteredOverrideFromProto(
                        MeteredOverrideProto.METERED_OVERRIDE_NONE));
    }

    @Test
    public void testConvertLinkAddressToProto() {
        LinkAddress linkAddr = new LinkAddress(LINK_ADDRESS_STRING);
        LinkAddressProto proto = ProtoConfigUtils.convertLinkAddressToProto(linkAddr);

        assertEquals(IP_ADDRESS_STRING, proto.getAddress());
        assertEquals(PREFIX_LENGTH, proto.getPrefixLength());
    }

    @Test
    public void testConvertLinkAddressFromProto() {
        LinkAddressProto proto = LinkAddressProto.newBuilder()
                .setAddress(IP_ADDRESS_STRING)
                .setPrefixLength(PREFIX_LENGTH)
                .build();

        LinkAddress actual = ProtoConfigUtils.convertLinkAddressFromProto(proto);
        LinkAddress target = new LinkAddress(LINK_ADDRESS_STRING);
        assertEquals(actual, target);
    }
}
