/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.api.receiver.YonaReceiver;
import nu.yona.app.api.utils.ServerErrorCode;
import nu.yona.app.customview.CustomAlertDialog;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.enums.IntentEnum;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.challenges.ChallengesFragment;
import nu.yona.app.ui.challenges.ChallengesGoalDetailFragment;
import nu.yona.app.ui.dashboard.DashboardFragment;
import nu.yona.app.ui.frinends.AddFriendFragment;
import nu.yona.app.ui.frinends.FriendsFragment;
import nu.yona.app.ui.message.MessageFragment;
import nu.yona.app.ui.pincode.PinActivity;
import nu.yona.app.ui.profile.EditDetailsProfileFragment;
import nu.yona.app.ui.profile.ProfileFragment;
import nu.yona.app.ui.settings.PrivacyFragment;
import nu.yona.app.ui.settings.SettingsFragment;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by kinnarvasa on 18/03/16.
 */
public class YonaActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener, EventChangeListener {

    private static final int TOTAL_TABS = 4;
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;
    private static final int PICK_CONTACT = 2;
    private static final int PICK_IMAGE = 3;
    private static final int PICK_CAMERA = 4;
    private Fragment mContent, homeFragment;
    private boolean isStateActive = false;
    private boolean mStateSaved;
    private boolean isBackPressed = false;
    private Toolbar mToolBar;
    private TabLayout mTabLayout;
    private YonaFontTextView toolbarTitle;
    private ImageView leftIcon;
    private ImageView rightIcon;
    private boolean isToDisplayLogin = true;
    private boolean skipVerification = false;
    private boolean launchedPinActiivty = false;
    private int READ_EXTERNAL_STORAGE_REQUEST = 1;
    private int CAMERA_REQUEST = 2;

