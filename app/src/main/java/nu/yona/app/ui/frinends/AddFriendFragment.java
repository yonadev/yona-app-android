/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.frinends;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.ViewPagerAdapter;
import nu.yona.app.ui.YonaActivity;

/**
 * Created by kinnarvasa on 27/04/16.
 */
public class AddFriendFragment extends BaseFragment {

    private final int ADD_FRIEND_MANUALLY = 0, ADD_FRIENT_CONTACT = 1;
    private ViewPager viewPager;
    private final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_friend_fragment, null);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new AddFriendManually(), getString(R.string.addfriendmanually));
        adapter.addFragment(new AddFriendContacts(), getString(R.string.addfriendcontacts));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == ADD_FRIENT_CONTACT && getPermissionToReadUserContacts()) {
                    openContactBook();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public boolean getPermissionToReadUserContacts() {
        ((YonaActivity) getActivity()).setSkipVerification(true);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }

    private void openContactBook() {
        YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_OPEN_CONTACT_BOOK, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.setCurrentItem(ADD_FRIEND_MANUALLY, true);
        ((YonaActivity)getActivity()).updateTitle(R.string.friends);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openContactBook();
            } else {
                viewPager.setCurrentItem(ADD_FRIEND_MANUALLY);
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
