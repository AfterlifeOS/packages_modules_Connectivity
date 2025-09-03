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

package com.android.server.vcn.metrics;

import static com.android.server.vcn.metrics.VcnStatsLog.VCN_GATEWAY_CONNECTION_STATE_CHANGED__GW_TEARDOWN_REASON__GATEWAY_TEARDOWN_REASON_NONE;

/** Utility class for logging VCN metrics. */
public class VcnMetrics {

    private static final int GATEWAY_TEARDOWN_REASON_NONE =
            VCN_GATEWAY_CONNECTION_STATE_CHANGED__GW_TEARDOWN_REASON__GATEWAY_TEARDOWN_REASON_NONE;

    /** Log an atom when a VcnGatewayConnection has entered safe mode. */
    public void logEnterSafeMode(int gatewayConnectionId) {
        VcnStatsLog.write(
                VcnStatsLog.VCN_GATEWAY_CONNECTION_STATE_CHANGED,
                gatewayConnectionId,
                GATEWAY_TEARDOWN_REASON_NONE,
                true /* isInSafeMode */);
    }

    /** Log an atom when a VcnGatewayConnection has exited safe mode. */
    public void logExitSafeMode(int gatewayConnectionId) {
        VcnStatsLog.write(
                VcnStatsLog.VCN_GATEWAY_CONNECTION_STATE_CHANGED,
                gatewayConnectionId,
                GATEWAY_TEARDOWN_REASON_NONE,
                false /* isInSafeMode */);
    }

    /** Log an atom when VCN network has been validated. */
    public void logVcnNetworkValidated(int gatewayConnectionId, int networkId) {
        VcnStatsLog.write(
                VcnStatsLog.VCN_NETWORK_STATE_CHANGED,
                gatewayConnectionId,
                networkId,
                true /* isConnected */,
                true /* isValidated */);
    }

    /** Log an atom when VCN network has been not validated. */
    public void logVcnNetworkNotValidated(int gatewayConnectionId, int networkId) {
        VcnStatsLog.write(
                VcnStatsLog.VCN_NETWORK_STATE_CHANGED,
                gatewayConnectionId,
                networkId,
                true /* isConnected */,
                false /* isValidated */);
    }
}
