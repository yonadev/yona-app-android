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
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.manager.ActivityManager;
import nu.yona.app.api.manager.dao.ActivityTrackerDAO;
import nu.yona.app.api.manager.network.ActivityNetworkImpl;
import nu.yona.app.api.model.Activity;
import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.api.model.AppActivity;
import nu.yona.app.api.model.Day;
import nu.yona.app.api.model.DayActivities;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.Embedded;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.Message;
import nu.yona.app.api.model.Properties;
import nu.yona.app.api.model.TimeZoneSpread;
import nu.yona.app.api.model.User;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.api.model.WeekDayActivity;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaDayActivityOverview;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.api.model.YonaWeekActivityOverview;
import nu.yona.app.customview.graph.GraphUtils;
import nu.yona.app.enums.ChartTypeEnum;
import nu.yona.app.enums.GoalsEnum;
import nu.yona.app.enums.WeekDayEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.DateUtility;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public class ActivityManagerImpl implements ActivityManager {

    private final ActivityNetworkImpl activityNetwork;
    private final ActivityTrackerDAO activityTrackerDAO;
    private final Context mContext;
    private final int maxSpreadTime = 15;
    private final SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.YONA_DATE_FORMAT, Locale.getDefault());

    /**
     * Instantiates a new Activity manager.
     *
     * @param context the context
     */
    public ActivityManagerImpl(Context context) {
        activityNetwork = new ActivityNetworkImpl();
        activityTrackerDAO = new ActivityTrackerDAO(DatabaseHelper.getInstance(context));
        mContext = context;
    }

    @Override
    public void getDaysActivity(boolean loadMore, boolean isBuddyFlow, Href url, DataLoadListener listener) {
        EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
        if (loadMore || embeddedYonaActivity == null
                || embeddedYonaActivity.getDayActivityList() == null
                || embeddedYonaActivity.getDayActivityList().size() == 0) {
            int pageNo = (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
                    && embeddedYonaActivity.getDayActivityList() != null && embeddedYonaActivity.getDayActivityList().size() > 0) ? embeddedYonaActivity.getPage().getNumber() + 1 : 0;
            //User user = YonaApplication.getEventChangeManager().getDataState().getUser();
            if (url != null && !TextUtils.isEmpty(url.getHref())) {
                getDailyActivity(url.getHref(), isBuddyFlow, AppConstant.PAGE_SIZE, pageNo, listener);
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } else {
            listener.onDataLoad(YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity().getDayActivityList());
        }
    }

    private void getDetailOfEachSpread() {
        List<DayActivity> dayActivities = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity().getDayActivityList();
        for (final DayActivity dayActivity : dayActivities) {
            if (dayActivity.getTimeZoneSpread() == null || (dayActivity.getTimeZoneSpread() != null && dayActivity.getTimeZoneSpread().size() == 0) || dayActivity.getChartTypeEnum() == ChartTypeEnum.TIME_FRAME_CONTROL) {
                APIManager.getInstance().getActivityManager().getDayDetailActivity(dayActivity.getLinks().getYonaDayDetails().getHref(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        if (result instanceof DayActivity) {
                            try {
                                DayActivity resultActivity = generateTimeZoneSpread((DayActivity) result);
                                List<DayActivity> dayActivityList = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity().getDayActivityList();
                                for (int i = 0; i < dayActivityList.size(); i++) {
                                    try {
                                        if (dayActivityList.get(i).getLinks().getYonaDayDetails().getHref().equals(resultActivity.getLinks().getSelf().getHref())) {
                                            dayActivityList.get(i).setTimeZoneSpread(resultActivity.getTimeZoneSpread());
                                            YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity().getDayActivityList().set(i, updateLinks(dayActivityList.get(i), resultActivity));
                                            break;
                                        }
                                    } catch (Exception e) {
                                        AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
                                    }
                                }
                            } catch (Exception e) {
                                AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
                            }
                        }
                    }

                    @Override
                    public void onError(Object errorMessage) {

                    }
                });
            }
        }
    }

    private void getDetailOfEachWeekSpread() {
        List<WeekActivity> weekActivities = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().getWeekActivityList();
        for (final WeekActivity weekActivity : weekActivities) {
            if (weekActivity.getTimeZoneSpread() == null || (weekActivity.getTimeZoneSpread() != null && weekActivity.getTimeZoneSpread().size() == 0)) {
                APIManager.getInstance().getActivityManager().getWeeksDetailActivity(weekActivity.getLinks().getWeekDetails().getHref(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        if (result instanceof WeekActivity) {
                            try {
                                WeekActivity resultActivity = generateTimeZoneSpread((WeekActivity) result);
                                List<WeekActivity> weekActivityList = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().getWeekActivityList();
                                for (int i = 0; i < weekActivityList.size(); i++) {
                                    try {
                                        if (weekActivityList.get(i).getLinks().getWeekDetails().getHref().equals(resultActivity.getLinks().getSelf().getHref())) {
                                            weekActivityList.get(i).setTimeZoneSpread(resultActivity.getTimeZoneSpread());
                                            weekActivityList.set(i, updateLinks(weekActivityList.get(i), resultActivity));
                                            weekActivityList.get(i).setTotalActivityDurationMinutes(resultActivity.getTotalActivityDurationMinutes());
                                            break;
                                        }
                                    } catch (Exception e) {
                                        AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
                                    }
                                }
                            } catch (Exception e) {
                                AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
                            }
                        }
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        //if error, we don't worry for now.
                    }
                });
            }
        }
    }

    @Override
    public void getDayDetailActivity(String url, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                activityNetwork.getDayDetailActivity(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        updateDayActivity((DayActivity) result, listener);
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        if (errorMessage instanceof ErrorMessage) {
                            listener.onError(errorMessage);
                        } else {
                            listener.onError(new ErrorMessage(errorMessage.toString()));
                        }
                    }
                });
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } catch (Exception e) {
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    @Override
    public void getWeeksActivity(boolean loadMore, boolean isBuddyFlow, Href href, DataLoadListener listener) {
        EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity();
        if (loadMore || embeddedYonaActivity == null
                || embeddedYonaActivity.getWeekActivityList() == null
                || embeddedYonaActivity.getWeekActivityList().size() == 0) {
            int pageNo = (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
                    && embeddedYonaActivity.getWeekActivityList() != null && embeddedYonaActivity.getWeekActivityList().size() > 0)
                    ? embeddedYonaActivity.getPage().getNumber() + 1 : 0;
            if (href != null && !TextUtils.isEmpty(href.getHref())) {
                getWeeksActivity(href.getHref(), isBuddyFlow, AppConstant.PAGE_SIZE, pageNo, listener);
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } else {
            listener.onDataLoad(YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().getWeekActivityList());
        }
    }

    @Override
    public void getWeeksDetailActivity(String url, final DataLoadListener listener) {
        activityNetwork.getWeeksDetailActivity(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                updateWeekActivity((WeekActivity) result, listener);
            }

            @Override
            public void onError(Object errorMessage) {
                if (errorMessage instanceof ErrorMessage) {
                    listener.onError(errorMessage);
                } else {
                    listener.onError(new ErrorMessage(errorMessage.toString()));
                }
            }
        });
    }

    public void postActivityToDB(String applicationName, Date startDate, Date endDate) {
        try {
            activityTrackerDAO.saveActivities(getAppActivity(applicationName, startDate, endDate));
        } catch (Exception e) {
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
        }
    }

    private void postActivityOnServer(final AppActivity activity, final boolean fromDB) {
        activityNetwork.postAppActivity(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getYonaAppActivity().getHref(),
                YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), activity, new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        //on success nothing to do, as it is posted on server.
                        if (fromDB) {
                            activityTrackerDAO.clearActivities();
                        }
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        //on failure, we need to store data in database to resend next time.
                        if (!fromDB) {
                            activityTrackerDAO.saveActivities(activity.getActivities());
                        }
                    }
                });
    }

    public void postAllDBActivities() {
        List<Activity> activityList = activityTrackerDAO.getActivities();
        if (activityList != null && activityList.size() > 0) {
            AppActivity appActivity = new AppActivity();
            appActivity.setDeviceDateTime(DateUtility.getLongFormatDate(new Date()));
            appActivity.setActivities(activityList);
            postActivityOnServer(appActivity, true);
        }

    }

    public void getWithBuddyActivity(boolean loadMore, final DataLoadListener listener) {
        EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity();
        if (loadMore || embeddedYonaActivity == null
                || embeddedYonaActivity.getDayActivityList() == null
                || embeddedYonaActivity.getDayActivityList().size() == 0) {
            int pageNo = (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
                    && embeddedYonaActivity.getDayActivityList() != null && embeddedYonaActivity.getDayActivityList().size() > 0) ? embeddedYonaActivity.getPage().getNumber() + 1 : 0;
            User user = YonaApplication.getEventChangeManager().getDataState().getUser();
            if (user != null && user.getLinks() != null
                    && user.getLinks().getYonaDailyActivityReports() != null
                    && !TextUtils.isEmpty(user.getLinks().getYonaDailyActivityReports().getHref())) {
                getWithBuddyActivity(user.getLinks().getDailyActivityReportsWithBuddies().getHref(), AppConstant.PAGE_SIZE, pageNo, listener);
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } else {
            listener.onDataLoad(YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList());
        }
    }

    public void getComments(List<DayActivity> dayActivityList, int position, final DataLoadListener listener) {
        int pageNo = 0;
        DayActivity dayActivity = dayActivityList.get(position);
        if (dayActivity != null) {
            if (dayActivity.getComments() != null && dayActivity.getComments().getPage() != null) {
                if (dayActivity.getComments().getPage().getNumber() + 1 == dayActivity.getComments().getPage().getTotalPages()) {
                    listener.onDataLoad(dayActivityList);
                } else {
                    pageNo = dayActivity.getComments().getPage().getNumber() + 1;
                    getCommentsFromServer(dayActivityList, dayActivity, pageNo, listener);
                }
            } else {
                getCommentsFromServer(dayActivityList, dayActivity, pageNo, listener);
            }
        }
    }

    public void getCommentsForWeek(List<WeekActivity> weekActivityList, int position, final DataLoadListener listener) {
        int pageNo = 0;
        WeekActivity weekActivity = weekActivityList.get(position);
        if (weekActivity != null) {
            if (weekActivity.getComments() != null && weekActivity.getComments().getPage() != null) {
                if (weekActivity.getComments().getPage().getNumber() + 1 == weekActivity.getComments().getPage().getTotalPages()) {
                    listener.onDataLoad(weekActivityList);
                } else if (weekActivity.getComments().getEmbedded() != null) {
                    pageNo = weekActivity.getComments().getPage().getNumber() + 1;
                    getCommentsFromServerForWeek(weekActivityList, weekActivity, pageNo, listener);
                } else {
                    getCommentsFromServerForWeek(weekActivityList, weekActivity, pageNo, listener);
                }
            } else {
                getCommentsFromServerForWeek(weekActivityList, weekActivity, pageNo, listener);
            }
        }
    }

    @Override
    public void addComment(DayActivity dayActivity, String comment, DataLoadListener listener) {
        Message message = new Message();
        message.setMessage(comment);
        if (dayActivity != null && dayActivity.getLinks() != null) {
            if (dayActivity.getLinks().getAddComment() != null) {
                doAddComment(dayActivity.getLinks().getAddComment().getHref(), message, listener);
            } else if (dayActivity.getLinks().getReplyComment() != null) {
                Properties properties = new Properties();
                properties.setMessage(message);
                reply(dayActivity.getLinks().getReplyComment().getHref(), properties, listener);
            }
        }
    }

    @Override
    public void addComment(WeekActivity weekActivity, String comment, DataLoadListener listener) {
        Message message = new Message();
        message.setMessage(comment);
        if (weekActivity != null && weekActivity.getLinks() != null) {
            if (weekActivity.getLinks().getAddComment() != null) {
                doAddComment(weekActivity.getLinks().getAddComment().getHref(), message, listener);
            } else if (weekActivity.getLinks().getReplyComment() != null) {
                Properties properties = new Properties();
                properties.setMessage(message);
                reply(weekActivity.getLinks().getReplyComment().getHref(), properties, listener);
            }
        }
    }

    private void reply(String url, Properties properties, final DataLoadListener listener) {
        activityNetwork.replyComment(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), properties, new DataLoadListener() {

            @Override
            public void onDataLoad(Object result) {
                if (result instanceof YonaMessage) {
                    listener.onDataLoad(result);
                }
            }

            @Override
            public void onError(Object errorMessage) {
                if (errorMessage instanceof ErrorMessage) {
                    listener.onError(errorMessage);
                } else {
                    listener.onError(new ErrorMessage(errorMessage.toString()));
                }
            }
        });
    }

    private void doAddComment(String url, Message message, final DataLoadListener listener) {
        activityNetwork.addComment(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), message, new DataLoadListener() {

            @Override
            public void onDataLoad(Object result) {
                if (result instanceof YonaMessage) {
                    listener.onDataLoad(result);
                }
            }

            @Override
            public void onError(Object errorMessage) {
                if (errorMessage instanceof ErrorMessage) {
                    listener.onError(errorMessage);
                } else {
                    listener.onError(new ErrorMessage(errorMessage.toString()));
                }
            }
        });
    }

    private void getCommentsFromServerForWeek(final List<WeekActivity> weekActivityList, final WeekActivity weekActivity, int pageNo, final DataLoadListener listener) {
        if (weekActivity.getLinks() != null && weekActivity.getLinks().getYonaMessages() != null && !TextUtils.isEmpty(weekActivity.getLinks().getYonaMessages().getHref())) {
            activityNetwork.getComments(weekActivity.getLinks().getYonaMessages().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), pageNo, AppConstant.PAGE_SIZE, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result instanceof EmbeddedYonaActivity) {
                        EmbeddedYonaActivity embeddedYonaActivity = (EmbeddedYonaActivity) result;
                        if (weekActivity.getComments() == null) {
                            weekActivity.setComments(embeddedYonaActivity);
                        } else {
                            if (embeddedYonaActivity.getEmbedded() != null && embeddedYonaActivity.getEmbedded().getYonaMessages() != null) {
                                weekActivity.getComments().getEmbedded().getYonaMessages().addAll(embeddedYonaActivity.getEmbedded().getYonaMessages());
                                weekActivity.getComments().setPage(embeddedYonaActivity.getEmbedded().getPage());
                            }
                        }
                        updateWeekActivityList(weekActivityList, weekActivity, listener);
                    } else {
                        listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.no_data_found)));
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    if (errorMessage instanceof ErrorMessage) {
                        listener.onError(errorMessage);
                    } else {
                        listener.onError(new ErrorMessage(errorMessage.toString()));
                    }
                }
            });
        }
    }

    private void getCommentsFromServer(final List<DayActivity> dayActivityList, final DayActivity dayActivity, int pageNo, final DataLoadListener listener) {
        if (dayActivity.getLinks() != null && dayActivity.getLinks().getYonaMessages() != null && !TextUtils.isEmpty(dayActivity.getLinks().getYonaMessages().getHref())) {
            activityNetwork.getComments(dayActivity.getLinks().getYonaMessages().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), pageNo, AppConstant.PAGE_SIZE, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result instanceof EmbeddedYonaActivity) {
                        EmbeddedYonaActivity embeddedYonaActivity = (EmbeddedYonaActivity) result;
                        if (dayActivity.getComments() == null) {
                            dayActivity.setComments(embeddedYonaActivity);
                        } else {
                            if (embeddedYonaActivity.getEmbedded() != null && embeddedYonaActivity.getEmbedded().getYonaMessages() != null) {
                                dayActivity.getComments().getEmbedded().getYonaMessages().addAll(embeddedYonaActivity.getEmbedded().getYonaMessages());
                                dayActivity.getComments().setPage(embeddedYonaActivity.getEmbedded().getPage());
                            }
                        }
                        updateDayActivityList(dayActivityList, dayActivity, listener);
                    } else {
                        listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.no_data_found)));
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    if (errorMessage instanceof ErrorMessage) {
                        listener.onError(errorMessage);
                    } else {
                        listener.onError(new ErrorMessage(errorMessage.toString()));
                    }
                }
            });
        }
    }


    private void updateWeekActivityList(List<WeekActivity> weekActivityList, WeekActivity weekActivity, DataLoadListener listener) {
        for (int i = 0; i < weekActivityList.size(); i++) {
            try {
                if (weekActivityList.get(i).getLinks().getSelf().getHref().equals(weekActivity.getLinks().getSelf().getHref())) {
                    weekActivityList.set(i, weekActivity);
                    listener.onDataLoad(weekActivityList);
                    break;
                }
            } catch (Exception e) {
                AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
            }
        }
    }

    private void updateDayActivityList(List<DayActivity> dayActivityList, DayActivity dayActivity, DataLoadListener listener) {
        for (int i = 0; i < dayActivityList.size(); i++) {
            try {
                if (dayActivityList.get(i).getLinks().getSelf().getHref().equals(dayActivity.getLinks().getSelf().getHref())) {
                    dayActivityList.set(i, dayActivity);
                    listener.onDataLoad(dayActivityList);
                    break;
                }
            } catch (Exception e) {
                AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
            }
        }
    }

    private Activity getAppActivity(String applicationName, Date startDate, Date endDate) {
        Activity activity = new Activity();
        activity.setApplication(applicationName);
        activity.setStartTime(DateUtility.getLongFormatDate(startDate));
        activity.setEndTime(DateUtility.getLongFormatDate(endDate));
        return activity;
    }

    private void getWeeksActivity(String url, final boolean isbuddyFlow, int itemsPerPage, int pageNo, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                activityNetwork.getWeeksActivity(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), itemsPerPage, pageNo, new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        filterAndUpdateWeekData((EmbeddedYonaActivity) result, isbuddyFlow, listener);
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        if (errorMessage instanceof ErrorMessage) {
                            listener.onError(errorMessage);
                        } else {
                            listener.onError(new ErrorMessage(errorMessage.toString()));
                        }
                    }
                });
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } catch (Exception e) {
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    private void getDailyActivity(String url, final boolean isbuddyFlow, int itemsPerPage, int pageNo, final DataLoadListener listener) {
        try {
            activityNetwork.getDaysActivity(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), itemsPerPage, pageNo, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result instanceof EmbeddedYonaActivity) {
                        filterAndUpdateDailyData((EmbeddedYonaActivity) result, isbuddyFlow, listener);
                    } else {
                        listener.onError(new ErrorMessage(mContext.getString(R.string.dataparseerror)));
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    if (errorMessage instanceof ErrorMessage) {
                        listener.onError(errorMessage);

                    } else {
                        listener.onError(new ErrorMessage(errorMessage.toString()));
                    }
                }
            });
        } catch (Exception e) {
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    private void filterAndUpdateWeekData(EmbeddedYonaActivity embeddedYonaActivity, boolean isbuddyFlow, DataLoadListener listener) {
        List<WeekActivity> weekActivities = new ArrayList<>();
        if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity() == null) {
            YonaApplication.getEventChangeManager().getDataState().setEmbeddedWeekActivity(embeddedYonaActivity);
        }
        if (embeddedYonaActivity != null) {
            if (embeddedYonaActivity.getEmbedded() != null) {
                Embedded embedded = embeddedYonaActivity.getEmbedded();
                List<YonaWeekActivityOverview> yonaDayActivityOverviews = embedded.getYonaWeekActivityOverviews();
                List<WeekActivity> thisWeekActivities;
                for (YonaWeekActivityOverview overview : yonaDayActivityOverviews) {
                    thisWeekActivities = new ArrayList<>();
                    List<WeekActivity> overviewWeekActivities = overview.getWeekActivities();
                    for (WeekActivity activity : overviewWeekActivities) {
                        YonaGoal goal = getYonaGoal(isbuddyFlow, activity.getLinks().getYonaGoal());
                        if (goal != null) {
                            activity.setYonaGoal(goal);
                            if (activity.getYonaGoal() != null) {
                                activity.setChartTypeEnum(ChartTypeEnum.WEEK_SCORE_CONTROL);
                            }
                            try {
                                activity.setStickyTitle(DateUtility.getRetriveWeek(overview.getDate()));
                            } catch (Exception e) {
                                AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
                            }
                            activity.setDate(overview.getDate());
                            activity = getWeekDayActivity(activity);
                            thisWeekActivities.add(activity);
                        }

                    }
                    weekActivities.addAll(sortWeekActivity(thisWeekActivities));
                }
                if (embeddedYonaActivity.getWeekActivityList() == null) {
                    embeddedYonaActivity.setWeekActivityList(weekActivities);
                } else {
                    YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().getWeekActivityList().addAll(weekActivities);
                }
            }
            if (embeddedYonaActivity.getPage() != null) {
                YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().setPage(embeddedYonaActivity.getPage());
            }
            getDetailOfEachWeekSpread();
            listener.onDataLoad(embeddedYonaActivity);
        }
    }


    private void updateWeekActivity(WeekActivity weekActivity, DataLoadListener listener) {
        YonaGoal currentYonaGoal = findYonaGoal(weekActivity.getLinks().getYonaGoal()) != null ? findYonaGoal(weekActivity.getLinks().getYonaGoal()) : findYonaBuddyGoal(weekActivity.getLinks().getYonaGoal());
        if (currentYonaGoal != null) {
            weekActivity.setYonaGoal(currentYonaGoal);
            if (weekActivity.getYonaGoal() != null) {
                weekActivity.setChartTypeEnum(ChartTypeEnum.WEEK_SCORE_CONTROL);
            }
            try {
                weekActivity.setStickyTitle(DateUtility.getRetriveWeek(weekActivity.getDate()));
            } catch (Exception e) {
                AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
            }
            weekActivity.setDate(weekActivity.getDate());
            weekActivity = getWeekDayActivity(weekActivity);
        }
        WeekActivity resultActivity = generateTimeZoneSpread(weekActivity);
        try {
            if (weekActivity.getLinks().getWeekDetails().getHref().equals(resultActivity.getLinks().getSelf().getHref())) {
                weekActivity.setTimeZoneSpread(resultActivity.getTimeZoneSpread());
                weekActivity.setTotalActivityDurationMinutes(resultActivity.getTotalActivityDurationMinutes());
            }
        } catch (Exception e) {
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
        }
        listener.onDataLoad(weekActivity);
    }

    private WeekActivity getWeekDayActivity(WeekActivity activity) {
        List<WeekDayActivity> mWeekDayActivityList = new ArrayList<>();
        Iterator calDates = DateUtility.getWeekDay(activity.getDate()).entrySet().iterator();
        int i = 0;
        boolean isCurrentDateReached = false;
        int mAccomplishedGoalCount = 0;
        WeekDayEnum weekDayEnum = null;
        int color = GraphUtils.COLOR_WHITE_THREE;
        while (calDates.hasNext()) {
            Map.Entry pair = (Map.Entry) calDates.next();
            Calendar calendar = Calendar.getInstance();
            DayActivities dayActivity = activity.getDayActivities();

            WeekDayActivity weekDayActivity = new WeekDayActivity();
            switch (i) {
                case 0:
                    weekDayEnum = WeekDayEnum.SUNDAY;
                    color = getColor(dayActivity.getSUNDAY());
                    mAccomplishedGoalCount = mAccomplishedGoalCount + getGoalAccomplished(dayActivity.getSUNDAY());
                    break;
                case 1:
                    weekDayEnum = WeekDayEnum.MONDAY;
                    color = getColor(dayActivity.getMONDAY());
                    mAccomplishedGoalCount = mAccomplishedGoalCount + getGoalAccomplished(dayActivity.getMONDAY());
                    break;
                case 2:
                    weekDayEnum = WeekDayEnum.TUESDAY;
                    color = getColor(dayActivity.getTUESDAY());
                    mAccomplishedGoalCount = mAccomplishedGoalCount + getGoalAccomplished(dayActivity.getTUESDAY());
                    break;
                case 3:
                    weekDayEnum = WeekDayEnum.WEDNESDAY;
                    color = getColor(dayActivity.getWEDNESDAY());
                    mAccomplishedGoalCount = mAccomplishedGoalCount + getGoalAccomplished(dayActivity.getWEDNESDAY());
                    break;
                case 4:
                    weekDayEnum = WeekDayEnum.THURSDAY;
                    color = getColor(dayActivity.getTHURSDAY());
                    mAccomplishedGoalCount = mAccomplishedGoalCount + getGoalAccomplished(dayActivity.getTHURSDAY());
                    break;
                case 5:
                    weekDayEnum = WeekDayEnum.FRIDAY;
                    color = getColor(dayActivity.getFRIDAY());
                    mAccomplishedGoalCount = mAccomplishedGoalCount + getGoalAccomplished(dayActivity.getFRIDAY());
                    break;
                case 6:
                    weekDayEnum = WeekDayEnum.SATURDAY;
                    color = getColor(dayActivity.getSATURDAY());
                    mAccomplishedGoalCount = mAccomplishedGoalCount + getGoalAccomplished(dayActivity.getSATURDAY());
                    break;
                default:
                    break;

            }
            if (activity.getLinks() != null && activity.getLinks().getYonaDayDetails() != null && !TextUtils.isEmpty(activity.getLinks().getYonaDayDetails().getHref())) {
                weekDayActivity.setUrl(activity.getLinks().getYonaDayDetails().getHref());
            }
            weekDayActivity.setWeekDayEnum(weekDayEnum);
            weekDayActivity.setColor(color);
            weekDayActivity.setDay(pair.getKey().toString());
            weekDayActivity.setDate(pair.getValue().toString());
            if (!isCurrentDateReached) {
                isCurrentDateReached = DateUtility.DAY_NO_FORMAT.format(calendar.getTime()).equals(pair.getValue().toString());
            }
            i++;
            mWeekDayActivityList.add(weekDayActivity);
        }
        activity.setWeekDayActivity(mWeekDayActivityList);
        activity.setTotalAccomplishedGoal(mAccomplishedGoalCount);
        return activity;
    }


    /**
     * Get the Accomplished number on base on day he has achieved or not
     *
     * @param day
     * @return
     */
    private int getGoalAccomplished(Day day) {
        return (day != null && day.getGoalAccomplished()) ? 1 : 0;
    }

    /**
     * Get the color of week Circle ,
     * if goal has achieved then its Green,
     * if goal not achieved then its Pink,
     * else if its future date or not added that goal before date of created then its Grey
     *
     * @param day
     * @return
     */
    private int getColor(Day day) {
        if (day != null) {
            if (day.getGoalAccomplished()) {
                return GraphUtils.COLOR_GREEN;
            } else {
                return GraphUtils.COLOR_PINK;
            }
        }
        return GraphUtils.COLOR_WHITE_THREE;
    }


    private void filterAndUpdateDailyData(EmbeddedYonaActivity embeddedYonaActivity, boolean isBuddyFlow, DataLoadListener listener) {
        List<DayActivity> dayActivities = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.YONA_DATE_FORMAT, Locale.getDefault());
        if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity() == null) {
            YonaApplication.getEventChangeManager().getDataState().setEmbeddedDayActivity(embeddedYonaActivity);
        }
        if (embeddedYonaActivity != null) {
            if (embeddedYonaActivity.getEmbedded() != null) {
                Embedded embedded = embeddedYonaActivity.getEmbedded();
                List<YonaDayActivityOverview> yonaDayActivityOverviews = embedded.getYonaDayActivityOverviews();
                for (YonaDayActivityOverview overview : yonaDayActivityOverviews) {
                    List<DayActivity> overviewDayActivities = overview.getDayActivities();
                    List<DayActivity> updatedOverviewDayActivities = new ArrayList<>();
                    for (DayActivity activity : overviewDayActivities) {
                        activity.setYonaGoal(getYonaGoal(isBuddyFlow, activity.getLinks().getYonaGoal()));
                        if (activity.getYonaGoal() != null) {
                            if (GoalsEnum.fromName(activity.getYonaGoal().getType()) == GoalsEnum.BUDGET_GOAL) {
                                if (activity.getYonaGoal().getMaxDurationMinutes() == 0) {
                                    activity.setChartTypeEnum(ChartTypeEnum.NOGO_CONTROL);
                                } else {
                                    activity.setChartTypeEnum(ChartTypeEnum.TIME_BUCKET_CONTROL);
                                }
                            } else if (GoalsEnum.fromName(activity.getYonaGoal().getType()) == GoalsEnum.TIME_ZONE_GOAL) {
                                activity.setChartTypeEnum(ChartTypeEnum.TIME_FRAME_CONTROL);
                            }
                        }
                        String createdTime = overview.getDate();
                        try {
                            Calendar futureCalendar = Calendar.getInstance();
                            futureCalendar.setTime(sdf.parse(createdTime));
                            activity.setStickyTitle(DateUtility.getRelativeDate(futureCalendar));
                        } catch (Exception e) {
                            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
                            Log.e(NotificationManagerImpl.class.getName(), "DateFormat " + e);
                        }
                        if (activity.getYonaGoal() != null && activity.getYonaGoal() != null && !activity.getYonaGoal().isHistoryItem()) {
                            updatedOverviewDayActivities.add(generateTimeZoneSpread(activity));
                        }
                    }
                    dayActivities.addAll(sortDayActivity(updatedOverviewDayActivities));
                }
                if (embeddedYonaActivity.getDayActivityList() == null) {
                    embeddedYonaActivity.setDayActivityList(dayActivities);
                } else {
                    YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity().getDayActivityList().addAll(dayActivities);
                }
            }
            if (embeddedYonaActivity.getPage() != null) {
                YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity().setPage(embeddedYonaActivity.getPage());
            }
            getDetailOfEachSpread();
            listener.onDataLoad(embeddedYonaActivity);
        } else {
            listener.onError(new ErrorMessage(mContext.getString(R.string.no_data_found)));
        }
    }


    private List<DayActivity> sortDayActivity(List<DayActivity> overviewDayActiivties) {
        Collections.sort(overviewDayActiivties, new Comparator<DayActivity>() {
            public int compare(DayActivity o1, DayActivity o2) {
                if (!TextUtils.isEmpty(o1.getYonaGoal().getActivityCategoryName()) && !TextUtils.isEmpty(o2.getYonaGoal().getActivityCategoryName())) {
                    return o1.getYonaGoal().getActivityCategoryName().compareTo(o2.getYonaGoal().getActivityCategoryName());
                }
                return 0;
            }
        });
        return overviewDayActiivties;
    }

    private List<WeekActivity> sortWeekActivity(List<WeekActivity> overviewDayActiivties) {
        Collections.sort(overviewDayActiivties, new Comparator<WeekActivity>() {
            public int compare(WeekActivity o1, WeekActivity o2) {
                if (!TextUtils.isEmpty(o1.getYonaGoal().getActivityCategoryName()) && !TextUtils.isEmpty(o2.getYonaGoal().getActivityCategoryName())) {
                    return o1.getYonaGoal().getActivityCategoryName().compareTo(o2.getYonaGoal().getActivityCategoryName());
                }
                return 0;
            }
        });
        return overviewDayActiivties;
    }

    private YonaGoal getYonaGoal(boolean isBuddyFlow, Href url) {
        if (!isBuddyFlow) {
            return findYonaGoal(url);
        } else {
            return findYonaBuddyGoal(url);
        }
    }

    private YonaGoal findYonaGoal(Href goalHref) {
        if (YonaApplication.getEventChangeManager().getDataState().getUser() != null && YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded() != null
                && YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals() != null
                && YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals().getEmbedded() != null
                && YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals().getEmbedded().getYonaGoals() != null) {
            List<YonaGoal> yonaGoals = YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals().getEmbedded().getYonaGoals();
            for (YonaGoal goal : yonaGoals) {
                if (goal.getLinks().getSelf().getHref().equals(goalHref.getHref())) {
                    goal.setActivityCategoryName(getActivityCategory(goal));
                    goal.setNickName(YonaApplication.getEventChangeManager().getDataState().getUser().getNickname());
                    return goal;
                }
            }
        }
        return null;
    }


    public YonaBuddy findYonaBuddy(Href yonaBuddy) {
        User user = YonaApplication.getEventChangeManager().getDataState().getUser();
        if (user != null && user.getEmbedded() != null
                && user.getEmbedded().getYonaBuddies() != null
                && user.getEmbedded().getYonaBuddies().getEmbedded() != null
                && user.getEmbedded().getYonaBuddies().getEmbedded().getYonaBuddies() != null) {
            List<YonaBuddy> yonaBuddies = user.getEmbedded().getYonaBuddies().getEmbedded().getYonaBuddies();
            for (YonaBuddy buddy : yonaBuddies) {
                if (buddy != null && buddy.getLinks() != null && buddy.getLinks().getSelf() != null && buddy.getLinks().getSelf().getHref().equals(yonaBuddy.getHref())) {
                    return buddy;
                }
            }
        }
        return null;
    }

    private YonaGoal findYonaBuddyGoal(Href goalHref) {
        User user = YonaApplication.getEventChangeManager().getDataState().getUser();
        if (user != null && user.getEmbedded() != null
                && user.getEmbedded().getYonaBuddies() != null
                && user.getEmbedded().getYonaBuddies().getEmbedded() != null
                && user.getEmbedded().getYonaBuddies().getEmbedded().getYonaBuddies() != null) {
            List<YonaBuddy> yonaBuddies = user.getEmbedded().getYonaBuddies().getEmbedded().getYonaBuddies();
            for (YonaBuddy buddy : yonaBuddies) {
                if (buddy != null && buddy.getEmbedded() != null && buddy.getEmbedded().getYonaGoals() != null
                        && buddy.getEmbedded().getYonaGoals().getEmbedded() != null
                        && buddy.getEmbedded().getYonaGoals().getEmbedded().getYonaGoals() != null) {
                    List<YonaGoal> yonaGoals = buddy.getEmbedded().getYonaGoals().getEmbedded().getYonaGoals();
                    for (YonaGoal goal : yonaGoals) {
                        if (goal.getLinks().getSelf().getHref().equals(goalHref.getHref())) {
                            goal.setActivityCategoryName(getActivityCategory(goal));
                            goal.setNickName(buddy.getNickname());
                            return goal;
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getActivityCategory(YonaGoal goal) {
        ActivityCategories categories = APIManager.getInstance().getActivityCategoryManager().getListOfActivityCategories();
        if (categories != null) {
            List<YonaActivityCategories> categoriesList = categories.getEmbeddedActivityCategories().getYonaActivityCategories();
            for (YonaActivityCategories yonaActivityCategories : categoriesList) {
                if (yonaActivityCategories.get_links().getSelf().getHref().equals(goal.getLinks().getYonaActivityCategory().getHref())) {
                    return yonaActivityCategories.getName();
                }
            }
        }
        return null;
    }

    private DayActivity generateTimeZoneSpread(DayActivity activity) {

        if (activity.getSpread() != null) {
            List<Integer> spreadsList = activity.getSpread();
            List<Integer> spreadCellsList;
            boolean isBudgetGoal = false;
            if (activity.getYonaGoal() != null && activity.getYonaGoal().getSpreadCells() != null) {
                isBudgetGoal = activity.getYonaGoal().getType().equals(GoalsEnum.BUDGET_GOAL.getActionString()) && activity.getYonaGoal().getMaxDurationMinutes() != 0;
                spreadCellsList = activity.getYonaGoal().getSpreadCells();
            } else {
                spreadCellsList = new ArrayList<>();
            }
            List<TimeZoneSpread> timeZoneSpreadList = new ArrayList<>();
            for (int i = 0; i < spreadsList.size(); i++) {
                setTimeZoneSpread(i, spreadsList.get(i), timeZoneSpreadList, spreadCellsList.contains(i) || isBudgetGoal);
            }
            activity.setTimeZoneSpread(timeZoneSpreadList);
        }
        return activity;
    }

    private void setTimeZoneSpread(int index, int spreadListValue, List<TimeZoneSpread> timeZoneSpreadList, boolean allowed) {
        TimeZoneSpread timeZoneSpread = new TimeZoneSpread();
        timeZoneSpread.setIndex(index);
        timeZoneSpread.setAllowed(allowed);
        if (spreadListValue > 0) {
            if (allowed) {
                timeZoneSpread.setColor(GraphUtils.COLOR_BLUE);
            } else {
                timeZoneSpread.setColor(GraphUtils.COLOR_PINK);
            }
            timeZoneSpread.setUsedValue(spreadListValue); // ex. set 10 min as blue used during allowed time.
            timeZoneSpreadList.add(timeZoneSpread);
            //Now create remaining time's other object:
            if (spreadListValue < maxSpreadTime) {
                TimeZoneSpread secondSpread = new TimeZoneSpread();
                secondSpread.setIndex(index);
                secondSpread.setAllowed(allowed);
                timeZoneSpreadList.add(addToArray(allowed, secondSpread, maxSpreadTime - spreadListValue));
            }
        } else {
            timeZoneSpreadList.add(addToArray(allowed, timeZoneSpread, maxSpreadTime - spreadListValue));
        }
    }

    private TimeZoneSpread addToArray(boolean allowed, TimeZoneSpread timeZoneSpread, int usage) {
        if (allowed) {
            timeZoneSpread.setColor(GraphUtils.COLOR_GREEN);
        } else {
            timeZoneSpread.setColor(GraphUtils.COLOR_WHITE_THREE);
        }
        timeZoneSpread.setUsedValue(usage); // out of 15 mins, if 10 min used, so here we need to show 5 min as green
        return timeZoneSpread;
    }

    private WeekActivity generateTimeZoneSpread(WeekActivity activity) {

        if (activity.getSpread() != null) {
            List<Integer> spreadsList = activity.getSpread();
            List<Integer> spreadCellsList;
            if (activity.getYonaGoal() != null && activity.getYonaGoal().getSpreadCells() != null) {
                spreadCellsList = activity.getYonaGoal().getSpreadCells();
            } else {
                spreadCellsList = new ArrayList<>();
            }
            List<TimeZoneSpread> timeZoneSpreadList = new ArrayList<>();
            for (int i = 0; i < spreadsList.size(); i++) {
                setTimeZoneSpread(i, spreadsList.get(i), timeZoneSpreadList, spreadCellsList.contains(i));
            }
            activity.setTimeZoneSpread(timeZoneSpreadList);
        }
        return activity;
    }

    private void getWithBuddyActivity(String url, int itemsPerPage, int pageNo, final DataLoadListener listener) {
        try {
            activityNetwork.getWithBuddyActivity(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), itemsPerPage, pageNo, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result instanceof EmbeddedYonaActivity) {
                        filterAndUpdateWithBuddyData((EmbeddedYonaActivity) result, listener);
                    } else {
                        listener.onError(new ErrorMessage(mContext.getString(R.string.dataparseerror)));
                    }
                }

                @Override
                public void onError(Object errorMessage) {
                    if (errorMessage instanceof ErrorMessage) {
                        listener.onError(errorMessage);

                    } else {
                        listener.onError(new ErrorMessage(errorMessage.toString()));
                    }
                }
            });
        } catch (Exception e) {
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }

    //TOD0 modify this method
    private void filterAndUpdateWithBuddyData(EmbeddedYonaActivity embeddedYonaActivity, DataLoadListener listener) {
        List<DayActivity> dayActivities = new ArrayList<>();

        if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity() == null) {
            YonaApplication.getEventChangeManager().getDataState().setEmbeddedWithBuddyActivity(embeddedYonaActivity);
        }
        if (embeddedYonaActivity != null) {
            if (embeddedYonaActivity.getEmbedded() != null) {
                Embedded embedded = embeddedYonaActivity.getEmbedded();
                List<YonaDayActivityOverview> yonaDayActivityOverviews = embedded.getYonaDayActivityOverviews();
                for (YonaDayActivityOverview overview : yonaDayActivityOverviews) {
                    List<DayActivity> overviewDayActivities = overview.getDayActivities();
                    List<DayActivity> updatedOverviewDayActivities = new ArrayList<>();
                    for (DayActivity activities : overviewDayActivities) {
                        if (activities != null && activities.getDayActivitiesForUsers() != null) {
                            for (DayActivity activity : activities.getDayActivitiesForUsers()) {
                                activity.setYonaGoal(getYonaGoal(activity.getLinks().getYonaUser() != null ? false : true, activity.getLinks().getYonaGoal()));
                                if (activity.getYonaGoal() != null) {
                                    if (GoalsEnum.fromName(activity.getYonaGoal().getType()) == GoalsEnum.BUDGET_GOAL) {
                                        if (activity.getYonaGoal().getMaxDurationMinutes() == 0) {
                                            activity.setChartTypeEnum(ChartTypeEnum.NOGO_CONTROL);
                                        } else {
                                            activity.setChartTypeEnum(ChartTypeEnum.TIME_BUCKET_CONTROL);
                                        }
                                    } else if (GoalsEnum.fromName(activity.getYonaGoal().getType()) == GoalsEnum.TIME_ZONE_GOAL) {
                                        activity.setChartTypeEnum(ChartTypeEnum.TIME_FRAME_CONTROL);
                                    }
                                }
                                String createdTime = overview.getDate();
                                try {
                                    Calendar futureCalendar = Calendar.getInstance();
                                    futureCalendar.setTime(sdf.parse(createdTime));
                                    activity.setStickyTitle(DateUtility.getRelativeDate(futureCalendar));
                                } catch (Exception e) {
                                    AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
                                }
                                if (activity.getYonaGoal() != null && activity.getYonaGoal() != null && !activity.getYonaGoal().isHistoryItem()) {
                                    updatedOverviewDayActivities.add(generateTimeZoneSpread(activity));
                                }
                            }
                        }
                    }
                    dayActivities.addAll(sortDayActivity(updatedOverviewDayActivities));
                }
                if (embeddedYonaActivity.getDayActivityList() == null) {
                    embeddedYonaActivity.setDayActivityList(dayActivities);
                } else {
                    YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList().addAll(dayActivities);
                }
            }
            if (embeddedYonaActivity.getPage() != null) {
                YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().setPage(embeddedYonaActivity.getPage());
            }
            getBuddyDetailOfEachSpread();
            listener.onDataLoad(embeddedYonaActivity);
        } else {
            listener.onError(new ErrorMessage(mContext.getString(R.string.no_data_found)));
        }
    }

    private void updateDayActivity(DayActivity activity, DataLoadListener listener) {
        YonaGoal currentYonaGoal = findYonaGoal(activity.getLinks().getYonaGoal()) != null ? findYonaGoal(activity.getLinks().getYonaGoal()) : findYonaBuddyGoal(activity.getLinks().getYonaGoal());
        activity.setYonaGoal(currentYonaGoal);
        if (activity.getYonaGoal() != null) {
            if (GoalsEnum.fromName(activity.getYonaGoal().getType()) == GoalsEnum.BUDGET_GOAL) {
                if (activity.getYonaGoal().getMaxDurationMinutes() == 0) {
                    activity.setChartTypeEnum(ChartTypeEnum.NOGO_CONTROL);
                } else {
                    activity.setChartTypeEnum(ChartTypeEnum.TIME_BUCKET_CONTROL);
                }
            } else if (GoalsEnum.fromName(activity.getYonaGoal().getType()) == GoalsEnum.TIME_ZONE_GOAL) {
                activity.setChartTypeEnum(ChartTypeEnum.TIME_FRAME_CONTROL);
            }
        }
        String createdTime = activity.getDate();
        try {
            Calendar futureCalendar = Calendar.getInstance();
            futureCalendar.setTime(sdf.parse(createdTime));
            activity.setStickyTitle(DateUtility.getRelativeDate(futureCalendar));
        } catch (Exception e) {
            Log.e(NotificationManagerImpl.class.getName(), "DateFormat " + e);
        }
        listener.onDataLoad(generateTimeZoneSpread(activity));
    }

    private void getBuddyDetailOfEachSpread() {
        final List<DayActivity> dayActivities = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList();
        for (final DayActivity dayActivity : dayActivities) {
            if (dayActivity.getTimeZoneSpread() == null || (dayActivity.getTimeZoneSpread() != null && dayActivity.getTimeZoneSpread().size() == 0)) {
                APIManager.getInstance().getActivityManager().getDayDetailActivity(dayActivity.getLinks().getYonaDayDetails().getHref(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        if (result instanceof DayActivity) {
                            try {
                                DayActivity resultActivity = generateTimeZoneSpread((DayActivity) result);
                                List<DayActivity> dayActivityList = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList();
                                for (int i = 0; i < dayActivityList.size(); i++) {
                                    try {
                                        if (dayActivityList.get(i).getLinks().getYonaDayDetails().getHref().equals(resultActivity.getLinks().getSelf().getHref())) {
                                            dayActivityList.get(i).setTimeZoneSpread(resultActivity.getTimeZoneSpread());
                                            dayActivityList.set(i, updateLinks(dayActivityList.get(i), resultActivity));
                                            break;
                                        }
                                    } catch (Exception e) {
                                        AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
                                    }
                                }
                            } catch (Exception e) {
                                AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), null);
                            }
                        }
                    }

                    @Override
                    public void onError(Object errorMessage) {
                        // we are not worry as of now here
                    }
                });
            }
        }
    }

    private DayActivity updateLinks(DayActivity actualActivity, DayActivity resultActivity) {
        if (resultActivity.getLinks() != null) {
            if (resultActivity.getLinks().getSelf() != null) {
                actualActivity.getLinks().setSelf(resultActivity.getLinks().getSelf());
            }
            if (resultActivity.getLinks().getEdit() != null) {
                actualActivity.getLinks().setEdit(resultActivity.getLinks().getEdit());
            }
            if (resultActivity.getLinks().getReplyComment() != null) {
                actualActivity.getLinks().setEdit(resultActivity.getLinks().getReplyComment());
            }
            if (resultActivity.getLinks().getYonaMessages() != null) {
                actualActivity.getLinks().setYonaMessages(resultActivity.getLinks().getYonaMessages());
            }
            if (resultActivity.getLinks().getYonaUser() != null) {
                actualActivity.getLinks().setYonaUser(resultActivity.getLinks().getYonaUser());
            }
            if (resultActivity.getLinks().getYonaDayDetails() != null) {
                actualActivity.getLinks().setYonaDayDetails(resultActivity.getLinks().getYonaDayDetails());
            }
            if (resultActivity.getLinks().getYonaBuddy() != null) {
                actualActivity.getLinks().setYonaBuddy(resultActivity.getLinks().getYonaBuddy());
            }
            if (resultActivity.getLinks().getAddComment() != null) {
                actualActivity.getLinks().setAddComment(resultActivity.getLinks().getAddComment());
            }
        }
        return actualActivity;
    }

    private WeekActivity updateLinks(WeekActivity actualActivity, WeekActivity resultActivity) {
        if (resultActivity.getLinks() != null) {
            if (resultActivity.getLinks().getSelf() != null) {
                actualActivity.getLinks().setSelf(resultActivity.getLinks().getSelf());
            }
            if (resultActivity.getLinks().getEdit() != null) {
                actualActivity.getLinks().setEdit(resultActivity.getLinks().getEdit());
            }
            if (resultActivity.getLinks().getReplyComment() != null) {
                actualActivity.getLinks().setEdit(resultActivity.getLinks().getReplyComment());
            }
            if (resultActivity.getLinks().getYonaMessages() != null) {
                actualActivity.getLinks().setYonaMessages(resultActivity.getLinks().getYonaMessages());
            }
            if (resultActivity.getLinks().getYonaUser() != null) {
                actualActivity.getLinks().setYonaUser(resultActivity.getLinks().getYonaUser());
            }
            if (resultActivity.getLinks().getYonaDayDetails() != null) {
                actualActivity.getLinks().setYonaDayDetails(resultActivity.getLinks().getYonaDayDetails());
            }
            if (resultActivity.getLinks().getYonaBuddy() != null) {
                actualActivity.getLinks().setYonaBuddy(resultActivity.getLinks().getYonaBuddy());
            }
            if (resultActivity.getLinks().getAddComment() != null) {
                actualActivity.getLinks().setAddComment(resultActivity.getLinks().getAddComment());
            }
        }
        return actualActivity;
    }
}

