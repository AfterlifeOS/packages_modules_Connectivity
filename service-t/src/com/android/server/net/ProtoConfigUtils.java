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

import static java.util.Objects.requireNonNull;

import android.net.EthernetConfiguration;
import android.net.EthernetConfiguration.MeteredOverride;
import android.util.Log;

import com.android.server.network.configstore.proto.NetworkConfigStoreProto.MeteredOverrideProto;

/**
 * Static util methods for {@link ProtoConfig}.
 */
public class ProtoConfigUtils {
    private static final String TAG = "ProtoConfigUtils";

    /**
     * Converts an {@link EthernetConfiguration.MeteredOverride} value to a corresponding value of
     * {@link MeteredOverrideProto} type.
     */
    public static MeteredOverrideProto convertMeteredOverrideToProto(
            @MeteredOverride int override) {
        return switch (override) {
            case EthernetConfiguration.METERED_OVERRIDE_FORCE_METERED ->
                    MeteredOverrideProto.METERED_OVERRIDE_FORCE_METERED;
            case EthernetConfiguration.METERED_OVERRIDE_FORCE_UNMETERED ->
                    MeteredOverrideProto.METERED_OVERRIDE_FORCE_UNMETERED;
            case EthernetConfiguration.METERED_OVERRIDE_NONE ->
                    MeteredOverrideProto.METERED_OVERRIDE_NONE;
            default -> {
                Log.e(TAG, "Ignore invalid metered override: " + override);
                yield MeteredOverrideProto.METERED_OVERRIDE_NONE;
            }
        };
    }

    /**
     * Converts a {@link MeteredOverrideProto} value to a corresponding value of
     * {@link EthernetConfiguration.MeteredOverride} type.
     */
    public static @MeteredOverride int convertMeteredOverrideFromProto(
            MeteredOverrideProto protoOverride) {
        requireNonNull(protoOverride, "MeteredOverrideProto must not be null");
        return switch (protoOverride) {
            case METERED_OVERRIDE_FORCE_METERED ->
                    EthernetConfiguration.METERED_OVERRIDE_FORCE_METERED;
            case METERED_OVERRIDE_FORCE_UNMETERED ->
                    EthernetConfiguration.METERED_OVERRIDE_FORCE_UNMETERED;
            case METERED_OVERRIDE_NONE ->
                    EthernetConfiguration.METERED_OVERRIDE_NONE;
            default -> {
                Log.e(TAG, "Ignore invalid metered override: " + protoOverride);
                yield EthernetConfiguration.METERED_OVERRIDE_NONE;
            }
        };
    }
}
