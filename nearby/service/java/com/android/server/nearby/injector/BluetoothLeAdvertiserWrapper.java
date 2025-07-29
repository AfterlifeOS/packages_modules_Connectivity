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

package com.android.server.nearby.injector;

import static com.android.server.nearby.NearbyService.TAG;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.PeriodicAdvertisingParameters;
import android.content.Context;
import android.util.Log;

/**
 * Wrapper for BluetoothLeAdvertiser.
 *
 * This class proxies all functions to BluetoothLeAdvertiser.
 * It will be mocked for unit tests.
 *
 * @see BluetoothLeAdvertiser for the details of each public function APIs.
 */
public class BluetoothLeAdvertiserWrapper {
    private final BluetoothAdapter mAdapter;
    private final BluetoothLeAdvertiser mLeAdvertiser;

    /**
     * Returns a wrapper of BluetoothLeAdvertiser.
     *
     * @return an object of BluetoothLeAdvertiserWrapper
     * @see BluetoothLeAdvertiser
     */
    public BluetoothLeAdvertiserWrapper(Context context) {
        BluetoothManager manager = context.getSystemService(BluetoothManager.class);
        if (manager == null) {
            mAdapter = null;
            mLeAdvertiser = null;
            return;
        }
        mAdapter = manager.getAdapter();
        mLeAdvertiser = mAdapter == null ? null : mAdapter.getBluetoothLeAdvertiser();
    }

    /**
     * Starts BLE advertising.
     *
     * @see BluetoothLeAdvertiser
     */
    public void startAdvertising(
            AdvertiseSettings settings,
            AdvertiseData advertiseData,
            final AdvertiseCallback callback) {
        if (mLeAdvertiser == null) {
            Log.e(TAG, "Call startAdvertising from null advertiser.");
        }
        mLeAdvertiser.startAdvertising(settings, advertiseData, callback);
    }

    /**
     * Starts BLE advertisingSet.
     *
     * @see BluetoothLeAdvertiser
     */
    public void startAdvertisingSet(
            AdvertisingSetParameters parameters,
            AdvertiseData advertiseData,
            AdvertiseData scanResponse,
            PeriodicAdvertisingParameters periodicParameters,
            AdvertiseData periodicData,
            AdvertisingSetCallback callback) {
        if (mLeAdvertiser == null) {
            Log.e(TAG, "Call startAdvertisingSet from null advertiser.");
        }
        mLeAdvertiser.startAdvertisingSet(
                parameters,
                advertiseData,
                scanResponse,
                periodicParameters,
                periodicData,
                callback);
    }

    /**
     * Stops BLE advertising.
     *
     * @see BluetoothLeAdvertiser
     */
    public void stopAdvertising(final AdvertiseCallback callback) {
        if (mLeAdvertiser == null) {
            Log.e(TAG, "Call startAdvertising from null advertiser.");
        }
        mLeAdvertiser.stopAdvertising(callback);
    }

    /**
     * Stops BLE advertisingSet.
     *
     * @see BluetoothLeAdvertiser
     */
    public void stopAdvertisingSet(AdvertisingSetCallback callback) {
        if (mLeAdvertiser == null) {
            Log.e(TAG, "Call startAdvertisingSet from null advertiser.");
        }
        mLeAdvertiser.stopAdvertisingSet(callback);
    }

    /**
     * Checks if BLE supports extended advertising.
     *
     * @return true if extended advertisng is supported.
     */
    public Boolean isLeExtendedAdvertisingSupported() {
        if (mAdapter == null) {
            Log.e(TAG, "Checks extended BLE support from null BLE adapter.");
        }
        return mAdapter.isLeExtendedAdvertisingSupported();
    }
}
