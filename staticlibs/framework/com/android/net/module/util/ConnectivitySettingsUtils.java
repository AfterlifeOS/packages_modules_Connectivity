/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android.net.module.util;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.util.Map;

/**
 * Collection of connectivity settings utilities.
 *
 * @hide
 */
public class ConnectivitySettingsUtils {
    public static final String TAG = ConnectivitySettingsUtils.class.getSimpleName();
    public static final int PRIVATE_DNS_MODE_OFF = 1;
    public static final int PRIVATE_DNS_MODE_OPPORTUNISTIC = 2;
    public static final int PRIVATE_DNS_MODE_PROVIDER_HOSTNAME = 3;

    public static final String PRIVATE_DNS_DEFAULT_MODE = "private_dns_default_mode";
    public static final String PRIVATE_DNS_MODE = "private_dns_mode";
    public static final String PRIVATE_DNS_MODE_OFF_STRING = "off";
    public static final String PRIVATE_DNS_MODE_OPPORTUNISTIC_STRING = "opportunistic";
    public static final String PRIVATE_DNS_MODE_PROVIDER_HOSTNAME_STRING = "hostname";
    public static final String PRIVATE_DNS_SPECIFIER = "private_dns_specifier";

    public static final String NETWORK_AVOID_BAD_WIFI = "network_avoid_bad_wifi";
    public static final String NETWORK_CARRIER_AWARE_AVOID_BAD_WIFI =
            "network_carrier_aware_avoid_bad_wifi";

    /**
     * Get private DNS mode as string.
     *
     * @param mode One of the private DNS values.
     * @return A string of private DNS mode.
     */
    public static String getPrivateDnsModeAsString(int mode) {
        switch (mode) {
            case PRIVATE_DNS_MODE_OFF:
                return PRIVATE_DNS_MODE_OFF_STRING;
            case PRIVATE_DNS_MODE_OPPORTUNISTIC:
                return PRIVATE_DNS_MODE_OPPORTUNISTIC_STRING;
            case PRIVATE_DNS_MODE_PROVIDER_HOSTNAME:
                return PRIVATE_DNS_MODE_PROVIDER_HOSTNAME_STRING;
            default:
                throw new IllegalArgumentException("Invalid private dns mode: " + mode);
        }
    }

    private static int getPrivateDnsModeAsInt(String mode) {
        // If both PRIVATE_DNS_MODE and PRIVATE_DNS_DEFAULT_MODE are not set, choose
        // PRIVATE_DNS_MODE_OPPORTUNISTIC as default mode.
        if (TextUtils.isEmpty(mode))
            return PRIVATE_DNS_MODE_OPPORTUNISTIC;
        switch (mode) {
            case "off":
                return PRIVATE_DNS_MODE_OFF;
            case "hostname":
                return PRIVATE_DNS_MODE_PROVIDER_HOSTNAME;
            case "opportunistic":
                return PRIVATE_DNS_MODE_OPPORTUNISTIC;
            default:
                // b/260211513: adb shell settings put global private_dns_mode foo
                // can result in arbitrary strings - treat any unknown value as empty string.
                // throw new IllegalArgumentException("Invalid private dns mode: " + mode);
                return PRIVATE_DNS_MODE_OPPORTUNISTIC;
        }
    }

    /**
     * Get private DNS mode from settings.
     *
     * @param context The Context to query the private DNS mode from settings.
     * @return An integer of private DNS mode.
     */
    public static int getPrivateDnsMode(@NonNull Context context) {
        final ContentResolver cr = context.getContentResolver();
        String mode = Settings.Global.getString(cr, PRIVATE_DNS_MODE);
        if (TextUtils.isEmpty(mode)) mode = Settings.Global.getString(cr, PRIVATE_DNS_DEFAULT_MODE);
        return getPrivateDnsModeAsInt(mode);
    }

    /**
     * Set private DNS mode to settings.
     *
     * @param context The {@link Context} to set the private DNS mode.
     * @param mode The private dns mode. This should be one of the PRIVATE_DNS_MODE_* constants.
     */
    public static void setPrivateDnsMode(@NonNull Context context, int mode) {
        if (!(mode == PRIVATE_DNS_MODE_OFF
                || mode == PRIVATE_DNS_MODE_OPPORTUNISTIC
                || mode == PRIVATE_DNS_MODE_PROVIDER_HOSTNAME)) {
            throw new IllegalArgumentException("Invalid private dns mode: " + mode);
        }
        Settings.Global.putString(context.getContentResolver(), PRIVATE_DNS_MODE,
                getPrivateDnsModeAsString(mode));
    }

    /**
     * Get specific private dns provider name from {@link Settings}.
     *
     * @param context The {@link Context} to query the setting.
     * @return The specific private dns provider name, or null if no setting value.
     */
    @Nullable
    public static String getPrivateDnsHostname(@NonNull Context context) {
        return Settings.Global.getString(context.getContentResolver(), PRIVATE_DNS_SPECIFIER);
    }

    /**
     * Set specific private dns provider name to {@link Settings}.
     *
     * @param context The {@link Context} to set the setting.
     * @param specifier The specific private dns provider name.
     */
    public static void setPrivateDnsHostname(@NonNull Context context, @Nullable String specifier) {
        Settings.Global.putString(context.getContentResolver(), PRIVATE_DNS_SPECIFIER, specifier);
    }

