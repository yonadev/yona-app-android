/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.ActivityCategoryManager;
import nu.yona.app.api.manager.GoalManager;
import nu.yona.app.api.manager.impl.ActivityCategoryManagerImpl;
import nu.yona.app.api.manager.impl.GoalManagerImpl;
import nu.yona.app.api.receiver.YonaReceiver;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.ui.challenges.ChallengesFragment;
import nu.yona.app.ui.dashboard.DashboardFragment;
import nu.yona.app.ui.frinends.FriendsFragment;
import nu.yona.app.ui.message.MessageFragment;
import nu.yona.app.ui.pincode.PinActivity;
import nu.yona.app.ui.profile.ProfileFragment;
import nu.yona.app.ui.settings.SettingsFragment;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 18/03/16.
 */
public class YonaActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    private static final int TOTAL_TABS = 4;
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;
    boolean isBackPressed = false;
    private Toolbar mToolBar;
    private ActionBar mActionBar;
    private TabLayout mTabLayout;
    private Fragment mContent;
    private DashboardFragment dashboardFragment = new DashboardFragment();
    private FriendsFragment friendsFragment = new FriendsFragment();
    private ChallengesFragment challengesFragment = new ChallengesFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private YonaFontTextView toolbarTitle;
    private ImageView rightIcon;
    private boolean isToDisplayLogin = false;

    private GoalManager goalManager;
    private ActivityCategoryManager activityCategoryManager;

    /**
     * This will register receiver for different events like screen on-off, boot, connectivity etc.
     */
    public static void registerReceiver() {
        YonaApplication.getAppContext().registerReceiver(new YonaReceiver(), new IntentFilter());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yona_layout);

        goalManager = new GoalManagerImpl(this);
        activityCategoryManager = new ActivityCategoryManagerImpl(this);

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(AppConstant.FROM_LOGIN)) {
            isToDisplayLogin = false;
        }
        mToolBar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbarTitle = (YonaFontTextView) mToolBar.findViewById(R.id.toolbar_title);
        rightIcon = (ImageView) mToolBar.findViewById(R.id.rightIcon);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        setupTabs();
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        clearAllFragment();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getCustomView().getTag().hashCode()) {
                    case R.string.dashboard:
                        replaceFragmentWithAction(new Intent(IntentEnum.ACTION_DASHBOARD.getActionString()));
                        break;
                    case R.string.frineds:
                        replaceFragmentWithAction(new Intent(IntentEnum.ACTION_FRIENDS.getActionString()));
                        break;
                    case R.string.challenges:
                        replaceFragmentWithAction(new Intent(IntentEnum.ACTION_CHALLENGES.getActionString()));
                        break;
                    case R.string.settings:
                        replaceFragmentWithAction(new Intent(IntentEnum.ACTION_SETTINGS.getActionString()));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Load default dashboard_selector fragment on start after login, if signup, start challenges.
        if (!YonaApplication.getUserPreferences().getBoolean(PreferenceConstant.STEP_CHALLENGES, false)) {
            mTabLayout.getTabAt(2).select();
        } else {
            mTabLayout.getTabAt(0).select();
        }

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContent instanceof DashboardFragment) {
                    replaceFragmentWithAction(new Intent(IntentEnum.ACTION_MESSAGE.getActionString()));
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkPermission();
                }
            }, AppConstant.ONE_SECOND);
        } else {
            AppUtils.startService(this);
        }
        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isToDisplayLogin) {
            startActivity(new Intent(YonaActivity.this, PinActivity.class));
            finish();
        }

        goalManager.getUserGoal(null);
        activityCategoryManager.getActivityCategoriesById(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isToDisplayLogin = true;
    }

    /**
     * This will check whether user has given permission or not, if already given, it will start service, if not given, it will ask for permission.
     */
    private void checkPermission() {
        if (!AppUtils.hasPermission(this)) {
            showPermissionAlert();
        } else {
            AppUtils.startService(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                if (AppUtils.hasPermission(this)) {
                    Log.e("Permission", "permission granted.");
                    AppUtils.startService(this);
                } else {
                    checkPermission();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Show permission alert to user on start of application if permission is not granted.
     */
    private void showPermissionAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.app_usage_permission_title));
        builder.setMessage(getString(R.string.app_usage_permission_message));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    /**
     * To clear fragment stack.
     */
    private void clearAllFragment() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * To setup bottom tabs for application
     */
    private void setupTabs() {
        for (int i = 0; i < TOTAL_TABS; i++) {
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setCustomView(getTabView(i));
            mTabLayout.addTab(tab);
        }
    }

    /**
     * @param position position of tab in number
     * @return View to display for tab.
     */
    private View getTabView(int position) {
        View v = LayoutInflater.from(this).inflate(R.layout.bottom_tab_item, null);
        ImageView img = (ImageView) v.findViewById(R.id.tab_image);
        switch (position) {
            case 0:
                img.setImageResource(R.drawable.dashboard_selector);
                v.setTag(R.string.dashboard);
                break;
            case 1:
                img.setImageResource(R.drawable.friends_selector);
                v.setTag(R.string.frineds);
                break;
            case 2:
                img.setImageResource(R.drawable.challenges_selector);
                v.setTag(R.string.challenges);
                break;
            case 3:
                img.setImageResource(R.drawable.settings_selector);
                v.setTag(R.string.settings);
                break;
            default:
                break;
        }
        return v;
    }

    /**
     * @param intent pass intent as input for replace current fragment with new.
     */
    private void replaceFragmentWithAction(Intent intent) {
        boolean addToBackstack = false;
        if (intent != null) {
            String callAction = intent.getAction();
            boolean clearFragmentStack = intent.getBooleanExtra(AppConstant.CLEAR_FRAGMENT_STACK, false);

            if (!TextUtils.isEmpty(callAction)) {
                Fragment oldFragment = mContent;
                IntentEnum intentEnum = IntentEnum.fromName(callAction);

                if (intentEnum == null) {
                    return;
                }

                switch (intentEnum) {
                    case ACTION_DASHBOARD:
                        if (mContent instanceof DashboardFragment) {
                            return;
                        }
                        setCustomTitle(R.string.dashboard);
                        clearFragmentStack = true;
                        addToBackstack = true;
                        mContent = dashboardFragment;
                        break;
                    case ACTION_FRIENDS:
                        if (mContent instanceof FriendsFragment) {
                            return;
                        }
                        setCustomTitle(R.string.frineds);
                        clearFragmentStack = true;
                        addToBackstack = true;
                        mContent = friendsFragment;
                        break;
                    case ACTION_CHALLENGES:
                        if (mContent instanceof ChallengesFragment) {
                            return;
                        }
                        setCustomTitle(R.string.challenges);
                        clearFragmentStack = true;
                        addToBackstack = true;
                        mContent = challengesFragment;
                        break;
                    case ACTION_SETTINGS:
                        if (mContent instanceof SettingsFragment) {
                            return;
                        }
                        setCustomTitle(R.string.settings);
                        clearFragmentStack = true;
                        addToBackstack = true;
                        mContent = settingsFragment;
                        break;
                    case ACTION_PROFILE:
                        if (mContent instanceof ProfileFragment) {
                            return;
                        }
                        setCustomTitle(R.string.profile);
                        mContent = new ProfileFragment();
                        clearFragmentStack = false;
                        addToBackstack = true;
                        break;
                    case ACTION_MESSAGE:
                        if (mContent instanceof MessageFragment) {
                            return;
                        }
                        setTitle(R.string.message);
                        mContent = new MessageFragment();
                        clearFragmentStack = false;
                        addToBackstack = true;
                        break;
                    default:
                        break;
                }
                loadFragment(clearFragmentStack, addToBackstack, oldFragment);
            }
        }
    }

    private void updateToolBar() {
        if (mContent instanceof ChallengesFragment) {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_green);
        } else if (mContent instanceof DashboardFragment || mContent instanceof MessageFragment) {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_grape);
        } else if (mContent instanceof SettingsFragment) {
            mToolBar.setBackgroundColor(getResources().getColor(R.color.mango));
        } else if (mContent instanceof FriendsFragment) {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_blue);
        }
    }

    /**
     * @param clearFragmentStack : yes if require to clear fragment stack.
     * @param addToBackstack     : yes if require to add fragment to back stack.
     * @param oldFragment        pass oldfragment which need to add in back stack.
     */
    private void loadFragment(boolean clearFragmentStack, boolean addToBackstack, Fragment oldFragment) {
        if (mContent != null && !mContent.isAdded()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.back_slide_in, R.anim.back_slide_out);
            if (clearFragmentStack) {
                int count = getSupportFragmentManager().getBackStackEntryCount();
                for (int i = 0; i < count; ++i) {
                    getSupportFragmentManager().popBackStackImmediate();
                }
                if (addToBackstack) {
                    ft.addToBackStack(mContent.getClass().getName());
                }
                // adding back stack change listener again.
                ft.replace(R.id.container, mContent).commit();
            } else {
                oldFragment.setUserVisibleHint(false);
                if (!clearFragmentStack && addToBackstack) {
                    ft.addToBackStack(mContent.getClass().getName());
                }
                ft.add(R.id.container, mContent).commit();

                oldFragment.onPause();
                oldFragment.onStop();
            }
            updateToolBar();
        }
    }

    /**
     * When user press back button, it will check in back stack, if stack has entry, it will reload previous fragment.
     */
    @Override
    public void onBackStackChanged() {
        if (isBackPressed) {
            isBackPressed = false;
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(backStackEntry.getName());
                reloadOldFragment(fragment);
            }
        }
    }

    /**
     * @param fragment pass fragment to load on UI.
     */
    private void reloadOldFragment(Fragment fragment) {
        if (fragment != null && !fragment.equals(mContent)) {
            mContent = fragment;
            if (mContent instanceof ProfileFragment) {
                setCustomTitle(R.string.dashboard);
            } else if (mContent instanceof MessageFragment) {
                setCustomTitle(R.string.dashboard);
            }
        }
    }

    /**
     * This method will set custom title and buttons as per preference.
     *
     * @param titleId title id from strings.xml
     */
    private void setCustomTitle(int titleId) {
        toolbarTitle.setText(getString(titleId));
        switch (titleId) {
            case R.string.dashboard:
                rightIcon.setVisibility(View.VISIBLE);
                rightIcon.setTag(getString(R.string.dashboard));
                rightIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icn_reminder));
                break;
            case R.string.frineds:
                rightIcon.setVisibility(View.GONE);
                break;
            case R.string.challenges:
                rightIcon.setVisibility(View.GONE);
                break;
            case R.string.settings:
                mToolBar.setBackgroundResource(R.drawable.triangle_shadow_mango);
                rightIcon.setVisibility(View.GONE);
                break;
            case R.string.profile:
                rightIcon.setVisibility(View.VISIBLE);
                rightIcon.setTag(getString(R.string.profile));
                break;
            case R.string.message:
                rightIcon.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        isBackPressed = true;
        onBackStackChanged();
        super.onBackPressed();
    }
}
