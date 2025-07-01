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

package com.android.server.ethernet;

import android.net.IEthernetServiceListener;
import android.net.IpConfiguration;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

/** Wraps IEthernetServiceListener and implements IInterface for use in RemoteCallbackList */
public class EthernetListener implements IInterface {
    private final IEthernetServiceListener mListener;
    private final boolean mHasUseRestrictedNetworksPermission;
    private final int mFlags;

    public EthernetListener(IEthernetServiceListener listener, boolean canUseRestrictedNetworks,
            int flags) {
        mListener = listener;
        mHasUseRestrictedNetworksPermission = canUseRestrictedNetworks;
        mFlags = flags;
    }

    /** Indicates whether the remote uid has USE_RESTRICTED_NETWORKS permission */
    public boolean hasUseRestrictedNetworksPermission() {
        return mHasUseRestrictedNetworksPermission;
    }

    /** Send onEthernetStateChanged callback */
    public void onEthernetStateChanged(int state) {
        try {
            mListener.onEthernetStateChanged(state);
        } catch (RemoteException e) {
            // Most likely because the other end is dead. Do nothing.
        }
    }

    /** Send onInterfaceStateChanged callback */
    public void onInterfaceStateChanged(String iface, int state, int role,
            IpConfiguration configuration) {
        try {
            mListener.onInterfaceStateChanged(iface, state, role, configuration);
        } catch (RemoteException e) {
            // Most likely because the other end is dead. Do nothing.
        }
    }

    @Override
    public IBinder asBinder() {
        return mListener.asBinder();
    }
}
