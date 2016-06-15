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
import nu.yona.app.api.model.User;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaDayActivityOverview;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.api.model.YonaWeekActivityOverview;
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
    public void getDaysActivity(boolean loadMore, DataLoadListener listener) {
        EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
        if (loadMore || embeddedYonaActivity == null
                || embeddedYonaActivity.getDayActivityList() == null
                || embeddedYonaActivity.getDayActivityList().size() == 0) {
            int pageNo = (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
                    && embeddedYonaActivity.getDayActivityList() != null && embeddedYonaActivity.getDayActivityList().size() > 0) ? embeddedYonaActivity.getPage().getNumber() + 1 : 0;
            User user = YonaApplication.getEventChangeManager().getDataState().getUser();
            if (user != null && user.getLinks() != null
                    && user.getLinks().getYonaDailyActivityReports() != null
                    && !TextUtils.isEmpty(user.getLinks().getYonaDailyActivityReports().getHref())) {
                getDailyActivity(user.getLinks().getYonaDailyActivityReports().getHref(), AppConstant.PAGE_SIZE, pageNo, listener);
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
            if (dayActivity.getTimeZoneSpread() == null || (dayActivity.getTimeZoneSpread() != null && dayActivity.getTimeZoneSpread().size() == 0)) {
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
                                            break;
                                        }
                                    } catch (Exception e) {
                                        Log.e(ActivityManager.class.getSimpleName(), e.getMessage());
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(ActivityManager.class.getSimpleName(), e.getMessage());
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
    public void getWeeksActivity(boolean loadMore, DataLoadListener listener) {
        EmbeddedYonaActivity embeddedYonaActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity();
        if (loadMore || embeddedYonaActivity == null
                || embeddedYonaActivity.getWeekActivityList() == null
                || embeddedYonaActivity.getWeekActivityList().size() == 0) {
            int pageNo = (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
                    && embeddedYonaActivity.getWeekActivityList() != null && embeddedYonaActivity.getWeekActivityList().size() > 0)
                    ? embeddedYonaActivity.getPage().getNumber() + 1 : 0;
            User user = YonaApplication.getEventChangeManager().getDataState().getUser();
            if (user != null && user.getLinks() != null
                    && user.getLinks().getYonaWeeklyActivityReports() != null
                    && !TextUtils.isEmpty(user.getLinks().getYonaWeeklyActivityReports().getHref())) {
                getWeeksActivity(user.getLinks().getYonaWeeklyActivityReports().getHref(), AppConstant.PAGE_SIZE, pageNo, listener);
            } else {
                listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
            }
        } else {
            listener.onDataLoad(YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().getWeekActivityList());
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
        activityNetwork.postAppActivity(YonaApplication.getEventChangeManager().getDataState().getUser().getLinks().getYonaAppActivity().getHref(),
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
        if (activityList != null && activityList.size() > 0) {
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
                        filterAndUpdateWeekData((EmbeddedYonaActivity) result, listener);
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

    private void filterAndUpdateWeekData(EmbeddedYonaActivity embeddedYonaActivity, DataLoadListener listener) {
        List<WeekActivity> weekActivities = new ArrayList<>();
        if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity() == null) {
            YonaApplication.getEventChangeManager().getDataState().setEmbeddedWeekActivity(embeddedYonaActivity);
        }
        if (embeddedYonaActivity != null) {
            if (embeddedYonaActivity.getEmbedded() != null) {
                Embedded embedded = embeddedYonaActivity.getEmbedded();
                List<YonaWeekActivityOverview> yonaDayActivityOverviews = embedded.getYonaWeekActivityOverviews();
                for (YonaWeekActivityOverview overview : yonaDayActivityOverviews) {
                    List<WeekActivity> overviewWeekActivities = overview.getWeekActivities();
                    for (WeekActivity activity : overviewWeekActivities) {
                        YonaGoal goal = findYonaGoal(activity.getLinks().getYonaGoal());
                        if (goal != null) {
                            activity.setYonaGoal(goal);
                            if (activity.getYonaGoal() != null) {
                                activity.setChartTypeEnum(ChartTypeEnum.WEEK_SCORE_CONTROL);
                            }
                            try {
                                activity.setStickyTitle(DateUtility.getRetriveWeek(overview.getDate()));
                            } catch (Exception e) {
                                Log.e(NotificationManagerImpl.class.getName(), "DateFormat " + e);
                            }
                            weekActivities.add(activity);
                        }

                    }
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
            listener.onDataLoad(embeddedYonaActivity);
        }
    }

    private void filterAndUpdateDailyData(EmbeddedYonaActivity embeddedYonaActivity, DataLoadListener listener) {
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
                        activity.setYonaGoal(findYonaGoal(activity.getLinks().getYonaGoal()));
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

    private YonaGoal findYonaGoal(Href goalHref) {
        if (YonaApplication.getEventChangeManager().getDataState().getUser() != null && YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded() != null
                && YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals() != null
                && YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals().getEmbedded() != null
                && YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals().getEmbedded().getYonaGoals() != null) {
            List<YonaGoal> yonaGoals = YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals().getEmbedded().getYonaGoals();
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
