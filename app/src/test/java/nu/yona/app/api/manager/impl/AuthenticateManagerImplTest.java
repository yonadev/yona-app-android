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
import nu.yona.app.api.manager.AuthenticateManager;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;

import static org.junit.Assert.assertTrue;

/**
 * Created by kinnarvasa on 02/04/16.
 */
public class AuthenticateManagerImplTest {

    private AuthenticateManager manager;
    private RegisterUser registerUser;
    private String password = "12423423234234324234";

    @Before
    public void setUp() throws Exception {
        manager = new AuthenticateManagerImpl(YonaApplication.getAppContext());
    }

    @Test
    public void checkRegisterUser() {
        registerUser = new RegisterUser();
        registerUser.setFirstName("Kinnar");
        registerUser.setLastName("Vasa");
        registerUser.setMobileNumber("+31873449748");
        registerUser.setNickName("Kinnar");

        manager.registerUser(registerUser, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                validateMobileNumber(password, ((User) result).getMobileNumberConfirmationCode());
                assertTrue(result instanceof User);
            }

            @Override
            public void onError(Object errorMessage) {
                assertTrue(errorMessage instanceof ErrorMessage);
            }
        });
    }

    public void validateMobileNumber(String password, String code) {
        manager.verifyOTP(code, new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                System.out.println(result.toString());
            }

            @Override
            public void onError(Object errorMessage) {
                System.out.println(errorMessage.toString());
            }
        });
    }
}
