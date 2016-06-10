/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.profile;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;

import com.amulyakhare.textdrawable.TextDrawable;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 10/05/16.
 */
public class BaseProfileFragment extends BaseFragment {
    private final int ALPHA = 50;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Gets image.
     *
     * @param bitmap          the bitmap
     * @param withAlpha       the with alpha
     * @param backgroundColor the background color
     * @param firstName       the first name
     * @param lastName        the last name
     * @return the image
     */
    protected Drawable getImage(Bitmap bitmap, boolean withAlpha, int backgroundColor, String firstName, String lastName) {
        if (bitmap != null) {// TODO: 10/05/16 When server provides user profile image, we need to check and enable if part on base of that.
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            drawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()));
            drawable.setAlpha(ALPHA);
            return drawable;
        } else {
            TextDrawable image = TextDrawable.builder().beginConfig().withBorder(AppConstant.PROFILE_IMAGE_BORDER_SIZE)
                    .textColor(Color.WHITE).endConfig()
                    .buildRound(getName(firstName, lastName),
                            ContextCompat.getColor(YonaActivity.getActivity(), backgroundColor));
            if (withAlpha) {
                image.setAlpha(ALPHA);
            }
            return image;
        }
    }

    private String getName(String firstName, String lastName) {
        StringBuffer displayName = new StringBuffer();
        if (YonaApplication.getEventChangeManager().getDataState().getUser() != null) {
            displayName.append(TextUtils.isEmpty(firstName) ? getString(R.string.blank) : firstName.substring(0, 1).toUpperCase());
            displayName.append(TextUtils.isEmpty(lastName) ? getString(R.string.blank) : lastName.substring(0, 1).toUpperCase());
        }
        return displayName.toString();
    }

}

