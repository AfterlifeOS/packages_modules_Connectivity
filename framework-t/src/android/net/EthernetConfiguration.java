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

package android.net;

import android.annotation.IntDef;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * This class represents the static IP configuration data such as IP configuration (static IPv4
 * address, IPv4 gateway, DNS servers, etc.) and network capabilities for a configured ethernet
 * network interface.
 * The configuration can be set to be either persisted across boot or not. When set to be
 * persistent, the Builder will check if all set fields can be persisted, otherwise throws an
 * exception. Currently persisting {@link NetworkCapabilities} is not supported.
 *
 * - Intended Usage and Scope:
 *   This class implements android.os.Parcelable and resides in the framework JAR. This is
 *   necessary for being used as an AIDL object to share code with EthernetPortInfo and
 *   EthernetNetworkUpdateRequest.
 *
 * - Future Evolution:
 *   This class is meant to progressively replace EthernetNetworkUpdateRequest in EthernetManager's
 *   {@code @SystemApi} and be exposed through {@code @SystemApi} getters as well.
 *
 * @hide
 */
public class EthernetConfiguration implements Parcelable {
    /**
     * Static IP configuration data (static IPv4 address, IPv4 gateway, DNS servers, etc.)
     */
    @Nullable private final IpConfiguration mIpConfiguration;

    /**
     * This is only for the requestable bits of NetworkCapabilities that an external caller can
     * pass through a EthernetNetworkUpdateRequest.
     */
    @Nullable private final NetworkCapabilities mNetworkCapabilities;

    /**
     * This configuration is not persisted across boot.
     * @hide
     */
    public static final int PERSISTENCE_NOT_PERSISTED = 0;

    /**
     * This configuration is persisted across boot.
     * @hide
     */
    public static final int PERSISTENCE_IS_PERSISTED = 1;

    /**
     * Indicates whether this ethernet configuration should be persisted across boot for the
     * associated user.
     * More persistence types will be introduced in the future for multi-user management.
     * @hide
     */
    @IntDef(prefix = "PERSISTENCE", value = {PERSISTENCE_IS_PERSISTED, PERSISTENCE_NOT_PERSISTED})
    public @interface Persistence {}

    /**
     * Indicates whether this ethernet configuration should be persisted across boot for the
     * associated user.
     * When {@code mPersistence} is set to {@code PERSISTENCE_IS_PERSISTED} and a configuration
     * that cannot be persisted is passed in, an exception is thrown. Currently
     * {@link NetworkCapabilities} is not allowed to be persisted.
     */
    private final @Persistence int mPersistence;

    private EthernetConfiguration(@Nullable IpConfiguration ipConfiguration,
            @Nullable NetworkCapabilities capabilities, @Persistence int persistence) {
        mIpConfiguration = ipConfiguration;
        mNetworkCapabilities = capabilities;
        mPersistence = persistence;
    }

    /**
     * Get the {@link IpConfiguration} object associated with this EthernetConfiguration, or null.
     */
    @Nullable
    public IpConfiguration getIpConfiguration() {
        return mIpConfiguration == null ? null : new IpConfiguration(mIpConfiguration);
    }

    /**
     * Get the {@link NetworkCapabilities} object associated with this EthernetConfiguration, or
     * null.
     */
    @Nullable
    public NetworkCapabilities getNetworkCapabilities() {
        return mNetworkCapabilities == null ? null : new NetworkCapabilities(mNetworkCapabilities);
    }

    /**
     * Get whether this ethernet configuration is persisted across boot.
     */
    public @Persistence int getPersistence() {
        return mPersistence;
    }

    @Override
    public String toString() {
        return "IP configurations: " + mIpConfiguration
                + "Network capabilities: " + mNetworkCapabilities
                + ", Is persisted: " + mPersistence;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof EthernetConfiguration)) {
            return false;
        }

        final EthernetConfiguration other = (EthernetConfiguration) o;
        return Objects.equals(this.mIpConfiguration, other.mIpConfiguration)
                && Objects.equals(this.mNetworkCapabilities, other.mNetworkCapabilities)
                && mPersistence == other.mPersistence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mIpConfiguration, mNetworkCapabilities, mPersistence);
    }

    /** Implement the Parcelable interface */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mIpConfiguration, flags);
        dest.writeParcelable(mNetworkCapabilities, flags);
        dest.writeInt(mPersistence);
    }

    private EthernetConfiguration(Parcel in) {
        mIpConfiguration = in.readParcelable(IpConfiguration.class.getClassLoader());
        mNetworkCapabilities = in.readParcelable(NetworkCapabilities.class.getClassLoader());
        mPersistence = in.readInt();
    }

    /** Implement the Parcelable interface */
    public static final @NonNull Creator<EthernetConfiguration> CREATOR =
            new Creator<EthernetConfiguration>() {
                @Override
                public EthernetConfiguration createFromParcel(Parcel in) {
                    return new EthernetConfiguration(in);
                }

                @Override
                public EthernetConfiguration[] newArray(int size) {
                    return new EthernetConfiguration[size];
                }
            };

    /** Implement the Parcelable interface */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Builder for {@link EthernetConfiguration}.
     */
    public static final class Builder {
        @Nullable private IpConfiguration mIpConfiguration;
        @Nullable private NetworkCapabilities mNetworkCapabilities;
        private @Persistence int mPersistence = PERSISTENCE_NOT_PERSISTED;

        /**
         * Set persisted configuration for current ethernet configuration. One of
         * {@code PERSISTENCE_IS_PERSISTED}, {@code PERSISTENCE_NOT_PERSISTED}.
         * When not set, this field is {@code PERSISTENCE_NOT_PERSISTED} by default.
         */
        @NonNull
        public Builder setPersistence(@Persistence int persistence) {
            switch (persistence) {
                case PERSISTENCE_IS_PERSISTED:
                case PERSISTENCE_NOT_PERSISTED:
                    mPersistence = persistence;
                    return this;
            }
            throw new IllegalArgumentException(
                    "Invalid persistence value: " + persistence);
        }

        /** Set IP configuration for current ethernet configuration */
        @NonNull
        public Builder setIpConfiguration(@Nullable IpConfiguration ipConfiguration) {
            mIpConfiguration = ipConfiguration;
            return this;
        }

        /** Set network capabilities for current ethernet configuration */
        @NonNull
        public Builder setNetworkCapabilities(@Nullable NetworkCapabilities capabilities) {
            mNetworkCapabilities = capabilities;
            return this;
        }

        /** Build an EthernetConfiguration object */
        @NonNull
        public EthernetConfiguration build() {
            if (mIpConfiguration == null && mNetworkCapabilities == null) {
                throw new IllegalArgumentException("IP configuration and network capabilities are"
                        + "both null, cannot construct an empty EthernetConfiguration.");
            }
            // Persisting NetworkCapabilities is not currently supported.
            if (mPersistence == PERSISTENCE_IS_PERSISTED && mNetworkCapabilities != null) {
                throw new IllegalArgumentException("Network capabilities cannot be persisted.");
            }
            return new EthernetConfiguration(mIpConfiguration, mNetworkCapabilities, mPersistence);
        }
    }
}
