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

import android.util.Log;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;

import nu.yona.app.YonaApplication;
import nu.yona.app.YonaTestCase;
import nu.yona.app.api.manager.AuthenticateManager;
import nu.yona.app.api.manager.dao.AuthenticateDAO;
import nu.yona.app.api.manager.network.AuthenticateNetworkImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.Links;
import nu.yona.app.api.model.OTPVerficationCode;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.listener.DataLoadListenerImpl;

import static com.google.android.gms.internal.zzs.TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by kinnarvasa on 02/04/16.
 */
public class AuthenticateManagerImplTest extends YonaTestCase {
    private AuthenticateManagerImpl manager;
    private AuthenticateNetworkImpl authenticateNetworkImplMock;
    private AuthenticateDAO authenticateDAOMock;
    private RegisterUser registerUser;

    private String correctMobileNumber = "+919686270640";
    private String wrongMobileNumber = "+9999211";
    private OTPVerficationCode correctOtpCode = new OTPVerficationCode("1234");
    private OTPVerficationCode wrongOtpCode = new OTPVerficationCode("1111");
    private String nullOtpCode = null;

    private DataLoadListener genericResponseListener = new DataLoadListener() {
        @Override
        public void onDataLoad(Object result) {
            assertTrue(result instanceof User);
        }
        @Override
        public void onError(Object errorMessage) {
            assertTrue(errorMessage instanceof  ErrorMessage);
        }
    };


    @Before
    public void setUp() throws Exception {
        setUpApplicationTestData();
        setUpRegisterUser();
        manager = new AuthenticateManagerImpl(YonaApplication.getAppContext());
        mockRequiredClasses();
        setUpMockedAuthNetworkDaoMethods();
        setUpMockedAuthNetworkImplMethods();
    }

    @Test
    public void verifyUserRegistrationWithProperData() {
        registerUser.setMobileNumber(correctMobileNumber);
        verifyUserRegistration();
    }

    @Test
    public void verifyUserRegistrationWithInvalidData() {
        registerUser.setMobileNumber(wrongMobileNumber);
        verifyUserRegistration();
    }

    @Test
    public void verifyUserPassCodeWithCorrectCode() {
        confirmMobileNumberWithOtp(correctOtpCode.getCode());
    }

    @Test
    public void verifyUserPassCodeWithWrongCode() {
        confirmMobileNumberWithOtp(wrongOtpCode.getCode());
    }


    @Test
    public void verifyUserPassCodeWithNullCode() {
        confirmMobileNumberWithOtp(nullOtpCode);
    }

    private void verifyUserRegistration(){
        manager.registerUser(registerUser, true, genericResponseListener);
    }


    private void confirmMobileNumberWithOtp( String code) {
        manager.verifyOTP(code, genericResponseListener);
    }

    private User getMockedUser(){
        User user = new User();
        user.setNickname("Mocked User");
        Href userSelfHref = new Href();
        userSelfHref.setHref("Url");
        Links links = new Links();
        links.setSelf(userSelfHref);
        links.setEdit(userSelfHref);
        links.setYonaConfirmMobileNumber(userSelfHref);
        user.setLinks(links);
        return user;
    }

    private void setUpApplicationTestData(){
        YonaApplication yonaApplication = (YonaApplication) RuntimeEnvironment.application;
        yonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences();
        yonaApplication.getEventChangeManager().getSharedPreference().setYonaPassword("AES:128:hiQK2AjU4YE8tEuJlUy+Ug==");
        yonaApplication.getEventChangeManager().getDataState().setUser(getMockedUser());
    }
    private void setUpRegisterUser(){
        registerUser = new RegisterUser();
        registerUser.setFirstName("Siva");
        registerUser.setLastName("Teja");
        registerUser.setNickName("Siva");
    }

    private  void mockRequiredClasses(){
        authenticateNetworkImplMock = Mockito.mock(AuthenticateNetworkImpl.class);
        manager.setAuthNetwork(authenticateNetworkImplMock);
        authenticateDAOMock = Mockito.mock(AuthenticateDAO.class);
        manager.setAuthenticateDao(authenticateDAOMock);
    }

    private void handleUserRegResponse(String url, String password, RegisterUser regUserfromResponse, boolean isEditMode, final DataLoadListener listener){
        String regUserMobileNumFromReponse = (String)regUserfromResponse.getMobileNumber();
        if(regUserMobileNumFromReponse.equals(correctMobileNumber)){
            listener.onDataLoad(getMockedUser());
        }else{
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setMessage("Invalid Mobile Number");
            errorMessage.setCode("400");
            listener.onError(errorMessage);
        }
    }

    private void handleVerifyMobileResponse(String password, String url, OTPVerficationCode otpFromresponse, final DataLoadListener listener ) {
        String otpFromresponseCode = (String) otpFromresponse.getCode();
        if (otpFromresponseCode.equals(correctOtpCode.getCode())) {
            listener.onDataLoad(getMockedUser());
        } else {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setMessage("Invalid Pass Code");
            errorMessage.setCode("400");
            listener.onError(errorMessage);
        }
    }

    private void handleGetUserResponse(String url, String yonaPassword ,DataLoadListener listener){
        listener.onDataLoad(getMockedUser());
    }


    private void setUpMockedAuthNetworkImplMethods(){
        Mockito.doAnswer((Answer) invocation -> {
            handleUserRegResponse(invocation.getArgument(0),invocation.getArgument(1),invocation.getArgument(2),invocation.getArgument(3),invocation.getArgument(4));
            return null;
        }).when(authenticateNetworkImplMock).registerUser(ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(String.class),ArgumentMatchers.any(RegisterUser.class),
                ArgumentMatchers.any(Boolean.class), ArgumentMatchers.any(DataLoadListener.class));

        Mockito.doAnswer((Answer) invocation -> {
            handleVerifyMobileResponse(invocation.getArgument(0),invocation.getArgument(1),invocation.getArgument(2),invocation.getArgument(3));
            return null;
        }).when(authenticateNetworkImplMock).verifyMobileNumber(ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(String.class), ArgumentMatchers.any(OTPVerficationCode.class),
                ArgumentMatchers.any(DataLoadListener.class));

        Mockito.doAnswer((Answer) invocation -> {
            handleGetUserResponse( invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2));
            return null;
        }).when(authenticateNetworkImplMock).getUser(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(DataLoadListener.class));
    }


    private void handleUpdateDataResponse(Object result, DataLoadListener listener){
        if(listener != null ){
            listener.onDataLoad(getMockedUser());
        }else{
            assertFalse(false);
        }
    }

    private void setUpMockedAuthNetworkDaoMethods() {
        Mockito.doAnswer((Answer) invocation -> {
            handleUpdateDataResponse(invocation.getArgument(0),invocation.getArgument(1));
            return null;
        }).when(authenticateDAOMock).updateDataForRegisterUser(ArgumentMatchers.any(Object.class),
                ArgumentMatchers.any(DataLoadListener.class));

        Mockito.when(authenticateDAOMock.getUser()).thenReturn(getMockedUser());
    }
}