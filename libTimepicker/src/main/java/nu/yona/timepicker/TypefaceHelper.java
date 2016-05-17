/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.timepicker;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.SimpleArrayMap;

/*
    Each call to Typeface.createFromAsset will load a new instance of the typeface into memory,
    and this memory is not consistently get garbage collected
    http://code.google.com/p/android/issues/detail?id=9904
    (It states released but even on Lollipop you can see the typefaces accumulate even after
    multiple GC passes)
    You can detect this by running:
    adb shell dumpsys meminfo com.your.packagenage
    You will see output like:
     Asset Allocations
        zip:/data/app/com.your.packagenage-1.apk:/assets/lib-roboto-medium.ttf: 125K
        zip:/data/app/com.your.packagenage-1.apk:/assets/lib-roboto-medium.ttf: 125K
        zip:/data/app/com.your.packagenage-1.apk:/assets/lib-roboto-medium.ttf: 125K
        zip:/data/app/com.your.packagenage-1.apk:/assets/Roboto-Regular.ttf: 123K
        zip:/data/app/com.your.packagenage-1.apk:/assets/lib-roboto-medium.ttf: 125K
*/
public class TypefaceHelper {

    private static final SimpleArrayMap<String, Typeface> cache = new SimpleArrayMap<>();

    public static Typeface get(Context c, String name) {
        synchronized (cache) {
            if (!cache.containsKey(name)) {
                Typeface t = Typeface.createFromAsset(
                        c.getAssets(), String.format("fonts/%s.ttf", name));
                cache.put(name, t);
                return t;
            }
            return cache.get(name);
        }
    }
}
