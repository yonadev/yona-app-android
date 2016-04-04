/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager.impl;

import org.junit.Before;
import org.junit.Test;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.manager.SignupManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;

import static junit.framework.Assert.assertTrue;

/**
 * Created by kinnarvasa on 02/04/16.
 */
public class SignupManagerImplTest {

    private SignupManager manager;
    private RegisterUser registerUser;

    @Before
    public void setUp() throws Exception {
        manager = new SignupManagerImpl(DatabaseHelper.getInstance(YonaApplication.getAppContext()), YonaApplication.getAppContext());
    }

    @Test
    public void checkRegisterUser(){
        registerUser = new RegisterUser();
        registerUser.setFirstName("Kinnar");
        registerUser.setLastName("Vasa");
        registerUser.setMobileNumber("+31333333333");
        registerUser.setNickName("Kinnar");

        manager.registerUser(registerUser, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                assertTrue(result instanceof User);
            }

            @Override
            public void onError(Object errorMessage) {
                assertTrue(errorMessage instanceof ErrorMessage);
            }
        });
    }
}
