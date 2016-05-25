/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.LaunchActivity;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by bhargavsuthar on 18/05/16.
 */
public class YonaCarrouselActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, ViewPager.PageTransformer {

    private final int TIMER_DELAY = 3000;
    private final int TIMER_PERIOD = 4000;
    private final float TRANFORMATION_POSITION = 0.999f;
    /**
     * The View.
     */
    protected View view;
    private ImageButton btnNext;
    private CarrouselViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private YonaCarrouselAdapter mAdapter;
    private int currentPage = 0;
    private Timer timer = new Timer();
    private int[] mImageResources = {
            R.drawable.slider_transparantie_clean,
            R.drawable.slider_delen_clean,
            R.drawable.slider_grenzen_clean,
            R.drawable.slider_tijdsbesteding_clean
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.carrousel_pager_layout);
        initializeView();
    }

    /**
     * set Layout reference
     */
    public void initializeView() {

        intro_images = (CarrouselViewPager) findViewById(R.id.pager_introduction);
        btnNext = (ImageButton) findViewById(R.id.btn_next);

        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        btnNext.setOnClickListener(this);

        mAdapter = new YonaCarrouselAdapter(this, mImageResources);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.addOnPageChangeListener(this);
        intro_images.setPageTransformer(true, this);
        setUiPageViewController();
        timer.schedule(new CarrouselTimer(), TIMER_DELAY, TIMER_PERIOD);
    }

    /**
     * Set UI pager View Controller
     */
    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.carrousel_nonselected_item));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(getResources().getInteger(R.integer.margin_pager_indicator), getResources().getInteger(R.integer.margin_pager_zero), getResources().getInteger(R.integer.margin_pager_indicator), getResources().getInteger(R.integer.margin_pager_zero));

            pager_indicator.addView(dots[i], params);
        }
        displayFirstItem(0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                moveToLaunchActivity();
                break;

            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * Display First position item
     *
     * @param position
     */
    private void displayFirstItem(int position) {
        dots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.carrousel_selected_pink_item_dot));
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.carrousel_nonselected_item));
        }
        currentPage = position;
        switch (position) {
            case 0:
                displayFirstItem(position);
                break;
            case 1:
                dots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.carrousel_selected_green_item_dot));
                break;
            case 2:
                dots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.carrousel_selected_blue_item_dot));
                break;
            case 3:
                dots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.carrousel_selected_orange_item_dot));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * Redirect user to Launcher page
     */
    private void moveToLaunchActivity() {
        YonaApplication.getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_TOUR, true).commit();
        startActivity(new Intent(this, LaunchActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    @Override
    public void transformPage(View view, float position) {

        final int pageWidth = view.getWidth();

        /*
         * When a page's alpha is set to 0 it's visibility should also be set to gone.
         * Even though the view isn't visible it can still be interacted with if it isn't gone and is drawn on top.
         */

        /*
         * Position is checked right up next to -1 and 1. The reason is because sometimes the position doesn't seem to come
         * all the way through as a whole number. Meaning it seems it would stop so very close to -1 or 0 (for example) and
         * the code to make necessary views 'gone' never gets called. So then there could be an invisible view on top that is
         * still able to be interacted with.
         */

        if (position < -TRANFORMATION_POSITION) { // [-Infinity,-1)
            // This page is way off-screen to the left so hide it.
            view.setAlpha(0);
            view.setVisibility(View.GONE);
            view.setTranslationX(pageWidth);
        } else if (position <= TRANFORMATION_POSITION) { // (-1, 1)
            // The further the page is from being center page the more transparent it is.
            view.setAlpha(getAlpha(position));
            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);
            // Make sure the page is visible
            view.setVisibility(View.VISIBLE);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right so hide it.
            view.setAlpha(0);
            view.setVisibility(View.GONE);
            view.setTranslationX(-pageWidth);
        }
    }

    /***
     * Get the alpha value that should be applied to a position.
     *
     * @param position Position to find an alpha for.
     * @return An alpha value.
     */
    private static final float getAlpha(final float position) {
        return getSlowQuadraticAlpha(position);
    }


    private static final float getSlowQuadraticAlpha(final float position) {
        return 1 - position * position;
    }

    /**
     * Changing next item of Carrousel till not reached last page
     */
    private void displayNextCarrousel() {
        if (currentPage < mAdapter.getCount()) {
            intro_images.setCurrentItem(currentPage + 1);
        }
    }

    /**
     * Timer to display next item of Carrousel
     */
    private class CarrouselTimer extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayNextCarrousel();
                }
            });
        }
    }
}