    /**
     * Set legacy global avoid bad wifi to {@link Settings}.
     *
     * @param context The {@link Context} to set the setting.
     * @param setting The desired setting value.
     * "0": Don't avoid bad Wi-Fi.
     * "1": Avoid bad Wi-Fi.
     * {@code null}: Ask the user whether to switch away from bad Wi-Fi.
     * @deprecated Use {@link #setNetworkAvoidBadWifiSetting(Context, String)} instead.
     */
    @Deprecated
    public static void setNetworkLegacyGlobalAvoidBadWifiSetting(
            @NonNull Context context, @Nullable String setting) {
        Settings.Global.putString(context.getContentResolver(), NETWORK_AVOID_BAD_WIFI, setting);
    }

    /**
     * Get legacy global avoid bad wifi to {@link Settings}.
     *
     * @param context The {@link Context} to query the setting.
     * @return The current setting value, which can be "0", "1", or {@code null}.
     * Returns {@code null} if the setting is not found.
     * @deprecated Use {@link #getNetworkAvoidBadWifiSetting(Context)} instead.
     */
    @Deprecated
    @Nullable
    public static String getNetworkLegacyGlobalAvoidBadWifiSetting(@NonNull Context context) {
        return Settings.Global.getString(context.getContentResolver(), NETWORK_AVOID_BAD_WIFI);
    }

    /**
     * Set the carrier-aware avoid bad wifi to {@link Settings}.
     *
     * @param context The {@link Context} to set the setting.
     * @param setting The desired setting string, formatted as "subId1,value1;subId2,value2".
     * subId: The carrier subscription ID (integer).
     * value: "0" to not avoid bad Wi-Fi for this subscription, or "1" to avoid.
     * {@code null}: Ask the user whether to switch away from bad Wi-Fi.
     */
    public static void setNetworkAvoidBadWifiSetting(
                @NonNull Context context, @Nullable String setting) {
        Settings.Global.putString(
                context.getContentResolver(), NETWORK_CARRIER_AWARE_AVOID_BAD_WIFI, setting);
    }

    /**
     * Get the raw carrier-aware avoid bad wifi string from {@link Settings}.
     * @see #convertCarrierAwareSettingsStringToMap(String)
     *
     * @param context The {@link Context} to set the setting.
     * @return The current setting string, formatted as "subId1,value1;subId2,value2",
     * or {@code null} if the setting is not set.
     */
    @Nullable
    public static String getNetworkAvoidBadWifiSetting(@NonNull Context context) {
        return Settings.Global.getString(
            context.getContentResolver(), NETWORK_CARRIER_AWARE_AVOID_BAD_WIFI);
    }

    /**
     * Parses the carrier-aware Wi-Fi avoid bad wifi setting string into a map.
     *
     * @param setting The setting string, typically retrieved from
     * {@link #getNetworkAvoidBadWifiSetting(Context)}.
     * Expected format is "subId1,value1;subId2,value2".
     * @return A {@link Map} where keys are carrier subscription IDs ({@link Integer})
     * and values are booleans indicating whether to avoid bad Wi-Fi ({@code true} for avoid,
     * {@code false} for not avoid).
     * Returns an empty map if the input setting is null or invalid.
     */
    @NonNull
    public static Map<Integer, Boolean> convertCarrierAwareSettingsStringToMap(
            @Nullable String setting) {
        final ArrayMap<Integer, Boolean> settingMap = new ArrayMap<>();
        if (setting == null) {
            return settingMap;
        }

        for (String entry: setting.split(";")) {
            final String[] parts = entry.split(",");
            if (parts.length != 2) {
                Log.e(TAG, "invalid setting string: " + entry);
                continue;
            }

            try {
                int subId = Integer.parseInt(parts[0].trim());
                boolean value = Integer.parseInt(parts[1].trim()) != 0;
                settingMap.put(subId, value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "invalid setting string " + entry);
            }
        }

        return settingMap;
    }

    /**
     * Converts a map of carrier subscription IDs to Wi-Fi avoidance preferences into a string.
     *
     * @param context The application context.
     * @param settingMap A {@link Map} where keys are carrier subscription IDs ({@link Integer})
     * and values are booleans indicating whether to avoid bad Wi-Fi ({@code true} for avoid,
     * {@code false} for not avoid).
     * This map will be converted into a "subId,value;subId2,value2" string format.
     * @return A string representing the carrier-aware Wi-Fi avoidance settings,
     * or {@code null} if the provided {@code settingMap} is empty.
     * The format is "subId1,value1;subId2,value2".
     */
    @Nullable
    public static String convertCarrierAwareSettingsMapToString(
            @NonNull Context context, @NonNull Map<Integer, Boolean> settingMap) {
        if (settingMap.isEmpty()) return null;

        final StringBuilder sb = new StringBuilder();
        int index = 0;

        for (Map.Entry<Integer, Boolean> entry: settingMap.entrySet()) {
            final String valueStr = entry.getValue() ? "1" : "0";
            sb.append(entry.getKey())
                    .append(",")
                    .append(valueStr);
            if (index < settingMap.size() - 1) {
                sb.append(";");
            }
            index++;
        }

        return sb.toString();
    }
}
