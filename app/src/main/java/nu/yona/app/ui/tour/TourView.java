/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.tour;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.customview.ViewPagerIndicator;
import nu.yona.app.utils.AppUtils;

public class TourView extends LinearLayout {

    private final static int TOTAL_PAGE = 4;

    private final ViewPager viewPager;
    private OnPageChangeListener onPageChangeListener;

    public TourView(Context context, int startPage) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);

        final ViewPagerIndicator indicator = new ViewPagerIndicator(context);
        indicator.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, (int) AppUtils.getDp(context, 60)));
        indicator.setCount(TOTAL_PAGE);
        indicator.onScrolled(startPage, 0);

        viewPager = new ViewPager(context);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        viewPager.setCurrentItem(startPage);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                indicator.onScrolled(position, positionOffset);

                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
        addView(indicator);
        addView(viewPager);
    }

    public int getPage() {
        return viewPager.getCurrentItem();
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    private View createPage(int pageNo) {
        final Context context = getContext();
        View pageView = LayoutInflater.from(context).inflate(R.layout.tour_page, null);

        int titleId = getResources().getIdentifier("screentitle" + pageNo, "string", context.getPackageName());
        int infoId = getResources().getIdentifier("screendesc" + pageNo, "string", context.getPackageName());
        int imgAndroid = getResources().getIdentifier("img_tour_android_" + pageNo, "drawable", context.getPackageName());
        int colorId = getResources().getIdentifier("color" + pageNo, "color", context.getPackageName());

        TextView tour_desc = (TextView) pageView.findViewById(R.id.tour_title);
        TextView title = (TextView) pageView.findViewById(R.id.tour_dec);
        ImageView imageAndorid = (ImageView) pageView.findViewById(R.id.tour_img_android);

        title.setText(titleId);
        title.setTextColor(colorId);
        tour_desc.setText(infoId);
        if (imgAndroid > 0) {
            imageAndorid.setImageResource(imgAndroid);
            imageAndorid.setVisibility(View.VISIBLE);
        } else {
            imageAndorid.setVisibility(View.GONE);
        }
        return pageView;
    }

    private int getIdentifierForPage(int pageNo) {
        switch (pageNo) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            default:
                return 0;
        }
    }

    private final PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return TOTAL_PAGE;
        }

        @Override
        public Object instantiateItem(View collection, int position) {
            View view = createPage(getIdentifierForPage(position + 1));
            ((ViewPager) collection).addView(view);

            return view;
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        @Override
        public void startUpdate(View view) {
        }

        @Override
        public void finishUpdate(View view) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable p, ClassLoader c) {
        }
    };

    public ViewPager getViewPager() {
        return viewPager;
    }


}
