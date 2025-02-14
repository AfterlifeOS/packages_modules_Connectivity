/*
 * Copyright (C) 2024 The Android Open Source Project
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

package android.net.vcn.cts;

import static org.junit.Assert.assertThrows;

import android.net.ConnectivityFrameworkInitializerBaklava;
import android.net.vcn.Flags;
import android.os.Build;
import android.platform.test.annotations.RequiresFlagsEnabled;
import android.platform.test.flag.junit.CheckFlagsRule;
import android.platform.test.flag.junit.DeviceFlagsValueProvider;

import com.android.testutils.DevSdkIgnoreRule;
import com.android.testutils.DevSdkIgnoreRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// TODO: b/374174952 After B finalization, use Sdk36ModuleController to ensure VCN tests only run on
// Android B/B+
@RunWith(DevSdkIgnoreRunner.class)
@DevSdkIgnoreRule.IgnoreUpTo(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@RequiresFlagsEnabled(Flags.FLAG_MAINLINE_VCN_MODULE_API)
public class ConnectivityFrameworkInitializerBaklavaTest {
    @Rule
    public final CheckFlagsRule mCheckFlagsRule = DeviceFlagsValueProvider.createCheckFlagsRule();

    /**
     * ConnectivityFrameworkInitializerBaklava.registerServiceWrappers() should only be called by
     * SystemServiceRegistry during boot up. Calling this API at any other time should throw an
     * exception.
     */
    @Test
    public void testRegisterServiceWrappers() {
        assertThrows(
                IllegalStateException.class,
                ConnectivityFrameworkInitializerBaklava::registerServiceWrappers);
    }
}
