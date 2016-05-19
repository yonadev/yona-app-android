/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.tour;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;

/**
 * Created by bhargavsuthar on 18/05/16.
 */
public class YonaCarrouselAdapter extends PagerAdapter {

    private Context mContext;
    private int[] mResources;

    public YonaCarrouselAdapter(Context mContext, int[] mResources) {
        this.mContext = mContext;
        this.mResources = mResources;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.carrousel_pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
        imageView.setImageResource(mResources[position]);

        ImageView img_center = (ImageView) itemView.findViewById(R.id.img_center);

        YonaFontTextView carrouselTitle = (YonaFontTextView) itemView.findViewById(R.id.carrousel_title);
        YonaFontTextView carrouselDesc = (YonaFontTextView) itemView.findViewById(R.id.carrousel_desc);


        switch (position) {
            case 0:
                carrouselTitle.setText(R.string.screentitle1);
                carrouselDesc.setText(R.string.screendesc1);
                carrouselTitle.setTextColor(ContextCompat.getColor(mContext, R.color.darkish_pink));
                img_center.setImageResource(R.drawable.img_transparantie);
                break;
            case 1:
                carrouselTitle.setText(R.string.screentitle2);
                carrouselDesc.setText(R.string.screendesc2);
                carrouselTitle.setTextColor(ContextCompat.getColor(mContext, R.color.pea));
                img_center.setImageResource(R.drawable.img_share);
                break;
            case 2:
                carrouselTitle.setText(R.string.screentitle3);
                carrouselDesc.setText(R.string.screendesc3);
                carrouselTitle.setTextColor(ContextCompat.getColor(mContext, R.color.mid_blue));
                img_center.setImageResource(R.drawable.img_borders);
                break;
            case 3:
                carrouselTitle.setText(R.string.screentitle4);
                carrouselDesc.setText(R.string.screendesc4);
                carrouselTitle.setTextColor(ContextCompat.getColor(mContext, R.color.mango));
                img_center.setImageResource(R.drawable.img_time);
                break;
        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}