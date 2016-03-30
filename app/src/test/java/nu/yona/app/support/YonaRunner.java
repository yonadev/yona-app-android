/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.support;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;
import org.robolectric.res.FsFile;

/**
 * Created by kinnarvasa on 30/03/16.
 */
public class YonaRunner extends RobolectricGradleTestRunner {
    public static final int MAX_SDK_SUPPORTED_BY_ROBOLECTRIC = 23;

    public YonaRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        AndroidManifest appManifest = super.getAppManifest(config);
        FsFile androidManifestFile = appManifest.getAndroidManifestFile();

        AndroidManifest testManifest;

        if (androidManifestFile.exists()) {
            testManifest = appManifest;
        } else {
            String moduleRoot = getModuleRootPath(config);
            androidManifestFile = FileFsFile.from(moduleRoot, appManifest.getAndroidManifestFile().getPath().replace("bundles", "manifests/full"));
            FsFile resDirectory = FileFsFile.from(moduleRoot, appManifest.getResDirectory().getPath());
            FsFile assetsDirectory = FileFsFile.from(moduleRoot, appManifest.getAssetsDirectory().getPath());
            testManifest = new AndroidManifest(androidManifestFile, resDirectory, assetsDirectory);
        }

        return new AndroidManifest(testManifest.getAndroidManifestFile(),
                testManifest.getResDirectory(),
                testManifest.getAssetsDirectory()) {
            @Override
            public int getTargetSdkVersion() {
                int targetSdk = super.getTargetSdkVersion();
                return targetSdk< MAX_SDK_SUPPORTED_BY_ROBOLECTRIC ?
                        targetSdk : MAX_SDK_SUPPORTED_BY_ROBOLECTRIC;

            }
        };
    }

    private String getModuleRootPath(Config config) {
        String moduleRoot = config.constants().getResource("").toString().replace("file:", "");
        return moduleRoot.substring(0, moduleRoot.indexOf("/build"));
    }
}
