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

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.manager.NotificationManager;
import nu.yona.app.api.manager.network.NotificationNetworkImpl;
import nu.yona.app.api.model.Embedded;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.MessageBody;
import nu.yona.app.api.model.Properties;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.api.model.YonaMessages;
import nu.yona.app.enums.NotificationMessageEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.DateUtility;

/**
 * Created by kinnarvasa on 09/05/16.
 */
public class NotificationManagerImpl implements NotificationManager {

    private NotificationNetworkImpl notificationNetwork;
    private Context mContext;

    /**
     * Instantiates a new Notification manager.
     *
     * @param context the context
     */
    public NotificationManagerImpl(Context context) {
        notificationNetwork = new NotificationNetworkImpl();
        this.mContext = context;
    }

    /**
     * Gets message.
     *
     * @param listener the listener
     */
    public void getMessage(DataLoadListener listener) {
        getMessage(0, 0, listener); //set default page 0, start page = 0
    }

    public void getMessage(final int itemsPerPage, final int pageNo, final DataLoadListener listener) {
        getMessage(itemsPerPage, pageNo, false, listener);
    }

    /**
     * Gets message.
     *
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    public void getMessage(final int itemsPerPage, final int pageNo, boolean isUnreadStatus, final DataLoadListener listener) {
        getMessage(itemsPerPage, pageNo, isUnreadStatus, listener, false);
    }

    private void getMessage(final int itemsPerPage, final int pageNo, final boolean isUnreadStatus, final DataLoadListener listener, final boolean isProcessUpdate) {
        try {
            User user = YonaApplication.getEventChangeManager().getDataState().getUser();
            if (user != null && user.getLinks() != null && user.getLinks().getYonaMessages() != null
                    && !TextUtils.isEmpty(user.getLinks().getYonaMessages().getHref())) {
                notificationNetwork.getMessage(user.getLinks().getYonaMessages().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), isUnreadStatus,
                        itemsPerPage, pageNo, new DataLoadListener() {
                            @Override
                            public void onDataLoad(Object result) {
                                if (listener != null) {
                                    if (result instanceof YonaMessages) {
                                        YonaMessages yonaMessages = (YonaMessages) result;
                                        if (yonaMessages != null && yonaMessages.getEmbedded() != null) {
                                            Embedded embedded = yonaMessages.getEmbedded();
                                            List<YonaMessage> listMessages = embedded.getYonaMessages();
                                            boolean isAnyProcessed = false;
                                            SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.YONA_LONG_DATE_FORMAT, Locale.getDefault());
                                            for (YonaMessage message : listMessages) {
                                                //update enum
                                                if (message.getStatus() != null) {
                                                    message.setNotificationMessageEnum(NotificationMessageEnum.getNotificationMessageEnum(message.getType(), message.getStatus()));
                                                } else if (message.getDropBuddyReason() != null) {
                                                    message.setNotificationMessageEnum(NotificationMessageEnum.getNotificationMessageEnum(message.getType(), message.getDropBuddyReason()));
                                                } else {
                                                    message.setNotificationMessageEnum(NotificationMessageEnum.getNotificationMessageEnum(message.getType(), message.getChange()));
                                                }
                                                if (message.getLinks() != null && message.getLinks().getYonaUser() != null
                                                        && !TextUtils.isEmpty(message.getLinks().getYonaUser().getHref())) {
                                                    if (message.getEmbedded() == null) {
                                                        message.setEmbedded(new Embedded());
                                                    }
                                                    message.getEmbedded().setYonaUser(getYonaUser(message.getLinks().getYonaUser().getHref()));
                                                }
                                                String uploadDate = "";

                                                String createdTime = message.getCreationTime();
                                                try {
                                                    Date date = sdf.parse(createdTime);

                                                    Calendar futureCalendar = Calendar.getInstance();
                                                    futureCalendar.setTime(date);

                                                    uploadDate = DateUtility.getRelativeDate(futureCalendar);
                                                    message.setStickyTitle(uploadDate);
                                                } catch (Exception e) {
                                                    AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
                                                }
                                                yonaMessages.setEmbedded(embedded);
                                                if (!isProcessUpdate && message != null && message.getLinks() != null && message.getLinks().getYonaPreocess() != null
                                                        && !TextUtils.isEmpty(message.getLinks().getYonaPreocess().getHref())) {
                                                    isAnyProcessed = true;
                                                    MessageBody body = new MessageBody();
                                                    body.setProperties(new Properties());
                                                    postMessageForProcess(message.getLinks().getYonaPreocess().getHref(), body);
                                                }
                                            }
                                            if (isAnyProcessed) {
                                                getMessage(itemsPerPage, pageNo, isUnreadStatus, listener, true);
                                            }
                                            APIManager.getInstance().getAuthenticateManager().getUserFromServer();
                                        }
                                        listener.onDataLoad(yonaMessages);
                                    }
                                }
                            }

                            @Override
                            public void onError(Object errorMessage) {
                                throwError(listener, errorMessage);
                            }
                        });
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } catch (Exception e) {
            AppUtils.throwException(NotificationManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    private RegisterUser getYonaUser(String href) {
        User user = YonaApplication.getEventChangeManager().getDataState().getUser();
        if (user != null && user.getEmbedded() != null && user.getEmbedded().getYonaBuddies() != null
                && user.getEmbedded().getYonaBuddies().getEmbedded() != null
                && user.getEmbedded().getYonaBuddies().getEmbedded().getYonaBuddies() != null) {
            List<YonaBuddy> yonaBuddies = user.getEmbedded().getYonaBuddies().getEmbedded().getYonaBuddies();
            for (YonaBuddy yonaBuddy : yonaBuddies) {
                if (yonaBuddy.getEmbedded() != null && yonaBuddy.getEmbedded().getYonaUser() != null
                        && yonaBuddy.getEmbedded().getYonaUser().getLinks() != null && yonaBuddy.getEmbedded().getYonaUser().getLinks().getSelf() != null
                        && !TextUtils.isEmpty(yonaBuddy.getEmbedded().getYonaUser().getLinks().getSelf().getHref())
                        && yonaBuddy.getEmbedded().getYonaUser().getLinks().getSelf().getHref().equals(href)) {
                    RegisterUser registerUser = new RegisterUser();
                    registerUser.setFirstName(yonaBuddy.getEmbedded().getYonaUser().getFirstName());
                    registerUser.setLastName(yonaBuddy.getEmbedded().getYonaUser().getLastName());
                    registerUser.setMobileNumber(yonaBuddy.getEmbedded().getYonaUser().getMobileNumber());
                    return registerUser;
                }

            }
        }
        return null;
    }

    /**
     * Delete message.
     *
     * @param message      the message
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    public void deleteMessage(YonaMessage message, final int itemsPerPage, final int pageNo, final DataLoadListener listener) {
        try {
            if (message != null && message.getLinks() != null
                    && message.getLinks().getSelf() != null && !TextUtils.isEmpty(message.getLinks().getSelf().getHref())) {
                notificationNetwork.deleteMessage(message.getLinks().getSelf().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        getMessage(itemsPerPage, pageNo, listener);
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        throwError(listener, errorMessage);
                    }
                });
            }
        } catch (Exception e) {
            AppUtils.throwException(NotificationManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    private void postMessageForProcess(String url, MessageBody body) {
        notificationNetwork.postMessage(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), body, null);
    }

    /**
     * Post message.
     *
     * @param url          the url
     * @param body         the body
     * @param itemsPerPage the items per page
     * @param pageNo       the page no
     * @param listener     the listener
     */
    public void postMessage(String url, MessageBody body, final int itemsPerPage, final int pageNo, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                notificationNetwork.postMessage(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), body, new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        getMessage(itemsPerPage, pageNo, listener);
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        throwError(listener, errorMessage);
                    }
                });
            }
        } catch (Exception e) {
            AppUtils.throwException(NotificationManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    public void deleteMessage(@NonNull String url, final int itemsPerPage, final int pageNo, final DataLoadListener listener) {
        try {
            notificationNetwork.deleteMessage(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    getMessage(itemsPerPage, pageNo, listener);
                }

                @Override
                public void onError(Object errorMessage) {
                    throwError(listener, errorMessage);
                }
            });
        } catch (Exception e) {
            AppUtils.throwException(NotificationManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    public void setReadMessage(List<YonaMessage> yonaMessageList, YonaMessage message, DataLoadListener listener) {
        try {
            if (yonaMessageList != null && yonaMessageList.size() > 0 && message != null
                    && message.getLinks() != null && message.getLinks().getMarkRead() != null && !TextUtils.isEmpty(message.getLinks().getMarkRead().getHref())) {
                String selectedMessageUrl = null;
                // post message on server:
                MessageBody body = new MessageBody();
                body.setProperties(new Properties());
                postMessageForProcess(message.getLinks().getMarkRead().getHref(), body);
                // update on UI
                if (message.getLinks().getSelf() != null && !TextUtils.isEmpty(message.getLinks().getSelf().getHref())) {
                    selectedMessageUrl = message.getLinks().getSelf().getHref();
                }
                message.getLinks().setMarkRead(null);
                if (yonaMessageList != null && selectedMessageUrl != null) {

                    for (int i = 0; i < yonaMessageList.size(); i++) {
                        if (yonaMessageList.get(i) != null && yonaMessageList.get(i).getLinks() != null && yonaMessageList.get(i).getLinks().getSelf() != null
                                && yonaMessageList.get(i).getLinks().getSelf().getHref() != null && !TextUtils.isEmpty(yonaMessageList.get(i).getLinks().getSelf().getHref())
                                && yonaMessageList.get(i).getLinks().getSelf().getHref().equals(selectedMessageUrl)) {
                            yonaMessageList.set(i, message);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            AppUtils.throwException(NotificationManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        } finally {
            listener.onDataLoad(yonaMessageList);
        }
    }

    private void throwError(DataLoadListener listener, Object errorMessage) {
        if (listener != null) {
            if (errorMessage instanceof ErrorMessage) {
                listener.onError(errorMessage);
            } else {
                listener.onError(new ErrorMessage(errorMessage.toString()));
            }
        }
    }
}