    /**
     * This will register receiver for different events like screen on-off, boot, connectivity etc.
     */
    private static void registerReceiver() {
        YonaApplication.getAppContext().registerReceiver(new YonaReceiver(), new IntentFilter());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yona_layout);

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(AppConstant.FROM_LOGIN)) {
            isToDisplayLogin = false;
        }
        mToolBar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbarTitle = (YonaFontTextView) mToolBar.findViewById(R.id.toolbar_title);
        leftIcon = (ImageView) mToolBar.findViewById(R.id.leftIcon);
        rightIcon = (ImageView) mToolBar.findViewById(R.id.rightIcon);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        setupTabs();
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        YonaApplication.getEventChangeManager().registerListener(this);
        homeFragment = new DashboardFragment();
        mContent = homeFragment;

        getSupportFragmentManager().beginTransaction().add(R.id.container, mContent).commit();
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getCustomView().getTag().hashCode()) {
                    case R.string.dashboard:
                        replaceFragmentWithAction(new Intent(IntentEnum.ACTION_DASHBOARD.getActionString()));
                        break;
                    case R.string.friends:
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
        mStateSaved = false;
        if (isStateActive) {
            isStateActive = false;
        }
        super.onResume();
        if (isToDisplayLogin) {
            Intent intent = new Intent(YonaActivity.this, PinActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.LOGGED_IN);
            bundle.putInt(AppConstant.COLOR_CODE, ContextCompat.getColor(this, R.color.grape));
            bundle.putInt(AppConstant.TITLE_BACKGROUND_RESOURCE, R.drawable.triangle_shadow_grape);
            intent.putExtras(bundle);
            startActivity(intent);
            launchedPinActiivty = true;
        }
        getUser();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    private void getUser() {
        User user = APIManager.getInstance().getAuthenticateManager().getUser();
        if (user != null) {
            APIManager.getInstance().getAuthenticateManager().getUser(user.getLinks().getSelf().getHref(), null);
        }
    }

    /**
     * Show error.
     *
     * @param errorMessage the error message
     */
    public void showError(ErrorMessage errorMessage) {
        if (!isFinishing()) {
            if (errorMessage.getCode().equals(ServerErrorCode.USER_NOT_FOUND)) {
                CustomAlertDialog.show(YonaActivity.this, errorMessage.getMessage(), getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = YonaApplication.getUserPreferences().edit();
                        editor.clear();
                        editor.putBoolean(PreferenceConstant.STEP_TOUR, true);
                        editor.commit();
                        startActivity(new Intent(YonaActivity.this, LaunchActivity.class));
                        dialogInterface.dismiss();
                    }
                });
            } else {
                CustomAlertDialog.show(YonaActivity.this, errorMessage.getMessage(), getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStateSaved = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (launchedPinActiivty) {
            isToDisplayLogin = false;
            launchedPinActiivty = false;
        } else if (!skipVerification) {
            isToDisplayLogin = true;
        } else {
            skipVerification = false;
        }
        mStateSaved = true;
    }

    /**
     * This will check whether user has given permission or not, if already given, it will start service, if not given, it will ask for permission.
     */
    private void checkPermission() {
        //As this feature is not yet implemented, commenting, so user don't see any alert during testing.
//        if (!AppUtils.hasPermission(this)) {
//            showPermissionAlert();
//        } else {
//            AppUtils.startService(this);
//        }
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
            case PICK_CONTACT:
                if (resultCode == RESULT_OK) {
                    showContactDetails(data);
                }
                break;
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    loadPickedImage(data);
                }
                break;
            case PICK_CAMERA:
                if (resultCode == RESULT_OK) {
                    loadCaptureImage(data);
                }
                break;
            default:
                break;
        }
    }

    private void loadCaptureImage(final Intent data) {
        new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {
                try {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                    FileOutputStream fo;
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    return thumbnail;
                } catch (Exception e) {
                    AppUtils.throwException(YonaApplication.class.getSimpleName(), e, Thread.currentThread(), null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_RECEIVED_PHOTO, (Bitmap) o);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void loadPickedImage(final Intent data) {
        try {
            Uri selectedImageUri = data.getData();
            String[] projection = {MediaStore.MediaColumns.DATA};
            CursorLoader cursorLoader = new CursorLoader(YonaActivity.this, selectedImageUri, projection, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            final String selectedImagePath = cursor.getString(column_index);
            cursor.close();
            new AsyncTask<Void, Void, Object>() {

                @Override
                protected Object doInBackground(Void... params) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(selectedImagePath, options);
                    final int REQUIRED_SIZE = 200;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) {
                        scale *= 2;
                    }
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    return BitmapFactory.decodeFile(selectedImagePath, options);
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_RECEIVED_PHOTO, (Bitmap) o);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            AppUtils.throwException(YonaApplication.class.getSimpleName(), e, Thread.currentThread(), null);
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
                v.setTag(R.string.friends);
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
        if (intent != null) {
            boolean addToBackstack = false;
            String callAction = intent.getAction();
            Fragment oldFragment = mContent;
            boolean clearFragmentStack = intent.getBooleanExtra(AppConstant.CLEAR_FRAGMENT_STACK, false);
            if (!TextUtils.isEmpty(callAction)) {
                IntentEnum intentEnum = IntentEnum.fromName(callAction);

                if (intentEnum == null) {
                    return;
                }

                switch (intentEnum) {
                    case ACTION_DASHBOARD:
                        if (mContent instanceof DashboardFragment) {
                            return;
                        }
                        clearFragmentStack = true;
                        addToBackstack = false;
                        mContent = new DashboardFragment();
                        break;
                    case ACTION_FRIENDS:
                        if (mContent instanceof FriendsFragment) {
                            return;
                        }
                        clearFragmentStack = true;
                        addToBackstack = false;
                        mContent = new FriendsFragment();
                        break;
                    case ACTION_CHALLENGES:
                        /*if (mContent instanceof ChallengesFragment) {
                            return;
                        }*/
                        clearFragmentStack = true;
                        addToBackstack = false;
                        mContent = new ChallengesFragment();
                        break;
                    case ACTION_SETTINGS:
                        if (mContent instanceof SettingsFragment) {
                            return;
                        }
                        clearFragmentStack = true;
                        addToBackstack = false;
                        mContent = new SettingsFragment();
                        break;
                    case ACTION_PROFILE:
                        if (mContent instanceof ProfileFragment) {
                            return;
                        }
                        mContent = new ProfileFragment();
                        clearFragmentStack = false;
                        addToBackstack = true;
                        break;
                    case ACTION_EDIT_PROFILE:
                        if (mContent instanceof EditDetailsProfileFragment) {
                            return;
                        }
                        mContent = new EditDetailsProfileFragment();
                        clearFragmentStack = false;
                        addToBackstack = true;
                        break;
                    case ACTION_MESSAGE:
                        if (mContent instanceof MessageFragment) {
                            return;
                        }
                        mContent = new MessageFragment();
                        clearFragmentStack = false;
                        addToBackstack = true;
                        break;
                    case ACTION_CHALLENGES_GOAL:
                        if (mContent instanceof ChallengesGoalDetailFragment) {
                            return;
                        }
                        mContent = new ChallengesGoalDetailFragment();
                        mContent.setArguments(intent.getExtras());
                        clearFragmentStack = false;
                        addToBackstack = true;
                        break;
                    case ACTION_ADD_FRIEND:
                        if (mContent instanceof AddFriendFragment) {
                            return;
                        }
                        mContent = new AddFriendFragment();
                        clearFragmentStack = false;
                        addToBackstack = true;
                        break;
                    case ACTION_PRIVACY_POLICY:
                        if (mContent instanceof PrivacyFragment) {
                            return;
                        }
                        mContent = new PrivacyFragment();
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

    private void updateToolBarBackground() {
        if (mContent instanceof ChallengesFragment || mContent instanceof ChallengesGoalDetailFragment) {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_green);
        } else if (mContent instanceof DashboardFragment || mContent instanceof MessageFragment) {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_grape);
        } else if (mContent instanceof SettingsFragment) {
            mToolBar.setBackgroundResource(R.drawable.triangle_shadow_mango);
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
        hideToolbarIcon();
        if (mContent != null && !mContent.isAdded()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.back_slide_in, R.anim.back_slide_out);
            if (clearFragmentStack) {
                getSupportFragmentManager().removeOnBackStackChangedListener(this);
                int count = getSupportFragmentManager().getBackStackEntryCount();
                for (int i = 0; i < count; ++i) {
                    if (mStateSaved) {
                        isStateActive = true;
                        break;
                    } else {
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                }
                if (mStateSaved) {
                    return;
                }
                removeCurrentFragment();
                // adding back stack change listener again.
                getSupportFragmentManager().addOnBackStackChangedListener(this);
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
            updateToolBarBackground();
        }
    }


    /**
     * Remove current fragment.
     */
    public void removeCurrentFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }
        transaction.commit();
    }


    /**
     * When user press back button, it will check in back stack, if stack has entry, it will reload previous fragment.
     */
    @Override
    public void onBackStackChanged() {
        if (isBackPressed) {
            isBackPressed = false;
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (fragment != null && !fragment.equals(mContent)) {
                mContent = fragment;
                mContent.setUserVisibleHint(true);
                if (mContent instanceof ChallengesFragment && ((ChallengesFragment) mContent).isChildViewVisible()) {
                    ((ChallengesFragment) mContent).updateView();
                }
                mContent.onStart();
                mContent.onResume();
                updateToolBarBackground();
                hideToolbarIcon();
            }
        }
    }

    /**
     * Update title.
     *
     * @param titleId the title id
     */
    public void updateTitle(int titleId) {
        updateTitle(getString(titleId));
    }

    /**
     * Udpate title.
     *
     * @param title the title
     */
    public void updateTitle(String title) {
        toolbarTitle.setText(title);
    }

    /**
     * Hide toolbar icon.
     */
    public void hideToolbarIcon() {
        rightIcon.setVisibility(View.GONE);
        leftIcon.setVisibility(View.GONE);
    }

    private boolean isStackEmpty() {
        return getSupportFragmentManager().getBackStackEntryCount() == 0;
    }

    @Override
    public void onBackPressed() {

        if (mContent instanceof ChallengesFragment && ((ChallengesFragment) mContent).isChildViewVisible()) {
            ((ChallengesFragment) mContent).updateView();
        } else {
            isBackPressed = true;
            if (isStackEmpty() && !(mContent instanceof DashboardFragment)) {
                Fragment oldFragment = mContent;
                //todo - check which content of instace is that and according update the view
                mContent = homeFragment;
                mTabLayout.getTabAt(0).select();
                loadFragment(true, false, oldFragment);
                return;
            }
            super.onBackPressed();
        }
    }

    /**
     * Replace fragment.
     *
     * @param intent the intent
     */
    public void replaceFragment(Intent intent) {
        replaceFragmentWithAction(intent);
    }

    /**
     * Gets right icon.
     *
     * @return the right icon
     */
    public ImageView getRightIcon() {
        return rightIcon;
    }

    /**
     * Gets left icon.
     *
     * @return the left icon
     */
    public ImageView getLeftIcon() {
        return leftIcon;
    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_OPEN_CONTACT_BOOK:
                openContactBook();
                break;
            case EventChangeManager.EVENT_CLOSE_YONA_ACTIVITY:
                finish();
                break;
            default:
                break;
        }
    }

    private void openContactBook() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
        skipVerification = true;
    }

    /**
     * Choose image.
     */
    public void chooseImage() {
        final CharSequence[] items = {getString(R.string.profile_take_photo), getString(R.string.profile_choose_from_library)};
        AlertDialog.Builder builder = new AlertDialog.Builder(YonaActivity.this);
        builder.setTitle(getString(R.string.profile_choose_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.profile_take_photo))) {
                    openCamera();
                } else if (items[item].equals(getString(R.string.profile_choose_from_library))) {
                    openCaptureImage();
                }
            }
        });
        builder.show();
    }

    private void showContactDetails(final Intent data) {
        final RegisterUser user = new RegisterUser();
        new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {
                try {
                    Uri result = data.getData();
                    String id = result.getLastPathSegment();

                    //To get email address of user
                    Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{id}, null);

                    if (cursor.moveToFirst()) {
                        user.setEmailAddress(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                    }
                    cursor.close();

                    // To get contact name etc of user
                    String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ? ";
                    String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, id};
                    Cursor nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
                    while (nameCur.moveToNext()) {
                        user.setFirstName(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
                        user.setLastName(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));

                    }
                    nameCur.close();

                    // To get Mobile number of contact
                    Cursor phoneCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                    if (phoneCur.moveToFirst()) {
                        String number = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        number = TextUtils.isEmpty(number) ? getString(R.string.blank) : number.replace(getString(R.string.space), getString(R.string.blank));
                        user.setMobileNumber(number);
                    }
                    phoneCur.close();
                } catch (Exception e) {
                    AppUtils.throwException(YonaActivity.class.getSimpleName(), e, Thread.currentThread(), null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                showLoadingView(false, null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CONTAT_CHOOSED, user);
                    }
                }, AppConstant.TIMER_DELAY);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Is skip verification boolean.
     *
     * @return the boolean
     */
    public boolean isSkipVerification() {
        return skipVerification;
    }

    /**
     * Sets skip verification.
     *
     * @param skipVerification the skip verification
     */
    public void setSkipVerification(boolean skipVerification) {
        this.skipVerification = skipVerification;
    }

    /**
     * Show keyboard.
     *
     * @param editText the edit text
     */
    public void showKeyboard(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    private boolean openCaptureImage() {
        setSkipVerification(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST);
            return false;
        } else {
            pickImage();
            return true;
        }
    }

    private boolean openCamera() {
        setSkipVerification(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST);
            return false;
        } else {
            pickCamera();
            return true;
        }
    }

    private void pickCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        skipVerification = true;
        startActivityForResult(intent, PICK_CAMERA);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
        skipVerification = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                openCaptureImage();
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } else if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickCamera();
            } else {
                openCamera();
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
