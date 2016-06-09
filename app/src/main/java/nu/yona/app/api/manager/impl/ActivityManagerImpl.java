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
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.Embedded;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.TimeZoneSpread;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaDayActivityOverview;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.customview.graph.GraphUtils;
import nu.yona.app.enums.ChartTypeEnum;
import nu.yona.app.enums.GoalsEnum;
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
    public void getDaysActivity(int itemsPerPage, int pageNo, DataLoadListener listener) {
        if (YonaApplication.getUser() != null && YonaApplication.getUser().getLinks() != null
                && YonaApplication.getUser().getLinks().getYonaDailyActivityReports() != null
                && !TextUtils.isEmpty(YonaApplication.getUser().getLinks().getYonaDailyActivityReports().getHref())) {
            getDailyActivity(YonaApplication.getUser().getLinks().getYonaDailyActivityReports().getHref(), itemsPerPage, pageNo, listener);
        } else {
            listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
        }
    }

    @Override
    public void getBuddyDaysActivity(String url, int itemsPerPage, int pageNo, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                getDailyActivity(url, itemsPerPage, pageNo, listener);
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } catch (Exception e) {
            AppUtils.throwException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
        }
    }


    @Override
    public void getDayDetailActivity(String url, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                activityNetwork.getDayDetailActivity(url, YonaApplication.getYonaPassword(), new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        listener.onDataLoad(result);
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
    public void getWeeksActivity(int itemsPerPage, int pageNo, DataLoadListener listener) {
        if (YonaApplication.getUser() != null && YonaApplication.getUser().getLinks() != null
                && YonaApplication.getUser().getLinks().getYonaWeeklyActivityReports() != null
                && !TextUtils.isEmpty(YonaApplication.getUser().getLinks().getYonaWeeklyActivityReports().getHref())) {
            getWeeksActivity(YonaApplication.getUser().getLinks().getYonaWeeklyActivityReports().getHref(), itemsPerPage, pageNo, listener);
        } else {
            listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
        }
    }

    @Override
    public void getBuddyWeeksActivity(String url, int itemsPerPage, int pageNo, DataLoadListener listener) {
        getWeeksActivity(url, itemsPerPage, pageNo, listener);
    }

    @Override
    public void getWeeksDetailActivity(String url, final DataLoadListener listener) {
        activityNetwork.getWeeksDetailActivity(url, YonaApplication.getYonaPassword(), new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {
                listener.onDataLoad(result);
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
        activityNetwork.postAppActivity(YonaApplication.getUser().getLinks().getYonaAppActivity().getHref(),
                YonaApplication.getYonaPassword(), activity, new DataLoadListener() {
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
        if (activityList != null) {
            AppActivity appActivity = new AppActivity();
            appActivity.setDeviceDateTime(DateUtility.getLongFormatDate(new Date()));
            appActivity.setActivities(activityList);
            postActivityOnServer(appActivity, true);
        }

    }

    private Activity getAppActivity(String applicationName, Date startDate, Date endDate) {
        Activity activity = new Activity();
        activity.setApplication(applicationName);
        activity.setStartTime(DateUtility.getLongFormatDate(startDate));
        activity.setEndTime(DateUtility.getLongFormatDate(endDate));
        return activity;
    }

    private void getWeeksActivity(String url, int itemsPerPage, int pageNo, final DataLoadListener listener) {
        try {
            if (!TextUtils.isEmpty(url)) {
                activityNetwork.getWeeksActivity(url, YonaApplication.getYonaPassword(), itemsPerPage, pageNo, new DataLoadListener() {
                    @Override
                    public void onDataLoad(Object result) {
                        listener.onDataLoad(result);
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

    private void getDailyActivity(String url, int itemsPerPage, int pageNo, final DataLoadListener listener) {
        try {
            activityNetwork.getDaysActivity(url, YonaApplication.getYonaPassword(), itemsPerPage, pageNo, new DataLoadListener() {
                @Override
                public void onDataLoad(Object result) {
                    if (result instanceof EmbeddedYonaActivity) {
                        filterAndUpdateDailyData((EmbeddedYonaActivity) result, listener);
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

    private void filterAndUpdateDailyData(EmbeddedYonaActivity embeddedYonaActivity, DataLoadListener listener) {
        List<DayActivity> dayActivities = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.YONA_DATE_FORMAT, Locale.getDefault());
        if (embeddedYonaActivity != null && embeddedYonaActivity.getEmbedded() != null) {
            Embedded embedded = embeddedYonaActivity.getEmbedded();
            List<YonaDayActivityOverview> yonaDayActivityOverviews = embedded.getYonaDayActivityOverviews();
            for (YonaDayActivityOverview overview : yonaDayActivityOverviews) {
                List<DayActivity> overviewDayActivities = overview.getDayActivities();
                for (DayActivity activity : overviewDayActivities) {
                    activity.setYonaGoal(findYonaGoal(activity.getLinks().getYonaGoal()));
                    if (activity.getYonaGoal() != null) {
                        if (GoalsEnum.fromName(activity.getYonaGoal().getType()) == GoalsEnum.BUDGET_GOAL) {
                            activity.setChartTypeEnum(ChartTypeEnum.TIME_BUCKET_CONTROL);
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
                        Log.e(NotificationManagerImpl.class.getName(), "DateFormat " + e);
                    }
                    dayActivities.add(generateTimeZoneSpread(activity));
                }
            }
            embeddedYonaActivity.setDayActivityList(dayActivities);
            listener.onDataLoad(embeddedYonaActivity);
        }
    }

    private YonaGoal findYonaGoal(Href goalHref) {
        if (YonaApplication.getUser() != null && YonaApplication.getUser().getEmbedded() != null
                && YonaApplication.getUser().getEmbedded().getYonaGoals() != null
                && YonaApplication.getUser().getEmbedded().getYonaGoals().getEmbedded() != null
                && YonaApplication.getUser().getEmbedded().getYonaGoals().getEmbedded().getYonaGoals() != null) {
            List<YonaGoal> yonaGoals = YonaApplication.getUser().getEmbedded().getYonaGoals().getEmbedded().getYonaGoals();
            for (YonaGoal goal : yonaGoals) {
                if (goal.getLinks().getSelf().getHref().equals(goalHref.getHref())) {
                    goal.setActivityCategoryName(getActivityCategory(goal));
                    return goal;
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

        if (activity.getSpread() != null && activity.getYonaGoal() != null && activity.getYonaGoal().getSpreadCells() != null) {
            List<Integer> spreadsList = activity.getSpread();
            List<Integer> spreadCellsList = activity.getYonaGoal().getSpreadCells();
            List<TimeZoneSpread> timeZoneSpreadList = new ArrayList<>();
            for (int i = 0; i < spreadsList.size(); i++) {
                setTimeZoneSpread(i, spreadsList.get(i), timeZoneSpreadList, spreadCellsList.contains(i));
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
}
