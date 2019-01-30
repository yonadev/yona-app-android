/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import android.content.Context;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import nu.yona.app.api.model.Links;
import nu.yona.app.api.model.Message;
import nu.yona.app.api.model.MessageBody;
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
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.DateUtility;
import nu.yona.app.utils.Logger;

import static nu.yona.app.YonaApplication.getSharedAppDataState;

/**
 * Created by kinnarvasa on 06/06/16.
 */
public class ActivityManagerImpl implements ActivityManager
{

	private final ActivityNetworkImpl activityNetwork;
	private final ActivityTrackerDAO activityTrackerDAO;
	private final Context mContext;
	private final SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.YONA_DATE_FORMAT, Locale.getDefault());

	/**
	 * Instantiates a new Activity manager.
	 *
	 * @param context the context
	 */
	public ActivityManagerImpl(Context context)
	{
		activityNetwork = new ActivityNetworkImpl();
		activityTrackerDAO = new ActivityTrackerDAO(DatabaseHelper.getInstance(context));
		mContext = context;
	}

	/**
	 * Day activity processing ***********
	 */

	@Override
	public void getDaysActivity(boolean loadMore, boolean isBuddyFlow, Href url, DataLoadListener listener)
	{
		EmbeddedYonaActivity embeddedYonaActivity = getSharedAppDataState().getEmbeddedDayActivity();
		if (loadMore || isValidEmbeddedDayActivityList(embeddedYonaActivity))
		{
			if (url != null && !TextUtils.isEmpty(url.getHref()))
			{
				getDailyActivity(url.getHref(), isBuddyFlow, listener);
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
			}
		}
		else
		{
			listener.onDataLoad(getSharedAppDataState().getEmbeddedDayActivity().getDayActivityList());
		}
	}

	@Override
	public void getDayDetailActivity(String url, DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(url))
			{
				DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> updateDayActivity((DayActivity) result, listener), (result) -> handleErrorMessage(result, listener), null);
				activityNetwork.getDayDetailActivity(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), dataLoadListenerImpl);
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	private Object handleErrorMessage(Object errorMessage, DataLoadListener listener)
	{
		if (errorMessage instanceof ErrorMessage)
		{
			listener.onError(errorMessage);
		}
		else
		{
			listener.onError(new ErrorMessage(errorMessage.toString()));
		}
		return null; // Dummy return value, to allow use as data error handler
	}

	@Override
	public void getDetailOfEachSpreadWithDayActivity(DayActivity dayActivity, DataLoadListener listener)
	{
		if (!isValidDayActivityTimeZoneSpread(dayActivity))
		{
			listener.onDataLoad(dayActivity);
		}
		else
		{
			DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl(((result) -> handleDayActivityDetailsFetchSuccess((DayActivity) result)), null, listener);
			APIManager.getInstance().getActivityManager().getDayDetailActivity(dayActivity.getLinks().getYonaDayDetails().getHref(), dataLoadListenerImpl);
		}
	}

	private boolean isValidDayActivityTimeZoneSpread(DayActivity dayActivity)
	{
		return (dayActivity.getTimeZoneSpread() == null
				|| (dayActivity.getTimeZoneSpread() != null && dayActivity.getTimeZoneSpread().size() == 0)
				|| dayActivity.getChartTypeEnum() == ChartTypeEnum.TIME_FRAME_CONTROL);
	}

	private boolean isEqualDayActivityDetailHrefs(DayActivity leftSideDayActivity, DayActivity rightSideDayActivity)
	{
		return leftSideDayActivity.getLinks().getYonaDayDetails().getHref().equals(rightSideDayActivity.getLinks().getSelf().getHref());
	}

	private Object handleDayActivityDetailsFetchSuccess(DayActivity dayActivity)
	{
		if (getSharedAppDataState().getEmbeddedDayActivity() == null)
		{
			return null; // Dummy return value to allow use as data load handler
		}
		updateDayActivity(dayActivity);
		return null; // Dummy return value to allow use as data load handler
	}


	private void updateDayActivity(DayActivity dayActivity)
	{
		DayActivity resultActivity = generateTimeZoneSpread(dayActivity);
		List<DayActivity> dayActivityList = getSharedAppDataState().getEmbeddedDayActivity().getDayActivityList();
		for (int i = 0; i < dayActivityList.size(); i++)
		{
			if (isEqualDayActivityDetailHrefs(dayActivityList.get(i), resultActivity))
			{
				dayActivityList.get(i).setTimeZoneSpread(resultActivity.getTimeZoneSpread());
				getSharedAppDataState().getEmbeddedDayActivity().getDayActivityList().set(i, updateLinks(dayActivityList.get(i), resultActivity));
				break;
			}
		}
	}

	/**
	 * Week activity processing ***********
	 */

	@Override
	public void getWeeksActivity(boolean loadMore, boolean isBuddyFlow, Href href, DataLoadListener listener)
	{
		EmbeddedYonaActivity embeddedYonaActivity = getSharedAppDataState().getEmbeddedWeekActivity();
		if (loadMore || isValidEmbeddedWeekActivityList(embeddedYonaActivity))
		{
			if (href != null && !TextUtils.isEmpty(href.getHref()))
			{
				getWeeksActivity(href.getHref(), isBuddyFlow, listener);
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
			}
		}
		else
		{
			listener.onDataLoad(getSharedAppDataState().getEmbeddedWeekActivity().getWeekActivityList());
		}
	}

	@Override
	public void getWeeksDetailActivity(String url, DataLoadListener listener)
	{
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> updateWeekActivity((WeekActivity) result, listener), (result) -> handleErrorMessage(result, listener), null);
		activityNetwork.getWeeksDetailActivity(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), dataLoadListenerImpl);
	}

	@Override
	public void getDetailOfEachWeekSpreadWithWeekActivity(WeekActivity weekActivity, DataLoadListener listener)
	{
		if (validateWeekActivityTimeZoneSpread(weekActivity))
		{
			DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl(((result) -> handleWeekActivityDetailsFetchSuccess((WeekActivity) result)), null, listener);
			APIManager.getInstance().getActivityManager().getWeeksDetailActivity(weekActivity.getLinks().getWeekDetails().getHref(), dataLoadListenerImpl);
		}
		else
		{
			listener.onDataLoad(weekActivity);
		}
	}

	private boolean validateWeekActivityTimeZoneSpread(WeekActivity weekActivity)
	{
		return (weekActivity.getTimeZoneSpread() == null
				|| (weekActivity.getTimeZoneSpread() != null && weekActivity.getTimeZoneSpread().size() == 0)
				|| weekActivity.getChartTypeEnum() == ChartTypeEnum.TIME_FRAME_CONTROL);
	}

	private boolean isEqualWeekActivitiesDayDetailsHrefs(WeekActivity leftSideWeekActivity, WeekActivity rightSideWeekActivity)
	{
		return (leftSideWeekActivity.getLinks().getWeekDetails().getHref().equals(rightSideWeekActivity.getLinks().getSelf().getHref()));
	}

	private Object handleWeekActivityDetailsFetchSuccess(WeekActivity weekActivity)
	{
		try
		{
			if (getSharedAppDataState().getEmbeddedWeekActivity() == null)
			{
				return null; // Dummy return value, to allow use as data load handler
			}
			updateWeekActivityListTimeZoneSpread(weekActivity);
		}
		catch (NullPointerException e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread());
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private void updateWeekActivityListTimeZoneSpread(WeekActivity weekActivity)
	{
		WeekActivity resultActivity = generateTimeZoneSpread(weekActivity);
		List<WeekActivity> weekActivityList = getSharedAppDataState().getEmbeddedWeekActivity().getWeekActivityList();
		for (int i = 0; i < weekActivityList.size(); i++)
		{
			if (isEqualWeekActivitiesDayDetailsHrefs(weekActivityList.get(i), resultActivity))
			{
				weekActivityList.get(i).setTimeZoneSpread(resultActivity.getTimeZoneSpread());
				weekActivityList.set(i, updateLinks(weekActivityList.get(i), resultActivity));
				weekActivityList.get(i).setTotalActivityDurationMinutes(resultActivity.getTotalActivityDurationMinutes());
				break;
			}
		}
	}

	/**
	 * Save User app acvitiy to local db
	 */
	@Override
	public void postActivityToDB(String applicationName, Date startDate, Date endDate)
	{
		try
		{
			activityTrackerDAO.saveActivities(getAppActivity(applicationName, startDate, endDate));
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread());
		}
	}

	private boolean isSyncAPICallDone = true;

	private void postActivityOnServer(AppActivity activity, boolean fromDB)
	{
		Logger.logi("postActivityOnServer", "isSyncAPICallDone: " + isSyncAPICallDone);
		User user = getSharedAppDataState().getUser();
		if (!isSyncAPICallDone)
		{
			return;
		}
		isSyncAPICallDone = false;
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> handlePostAppActivityOnSuccess(fromDB), (result) -> handlePostAppActivityOnFailure(fromDB, activity), null);
		activityNetwork.postAppActivity(user.getPostDeviceAppActivityLink(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), activity, dataLoadListenerImpl);

	}

	private Object handlePostAppActivityOnSuccess(Boolean fromDB)
	{
		//on success nothing to do, as it is posted on server. #JIRA_1022
		if (fromDB)
		{
			activityTrackerDAO.clearActivities();
		}
		isSyncAPICallDone = true;
		return null; // Dummy return value, to allow use as data load handler
	}

	private Object handlePostAppActivityOnFailure(Boolean fromDB, AppActivity activity)
	{
		//on failure, we need to store data in database to resend next time.
		if (!fromDB)
		{
			activityTrackerDAO.saveActivities(activity.getActivities());
		}
		isSyncAPICallDone = true;
		return null; // Dummy return value, to allow use as data error handler
	}

	@Override
	public void postAllDBActivities()
	{
		List<Activity> activityList = activityTrackerDAO.getActivities();
		if (activityList != null && activityList.size() > 0)
		{
			AppActivity appActivity = new AppActivity();
			appActivity.setDeviceDateTime(DateUtility.getLongFormatDate(new Date()));
			appActivity.setActivities(activityList);
			postActivityOnServer(appActivity, true);
		}
	}

	/**
	 * Buddy activity processing ***********
	 */

	@Override
	public void getWithBuddyActivity(boolean loadMore, DataLoadListener listener)
	{
		EmbeddedYonaActivity embeddedYonaActivity = getSharedAppDataState().getEmbeddedWithBuddyActivity();
		if (loadMore || isValidEmbeddedDayActivityList(embeddedYonaActivity))
		{
			int pageNo = (embeddedYonaActivity != null && embeddedYonaActivity.getPage() != null
					&& embeddedYonaActivity.getDayActivityList() != null && embeddedYonaActivity.getDayActivityList().size() > 0) ? embeddedYonaActivity.getPage().getNumber() + 1 : 0;
			User user = getSharedAppDataState().getUser();
			if (isUserWithDailyActivityReports(user))
			{
				getWithBuddyActivity(user.getLinks().getDailyActivityReportsWithBuddies().getHref(), AppConstant.PAGE_SIZE, pageNo, listener);
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
			}
		}
		else
		{
			listener.onDataLoad(getSharedAppDataState().getEmbeddedWithBuddyActivity().getDayActivityList());
		}
	}

	private boolean isValidEmbeddedDayActivityList(EmbeddedYonaActivity embeddedYonaActivity)
	{
		return (embeddedYonaActivity == null
				|| embeddedYonaActivity.getDayActivityList() == null
				|| embeddedYonaActivity.getDayActivityList().size() == 0);
	}


	private boolean isValidEmbeddedWeekActivityList(EmbeddedYonaActivity embeddedYonaActivity)
	{
		return (embeddedYonaActivity == null
				|| embeddedYonaActivity.getDayActivityList() == null
				|| embeddedYonaActivity.getDayActivityList().size() == 0);
	}

	private boolean isUserWithDailyActivityReports(User user)
	{
		return (user != null && user.getLinks() != null
				&& user.getLinks().getYonaDailyActivityReports() != null
				&& !TextUtils.isEmpty(user.getLinks().getYonaDailyActivityReports().getHref()));
	}

	@Override
	public void getComments(List<DayActivity> dayActivityList, int position, DataLoadListener listener)
	{
		DayActivity dayActivity = dayActivityList.get(position);
		if (dayActivity == null)
		{
			return;
		}
		EmbeddedYonaActivity commentsActivity = dayActivity.getComments();
		if (commentsActivity != null && commentsActivity.getPage() != null && (commentsActivity.getPage().getNumber() + 1 == commentsActivity.getPage().getTotalPages()))
		{
			listener.onDataLoad(dayActivityList);
		}
		else
		{
			getCommentsFromServer(dayActivityList, dayActivity, listener);
		}
	}

	@Override
	public void getCommentsForWeek(List<WeekActivity> weekActivityList, int position, DataLoadListener listener)
	{
		if (weekActivityList != null && weekActivityList.size() > 0)
		{
			WeekActivity weekActivity = weekActivityList.get(position);
			String urlToFetchComments;
			if (weekActivity.getComments() == null)
			{
				// No comments loaded yet
				urlToFetchComments = weekActivity.getLinks().getYonaMessages().getHref();
			}
			else if (weekActivity.getComments().getLinks().getNext() == null)
			{
				// No more comments
				listener.onDataLoad(weekActivityList);
				return;
			}
			else
			{
				urlToFetchComments = weekActivity.getComments().getLinks().getNext().getHref();
			}
			getCommentsFromServerForWeek(weekActivityList, weekActivity, urlToFetchComments, listener);
		}
	}

	@Override
	public void addComment(String url, boolean isReplying, String comment, DataLoadListener listener)
	{
		Message message = new Message();
		message.setMessage(comment);
		if (!TextUtils.isEmpty(url) && isReplying)
		{
			reply(url, prepareCommentBodyFromMessage(comment), listener);
		}
		else
		{
			doAddComment(url, message, listener);
		}
	}

	private MessageBody prepareCommentBodyFromMessage(String comment)
	{
		Properties properties = new Properties();
		properties.setMessage(comment);
		MessageBody body = new MessageBody();
		body.setProperties(properties);
		return body;
	}

	@Override
	public void addComment(WeekActivity weekActivity, String comment, DataLoadListener listener)
	{
		Message message = new Message();
		message.setMessage(comment);
		if (weekActivity != null && weekActivity.getLinks() != null && weekActivity.getLinks().getAddComment() != null)
		{
			doAddComment(weekActivity.getLinks().getAddComment().getHref(), message, listener);
		}
		else if (weekActivity.getLinks().getReplyComment() != null)
		{
			reply(weekActivity.getLinks().getReplyComment().getHref(), prepareCommentBodyFromMessage(comment), listener);
		}
	}

	private void reply(String url, MessageBody messageBody, DataLoadListener listener)
	{
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> handleCommentsDataAfterSuccess(result, listener), (result) -> handleErrorMessage(result, listener), null);
		activityNetwork.replyComment(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), messageBody, dataLoadListenerImpl);
	}

	private void doAddComment(String url, Message message, DataLoadListener listener)
	{
		DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> handleCommentsDataAfterSuccess(result, listener), (result) -> handleErrorMessage(result, listener), null);
		activityNetwork.addComment(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), message, dataLoadListenerImpl);
	}

	private Object handleCommentsDataAfterSuccess(Object result, DataLoadListener listener)
	{
		if (result instanceof YonaMessage)
		{
			listener.onDataLoad(result);
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private void getCommentsFromServerForWeek(List<WeekActivity> weekActivityList, WeekActivity weekActivity, String urlToFetchComments, DataLoadListener listener)
	{
		if (weekActivity.getLinks() != null && weekActivity.getLinks().getYonaMessages() != null && !TextUtils.isEmpty(weekActivity.getLinks().getYonaMessages().getHref()))
		{
			DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> handleGetCommentsForWeekFetchSuccess(result, weekActivity, weekActivityList, listener), (result) -> handleErrorMessage(result, listener), null);
			activityNetwork.getComments(urlToFetchComments, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), dataLoadListenerImpl);
		}
	}

	private Object handleGetCommentsForWeekFetchSuccess(Object result, WeekActivity weekActivity, List<WeekActivity> weekActivityList, DataLoadListener listener)
	{
		if (result instanceof EmbeddedYonaActivity)
		{
			updateWeekActivityList(weekActivityList, getUpdatedWeekActivity(result, weekActivity), listener);
		}
		else
		{
			listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.no_data_found)));
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private WeekActivity getUpdatedWeekActivity(Object result, WeekActivity weekActivity)
	{
		EmbeddedYonaActivity embeddedYonaActivity = (EmbeddedYonaActivity) result;
		if (weekActivity.getComments() != null)
		{
			return updateWeekActivityComments(embeddedYonaActivity, weekActivity);
		}
		weekActivity.setComments(embeddedYonaActivity);
		return weekActivity;
	}

	private WeekActivity updateWeekActivityComments(EmbeddedYonaActivity embeddedYonaActivity, WeekActivity weekActivity)
	{
		if (weekActivity.getComments().getEmbedded() == null)
		{
			weekActivity.getComments().setEmbedded(new Embedded());
		}
		if (weekActivity.getComments().getEmbedded().getYonaMessages() == null)
		{
			weekActivity.getComments().getEmbedded().setYonaMessages(new ArrayList<>());
		}
		if (embeddedYonaActivity.getEmbedded() != null && embeddedYonaActivity.getEmbedded().getYonaMessages() != null)
		{
			weekActivity.getComments().getEmbedded().getYonaMessages().addAll(embeddedYonaActivity.getEmbedded().getYonaMessages());
			weekActivity.getComments().setPage(embeddedYonaActivity.getPage());
		}
		return weekActivity;
	}

	private void getCommentsFromServer(List<DayActivity> dayActivityList, DayActivity dayActivity, DataLoadListener listener)
	{
		if (dayActivity.getLinks() != null && dayActivity.getLinks().getYonaMessages() != null && !TextUtils.isEmpty(dayActivity.getLinks().getYonaMessages().getHref()))
		{
			DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> handleGetCommentsForDayFetchSuccess(result, dayActivity, dayActivityList, listener), (result) -> handleErrorMessage(result, listener), null);
			activityNetwork.getComments(getUrlToFetchCommentsForDayActivity(dayActivity), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), dataLoadListenerImpl);
		}
	}

	private Object handleGetCommentsForDayFetchSuccess(Object result, DayActivity dayActivity, List<DayActivity> dayActivityList, DataLoadListener listener)
	{
		if (result instanceof EmbeddedYonaActivity)
		{
			setCommentsForDayActivity(result, dayActivity);
			updateDayActivityList(dayActivityList, dayActivity, listener);
		}
		else
		{
			listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.no_data_found)));
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private String getUrlToFetchCommentsForDayActivity(DayActivity dayActivity)
	{
		String urlToFetchComments;
		if (dayActivity.getComments() != null && dayActivity.getComments().getLinks() != null && dayActivity.getComments().getLinks().getNext() != null)
		{
			urlToFetchComments = dayActivity.getComments().getLinks().getNext().getHref();
		}
		else
		{
			urlToFetchComments = dayActivity.getLinks().getYonaMessages().getHref();
		}
		return urlToFetchComments;
	}

	private void setCommentsForDayActivity(Object result, DayActivity dayActivity)
	{
		EmbeddedYonaActivity embeddedYonaActivity = (EmbeddedYonaActivity) result;
		if (dayActivity.getComments() == null || dayActivity.getComments().getEmbedded() == null)
		{
			dayActivity.setComments(embeddedYonaActivity);
		}
		else if (dayActivity.getComments() != null
				&& dayActivity.getComments().getEmbedded() != null && dayActivity.getComments().getEmbedded().getYonaMessages() != null
				&& embeddedYonaActivity.getEmbedded() != null && embeddedYonaActivity.getEmbedded().getYonaMessages() != null)
		{
			dayActivity.getComments().getEmbedded().getYonaMessages().addAll(embeddedYonaActivity.getEmbedded().getYonaMessages());
			dayActivity.getComments().setPage(embeddedYonaActivity.getPage());
			dayActivity.getComments().setLinks(embeddedYonaActivity.getLinks());
		}
	}

	private void updateWeekActivityList(List<WeekActivity> weekActivityList, WeekActivity weekActivity, DataLoadListener listener)
	{
		if (weekActivityList == null)
		{
			return;
		}
		for (int i = 0; i < weekActivityList.size(); i++)
		{
			if (validateWeekActivityList(weekActivityList, weekActivity, i))
			{
				weekActivityList.set(i, weekActivity);
				listener.onDataLoad(weekActivityList);
				break;
			}
		}
	}

	private boolean validateWeekActivityList(List<WeekActivity> weekActivityList, WeekActivity weekActivity, int i)
	{
		return (weekActivityList.get(i) != null && weekActivityList.get(i).getLinks() != null && weekActivityList.get(i).getLinks().getSelf() != null
				&& !TextUtils.isEmpty(weekActivityList.get(i).getLinks().getSelf().getHref())
				&& weekActivity.getLinks() != null && weekActivity.getLinks().getSelf() != null
				&& !TextUtils.isEmpty(weekActivity.getLinks().getSelf().getHref())
				&& weekActivityList.get(i).getLinks().getSelf().getHref().equals(weekActivity.getLinks().getSelf().getHref()));
	}

	private void updateDayActivityList(List<DayActivity> dayActivityList, DayActivity dayActivity, DataLoadListener listener)
	{
		if (dayActivityList == null)
		{
			return;
		}
		for (int i = 0; i < dayActivityList.size(); i++)
		{
			if (validateDayActivityList(dayActivityList, dayActivity, i))
			{
				dayActivityList.set(i, dayActivity);
				listener.onDataLoad(dayActivityList);
				break;
			}
		}
	}

	private boolean validateDayActivityList(List<DayActivity> dayActivityList, DayActivity dayActivity, int i)
	{
		return (dayActivityList.get(i) != null && dayActivityList.get(i).getLinks() != null && dayActivityList.get(i).getLinks().getSelf() != null
				&& !TextUtils.isEmpty(dayActivityList.get(i).getLinks().getSelf().getHref())
				&& dayActivity.getLinks() != null && dayActivity.getLinks().getSelf() != null
				&& !TextUtils.isEmpty(dayActivity.getLinks().getSelf().getHref())
				&& dayActivityList.get(i).getLinks().getSelf().getHref().equals(dayActivity.getLinks().getSelf().getHref()));
	}

	private Activity getAppActivity(String applicationName, Date startDate, Date endDate)
	{
		Activity activity = new Activity();
		activity.setApplication(applicationName);
		activity.setStartTime(DateUtility.getLongFormatDate(startDate));
		activity.setEndTime(DateUtility.getLongFormatDate(endDate));
		return activity;
	}

	private void getWeeksActivity(String url, boolean isbuddyFlow, DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(url))
			{
				DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl(result -> filterAndUpdateWeekData((EmbeddedYonaActivity) result, isbuddyFlow, listener), (result) -> handleErrorMessage(result, listener), null);
				activityNetwork.getNextWeeksActivity(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), dataLoadListenerImpl);
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	private void getDailyActivity(String url, boolean isbuddyFlow, DataLoadListener listener)
	{
		try
		{
			DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl(result -> handleNextDayActivityFetchSuccess(result, isbuddyFlow, listener), (result) -> handleErrorMessage(result, listener), null);
			activityNetwork.getNextDayActivity(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), dataLoadListenerImpl);
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	private Object handleNextDayActivityFetchSuccess(Object result, boolean isbuddyFlow, DataLoadListener listener)
	{
		if (result instanceof EmbeddedYonaActivity)
		{
			filterAndUpdateDailyData((EmbeddedYonaActivity) result, isbuddyFlow, listener);
		}
		else
		{
			listener.onError(new ErrorMessage(mContext.getString(R.string.dataparseerror)));
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private Object filterAndUpdateWeekData(EmbeddedYonaActivity embeddedYonaActivity, boolean isbuddyFlow, DataLoadListener listener)
	{
		if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity() == null)
		{
			YonaApplication.getEventChangeManager().getDataState().setEmbeddedWeekActivity(embeddedYonaActivity);
		}
		if (embeddedYonaActivity != null)
		{
			if (embeddedYonaActivity.getEmbedded() != null)
			{
				setupWeekActivityListWithOverviews(embeddedYonaActivity, isbuddyFlow);
			}
			if (embeddedYonaActivity.getPage() != null)
			{
				YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().setPage(embeddedYonaActivity.getPage());
			}
			if (embeddedYonaActivity.getLinks() != null)
			{
				YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().setLinks(embeddedYonaActivity.getLinks());
			}
			listener.onDataLoad(embeddedYonaActivity);
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private void setupWeekActivityListWithOverviews(EmbeddedYonaActivity embeddedYonaActivity, boolean isbuddyFlow)
	{
		List<WeekActivity> weekActivities = new ArrayList<>();
		Embedded embedded = embeddedYonaActivity.getEmbedded();
		List<YonaWeekActivityOverview> yonaDayActivityOverviews = embedded.getYonaWeekActivityOverviews();
		List<WeekActivity> thisWeekActivities;
		for (YonaWeekActivityOverview overview : yonaDayActivityOverviews)
		{
			thisWeekActivities = new ArrayList<>();
			List<WeekActivity> overviewWeekActivities = overview.getWeekActivities();
			for (WeekActivity activity : overviewWeekActivities)
			{
				setUpWeekActivityWithGoal(isbuddyFlow, activity, overview, thisWeekActivities);
			}
			weekActivities.addAll(sortWeekActivity(thisWeekActivities));
		}
		setWeekActivityListAfterUpdate(embeddedYonaActivity, weekActivities);
	}

	private void setWeekActivityListAfterUpdate(EmbeddedYonaActivity embeddedYonaActivity, List<WeekActivity> weekActivities)
	{
		if (embeddedYonaActivity.getWeekActivityList() == null)
		{
			embeddedYonaActivity.setWeekActivityList(weekActivities);
		}
		else
		{
			YonaApplication.getEventChangeManager().getDataState().getEmbeddedWeekActivity().getWeekActivityList().addAll(weekActivities);
		}
	}

	private void setUpWeekActivityWithGoal(boolean isbuddyFlow, WeekActivity activity, YonaWeekActivityOverview overview, List<WeekActivity> thisWeekActivities)
	{
		YonaGoal goal = getYonaGoal(isbuddyFlow, activity.getLinks().getYonaGoal());
		if (goal == null)
		{
			return;
		}
		activity.setYonaGoal(goal);
		if (activity.getYonaGoal() != null)
		{
			activity.setChartTypeEnum(ChartTypeEnum.WEEK_SCORE_CONTROL);
		}
		try
		{
			activity.setStickyTitle(DateUtility.getRetriveWeek(overview.getDate()));
		}
		catch (ParseException e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread());
		}
		activity.setDate(overview.getDate());
		activity = getWeekDayActivity(activity);
		thisWeekActivities.add(activity);
	}

	private Object updateWeekActivity(WeekActivity weekActivity, DataLoadListener listener)
	{
		WeekActivity resultActivity = generateTimeZoneSpread(updateWeekActivityWithCurrentYonaGoal(weekActivity));
		if (isValidWeekActivity(weekActivity, resultActivity))
		{
			weekActivity.setTimeZoneSpread(resultActivity.getTimeZoneSpread());
			weekActivity.setTotalActivityDurationMinutes(resultActivity.getTotalActivityDurationMinutes());
		}
		if (listener != null)
		{
			listener.onDataLoad(weekActivity);
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private boolean isValidWeekActivity(WeekActivity weekActivity, WeekActivity resultActivity)
	{
		return (weekActivity != null && weekActivity.getLinks() != null && weekActivity.getLinks().getWeekDetails() != null
				&& !TextUtils.isEmpty(weekActivity.getLinks().getWeekDetails().getHref())
				&& resultActivity != null && resultActivity.getLinks() != null && resultActivity.getLinks().getSelf() != null
				&& !TextUtils.isEmpty(resultActivity.getLinks().getSelf().getHref())
				&& weekActivity.getLinks().getWeekDetails().getHref().equals(resultActivity.getLinks().getSelf().getHref()));
	}

	private WeekActivity updateWeekActivityWithCurrentYonaGoal(WeekActivity weekActivity)
	{
		YonaGoal currentYonaGoal = findYonaGoal(weekActivity.getLinks().getYonaGoal()) != null ? findYonaGoal(weekActivity.getLinks().getYonaGoal()) : findYonaBuddyGoal(weekActivity.getLinks().getYonaGoal());
		if (currentYonaGoal != null)
		{
			weekActivity.setYonaGoal(currentYonaGoal);
			if (weekActivity.getYonaGoal() != null)
			{
				weekActivity.setChartTypeEnum(ChartTypeEnum.WEEK_SCORE_CONTROL);
			}
			try
			{
				weekActivity.setStickyTitle(DateUtility.getRetriveWeek(weekActivity.getDate()));
			}
			catch (ParseException e)
			{
				AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread());
			}
			weekActivity.setDate(weekActivity.getDate());
			weekActivity = getWeekDayActivity(weekActivity);
		}
		return weekActivity;
	}

	private WeekActivity getWeekDayActivity(WeekActivity activity)
	{
		List<WeekDayActivity> mWeekDayActivityList = new ArrayList<>();
		Iterator<Map.Entry<String, String>> calDates = DateUtility.getWeekDay(activity.getDate()).entrySet().iterator();
		int i = 0;
		boolean isCurrentDateReached = false;
		int mAccomplishedGoalCount = 0;
		WeekDayEnum weekDayEnum = null;
		int color = GraphUtils.COLOR_WHITE_THREE;
		while (calDates.hasNext())
		{
			Map.Entry<String, String> pair = calDates.next();
			Calendar calendar = Calendar.getInstance();
			DayActivities dayActivity = activity.getDayActivities();

			WeekDayActivity weekDayActivity = new WeekDayActivity();
			switch (i)
			{
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
			if (activity.getLinks() != null && activity.getLinks().getYonaDayDetails() != null && !TextUtils.isEmpty(activity.getLinks().getYonaDayDetails().getHref()))
			{
				weekDayActivity.setUrl(activity.getLinks().getYonaDayDetails().getHref());
			}
			weekDayActivity.setWeekDayEnum(weekDayEnum);
			weekDayActivity.setColor(color);
			weekDayActivity.setDay(pair.getKey().toString());
			weekDayActivity.setDate(pair.getValue().toString());
			if (!isCurrentDateReached)
			{
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
	private int getGoalAccomplished(Day day)
	{
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
	private int getColor(Day day)
	{
		if (day != null)
		{
			if (day.getGoalAccomplished())
			{
				return GraphUtils.COLOR_GREEN;
			}
			else
			{
				return GraphUtils.COLOR_PINK;
			}
		}
		return GraphUtils.COLOR_WHITE_THREE;
	}


	private void filterAndUpdateDailyData(EmbeddedYonaActivity embeddedYonaActivity, boolean isBuddyFlow, DataLoadListener listener)
	{
		List<DayActivity> dayActivities = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.YONA_DATE_FORMAT, Locale.getDefault());
		if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity() == null)
		{
			YonaApplication.getEventChangeManager().getDataState().setEmbeddedDayActivity(embeddedYonaActivity);
		}
		if (embeddedYonaActivity != null)
		{
			updateDayActivityData(embeddedYonaActivity, isBuddyFlow, dayActivities);
			setupPageAndLinksForEmbeddedYonaActivity(embeddedYonaActivity, listener);
		}
		else
		{
			listener.onError(new ErrorMessage(mContext.getString(R.string.no_data_found)));
		}
	}

	private void updateDayActivityData(EmbeddedYonaActivity embeddedYonaActivity, boolean isBuddyFlow, List<DayActivity> dayActivities)
	{
		if (embeddedYonaActivity.getEmbedded() == null)
		{
			return;
		}
		Embedded embedded = embeddedYonaActivity.getEmbedded();
		List<YonaDayActivityOverview> yonaDayActivityOverviews = embedded.getYonaDayActivityOverviews();
		setDayActivityListAfterUpdate(embeddedYonaActivity, updateDayActivitiesListFromOverviews(yonaDayActivityOverviews, dayActivities, isBuddyFlow));
	}

	private List<DayActivity> updateDayActivitiesListFromOverviews(List<YonaDayActivityOverview> yonaDayActivityOverviews, List<DayActivity> dayActivities, boolean isBuddyFlow)
	{

		for (YonaDayActivityOverview overview : yonaDayActivityOverviews)
		{
			List<DayActivity> overviewDayActivities = overview.getDayActivities();
			List<DayActivity> updatedOverviewDayActivities = new ArrayList<>();
			for (DayActivity activity : overviewDayActivities)
			{
				setupDayActivityOverviews(activity, isBuddyFlow, overview, updatedOverviewDayActivities);
			}
			dayActivities.addAll(sortDayActivity(updatedOverviewDayActivities));
		}
		return dayActivities;
	}

	private void setDayActivityListAfterUpdate(EmbeddedYonaActivity embeddedYonaActivity, List<DayActivity> dayActivities)
	{
		if (embeddedYonaActivity.getDayActivityList() == null)
		{
			embeddedYonaActivity.setDayActivityList(dayActivities);
		}
		else
		{
			YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity().getDayActivityList().addAll(dayActivities);
		}
	}

	private void setupPageAndLinksForEmbeddedYonaActivity(EmbeddedYonaActivity embeddedYonaActivity, DataLoadListener listener)
	{
		EmbeddedYonaActivity embeddedDayActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedDayActivity();
		if (embeddedYonaActivity.getPage() != null)
		{
			embeddedDayActivity.setPage(embeddedYonaActivity.getPage());
		}

		if (embeddedYonaActivity.getLinks() != null)
		{
			embeddedDayActivity.setLinks(embeddedYonaActivity.getLinks());
		}
		if (listener != null)
		{
			listener.onDataLoad(embeddedYonaActivity);
		}
	}

	private void setupDayActivityOverviews(DayActivity activity, boolean isBuddyFlow, YonaDayActivityOverview overview, List<DayActivity> updatedOverviewDayActivities)
	{
		activity.setYonaGoal(getYonaGoal(isBuddyFlow, activity.getLinks().getYonaGoal()));
		setActivityChartEnumType(activity);
		String createdTime = overview.getDate();
		try
		{
			Calendar futureCalendar = Calendar.getInstance();
			futureCalendar.setTime(sdf.parse(createdTime));
			activity.setStickyTitle(DateUtility.getRelativeDate(futureCalendar));
		}
		catch (ParseException e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread());
		}
		// TODO: History check need to ve verify. Concern Issue: http://jira.yona.nu/browse/APPDEV-999.
		if (activity.getYonaGoal() != null && activity.getYonaGoal() != null/* && !activity.getYonaGoal().isHistoryItem()*/)
		{
			updatedOverviewDayActivities.add(generateTimeZoneSpread(activity));
		}
	}

	private List<DayActivity> sortDayActivity(List<DayActivity> overviewDayActiivties)
	{
		Collections.sort(overviewDayActiivties, (o1, o2) -> {
			if (!TextUtils.isEmpty(o1.getYonaGoal().getActivityCategoryName()) && !TextUtils.isEmpty(o2.getYonaGoal().getActivityCategoryName()))
			{
				return o1.getYonaGoal().getActivityCategoryName().compareTo(o2.getYonaGoal().getActivityCategoryName());
			}
			return 0;
		});
		return overviewDayActiivties;
	}

	private List<WeekActivity> sortWeekActivity(List<WeekActivity> overviewDayActiivties)
	{
		Collections.sort(overviewDayActiivties, (o1, o2) -> {
			if (!TextUtils.isEmpty(o1.getYonaGoal().getActivityCategoryName()) && !TextUtils.isEmpty(o2.getYonaGoal().getActivityCategoryName()))
			{
				return o1.getYonaGoal().getActivityCategoryName().compareTo(o2.getYonaGoal().getActivityCategoryName());
			}
			return 0;
		});
		return overviewDayActiivties;
	}

	private YonaGoal getYonaGoal(boolean isBuddyFlow, Href url)
	{
		if (!isBuddyFlow)
		{
			return findYonaGoal(url);
		}
		else
		{
			return findYonaBuddyGoal(url);
		}
	}

	private YonaGoal findYonaGoal(Href goalHref)
	{
		if (YonaApplication.getEventChangeManager().getDataState().getUser() != null && YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded() != null
				&& YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals() != null
				&& YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals().getEmbedded() != null
				&& YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals().getEmbedded().getYonaGoals() != null)
		{
			List<YonaGoal> yonaGoals = YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals().getEmbedded().getYonaGoals();
			for (YonaGoal goal : yonaGoals)
			{
				if (goal.getLinks().getSelf().getHref().equals(goalHref.getHref()))
				{
					goal.setActivityCategoryName(getActivityCategory(goal));
					goal.setNickName(YonaApplication.getEventChangeManager().getDataState().getUser().getNickname());
					return goal;
				}
			}
		}
		return null; // Dummy return value, to allow use as data load handler
	}


	@Override
	public YonaBuddy findYonaBuddy(Href yonaBuddy)
	{
		User user = YonaApplication.getEventChangeManager().getDataState().getUser();
		if (user != null && user.getEmbedded() != null
				&& user.getEmbedded().getYonaBuddies() != null
				&& user.getEmbedded().getYonaBuddies().getEmbedded() != null
				&& user.getEmbedded().getYonaBuddies().getEmbedded().getYonaBuddies() != null)
		{
			List<YonaBuddy> yonaBuddies = user.getEmbedded().getYonaBuddies().getEmbedded().getYonaBuddies();
			for (YonaBuddy buddy : yonaBuddies)
			{
				if (buddy != null && buddy.getLinks() != null && buddy.getLinks().getSelf() != null && buddy.getLinks().getSelf().getHref().equals(yonaBuddy.getHref()))
				{
					return buddy;
				}
			}
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private YonaGoal findYonaBuddyGoal(Href goalHref)
	{
		User user = YonaApplication.getEventChangeManager().getDataState().getUser();
		if (!isUserWithBuddies(user))
		{
			return null;
		}
		List<YonaBuddy> yonaBuddies = user.getEmbedded().getYonaBuddies().getEmbedded().getYonaBuddies();
		for (YonaBuddy buddy : yonaBuddies)
		{
			if (buddy.getYonaGoals() == null)
			{
				continue;
			}
			return findMatchingGoalFromYonaBuddy(goalHref, buddy);

		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private YonaGoal findMatchingGoalFromYonaBuddy(Href goalHref, YonaBuddy buddy)
	{
		for (YonaGoal goal : buddy.getYonaGoals())
		{
			if (goal.getLinks().getSelf().getHref().equals(goalHref.getHref()))
			{
				goal.setActivityCategoryName(getActivityCategory(goal));
				goal.setNickName(buddy.getNickname());
				return goal;
			}
		}
		return null;
	}

	private boolean isUserWithBuddies(User user)
	{
		return (user != null && user.getEmbedded() != null
				&& user.getEmbedded().getYonaBuddies() != null
				&& user.getEmbedded().getYonaBuddies().getEmbedded() != null
				&& user.getEmbedded().getYonaBuddies().getEmbedded().getYonaBuddies() != null);
	}

	private String getActivityCategory(YonaGoal goal)
	{
		ActivityCategories categories = APIManager.getInstance().getActivityCategoryManager().getListOfActivityCategories();
		if (categories != null)
		{
			List<YonaActivityCategories> categoriesList = categories.getEmbeddedActivityCategories().getYonaActivityCategories();
			for (YonaActivityCategories yonaActivityCategories : categoriesList)
			{
				if (yonaActivityCategories.get_links().getSelf().getHref().equals(goal.getLinks().getYonaActivityCategory().getHref()))
				{
					return yonaActivityCategories.getName();
				}
			}
		}
		return null;
	}

	@Override
	public String getActivityCategoryName(String categoryPath)
	{
		ActivityCategories categories = APIManager.getInstance().getActivityCategoryManager().getListOfActivityCategories();
		if (categories != null)
		{
			List<YonaActivityCategories> categoriesList = categories.getEmbeddedActivityCategories().getYonaActivityCategories();
			for (YonaActivityCategories yonaActivityCategories : categoriesList)
			{
				if (yonaActivityCategories.get_links().getSelf().getHref().equals(categoryPath))
				{
					return yonaActivityCategories.getName();
				}
			}
		}
		return null;
	}

	private DayActivity generateTimeZoneSpread(DayActivity activity)
	{

		if (activity.getSpread() != null)
		{
			List<Integer> spreadsList = activity.getSpread();
			List<Integer> spreadCellsList;
			boolean isBudgetGoal = false;
			if (activity.getYonaGoal() != null && activity.getYonaGoal().getSpreadCells() != null)
			{
				isBudgetGoal = activity.getYonaGoal().getType().equals(GoalsEnum.BUDGET_GOAL.getActionString()) && activity.getYonaGoal().getMaxDurationMinutes() != 0;
				spreadCellsList = activity.getYonaGoal().getSpreadCells();
			}
			else
			{
				spreadCellsList = new ArrayList<>();
			}
			List<TimeZoneSpread> timeZoneSpreadList = new ArrayList<>();
			for (int i = 0; i < spreadsList.size(); i++)
			{
				setTimeZoneSpread(i, spreadsList.get(i), timeZoneSpreadList, spreadCellsList.contains(i) || isBudgetGoal);
			}
			activity.setTimeZoneSpread(timeZoneSpreadList);
		}
		return activity;
	}

	private void setTimeZoneSpread(int index, int spreadListValue, List<TimeZoneSpread> timeZoneSpreadList, boolean allowed)
	{
		TimeZoneSpread timeZoneSpread = new TimeZoneSpread();
		timeZoneSpread.setIndex(index);
		timeZoneSpread.setAllowed(allowed);
		int maxSpreadTime = 15;
		if (spreadListValue > 0)
		{
			if (allowed)
			{
				timeZoneSpread.setColor(GraphUtils.COLOR_BLUE);
			}
			else
			{
				timeZoneSpread.setColor(GraphUtils.COLOR_PINK);
			}
			timeZoneSpread.setUsedValue(spreadListValue); // ex. set 10 min as blue used during allowed time.
			timeZoneSpreadList.add(timeZoneSpread);
			//Now create remaining time's other object:
			if (spreadListValue < maxSpreadTime)
			{
				TimeZoneSpread secondSpread = new TimeZoneSpread();
				secondSpread.setIndex(index);
				secondSpread.setAllowed(allowed);
				timeZoneSpreadList.add(addToArray(allowed, secondSpread, maxSpreadTime - spreadListValue));
			}
		}
		else
		{
			timeZoneSpreadList.add(addToArray(allowed, timeZoneSpread, maxSpreadTime - spreadListValue));
		}
	}

	private TimeZoneSpread addToArray(boolean allowed, TimeZoneSpread timeZoneSpread, int usage)
	{
		if (allowed)
		{
			timeZoneSpread.setColor(GraphUtils.COLOR_GREEN);
		}
		else
		{
			timeZoneSpread.setColor(GraphUtils.COLOR_BULLET_LIGHT_DOT);
		}
		timeZoneSpread.setUsedValue(usage); // out of 15 mins, if 10 min used, so here we need to show 5 min as green
		return timeZoneSpread;
	}

	private WeekActivity generateTimeZoneSpread(WeekActivity activity)
	{
		if (activity.getSpread() != null)
		{
			List<Integer> spreadsList = activity.getSpread();
			List<Integer> spreadCellsList;
			if (activity.getYonaGoal() != null && activity.getYonaGoal().getSpreadCells() != null)
			{
				spreadCellsList = activity.getYonaGoal().getSpreadCells();
			}
			else
			{
				spreadCellsList = new ArrayList<>();
			}
			List<TimeZoneSpread> timeZoneSpreadList = new ArrayList<>();
			for (int i = 0; i < spreadsList.size(); i++)
			{
				setTimeZoneSpread(i, spreadsList.get(i), timeZoneSpreadList, spreadCellsList.contains(i));
			}
			activity.setTimeZoneSpread(timeZoneSpreadList);
		}
		return activity;
	}

	private void getWithBuddyActivity(String url, int itemsPerPage, int pageNo, DataLoadListener listener)
	{
		try
		{
			DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> handleBuddyActivityFetchSuccess(result, listener), (result) -> handleErrorMessage(result, listener), null);
			activityNetwork.getWithBuddyActivity(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), itemsPerPage, pageNo, dataLoadListenerImpl);
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	private Object handleBuddyActivityFetchSuccess(Object result, DataLoadListener listener)
	{
		if (result instanceof EmbeddedYonaActivity)
		{
			filterAndUpdateWithBuddyData((EmbeddedYonaActivity) result, listener);
		}
		else
		{
			listener.onError(new ErrorMessage(mContext.getString(R.string.dataparseerror)));
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private DayActivity processEachDayActivityDetails(DayActivity dayActivity, String createdTime)
	{
		try
		{
			dayActivity.setYonaGoal(getYonaGoal(dayActivity.getLinks().getYonaUser() == null, dayActivity.getLinks().getYonaGoal()));
			setActivityChartEnumType(dayActivity);
			Calendar futureCalendar = Calendar.getInstance();
			futureCalendar.setTime(sdf.parse(createdTime));
			dayActivity.setStickyTitle(DateUtility.getRelativeDate(futureCalendar));
			return dayActivity;
		}
		catch (ParseException e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread());
		}
		return null;
	}

	private List<DayActivity> getListOfProcessedDayActivities(YonaDayActivityOverview dayActivityOverview)
	{
		List<DayActivity> currentDayActivities = dayActivityOverview.getDayActivities();
		List<DayActivity> updatedDayActivities = new ArrayList<>();
		for (DayActivity activity : currentDayActivities)
		{
			if (activity.getDayActivitiesForUsers() != null)
			{
				for (DayActivity buddyActivity : activity.getDayActivitiesForUsers())
				{
					buddyActivity = processEachDayActivityDetails(buddyActivity, dayActivityOverview.getDate());
					if (!buddyActivity.getYonaGoal().isHistoryItem())
					{
						updatedDayActivities.add(generateTimeZoneSpread(buddyActivity));
					}
				}
			}
		}
		return updatedDayActivities;
	}


	private EmbeddedYonaActivity processEmbeddedYonaActivity(EmbeddedYonaActivity embeddedYonaActivity)
	{
		if (embeddedYonaActivity.getEmbedded() == null)
		{
			return embeddedYonaActivity;
		}
		Embedded embedded = embeddedYonaActivity.getEmbedded();
		List<DayActivity> dayActivities = new ArrayList<>();
		List<YonaDayActivityOverview> yonaDayActivityOverviews = embedded.getYonaDayActivityOverviews();
		for (YonaDayActivityOverview dayActivityOverView : yonaDayActivityOverviews)
		{
			dayActivities.addAll(sortDayActivity(getListOfProcessedDayActivities(dayActivityOverView)));
		}
		if (embeddedYonaActivity.getDayActivityList() == null)
		{
			embeddedYonaActivity.setDayActivityList(dayActivities);
		}
		else
		{
			YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList().addAll(dayActivities);
		}
		return embeddedYonaActivity;
	}

	private void filterAndUpdateWithBuddyData(EmbeddedYonaActivity embeddedYonaActivity, DataLoadListener listener)
	{
		try
		{
			if (YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity() == null)
			{
				YonaApplication.getEventChangeManager().getDataState().setEmbeddedWithBuddyActivity(embeddedYonaActivity);
			}
			if (embeddedYonaActivity != null)
			{
				updateEmbeddedBuddyActivity(embeddedYonaActivity, listener);
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.no_data_found)));
			}
		}
		catch (NullPointerException e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	private void updateEmbeddedBuddyActivity(EmbeddedYonaActivity embeddedYonaActivity, DataLoadListener listener)
	{
		embeddedYonaActivity = processEmbeddedYonaActivity(embeddedYonaActivity);
		EmbeddedYonaActivity embeddedBuddyActivity = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity();
		if (embeddedYonaActivity.getPage() != null)
		{
			embeddedBuddyActivity.setPage(embeddedYonaActivity.getPage());
		}
		if (embeddedYonaActivity.getLinks() != null)
		{
			embeddedBuddyActivity.setLinks(embeddedYonaActivity.getLinks());
		}
		getBuddyDetailOfEachSpread();
		listener.onDataLoad(embeddedYonaActivity);
	}

	private DayActivity setActivityChartEnumType(DayActivity dayActivity)
	{
		switch (GoalsEnum.fromName(dayActivity.getYonaGoal().getType()))
		{
			case BUDGET_GOAL:
				dayActivity.setChartTypeEnum((dayActivity.getYonaGoal().getMaxDurationMinutes() == 0)
						? ChartTypeEnum.NOGO_CONTROL : ChartTypeEnum.TIME_BUCKET_CONTROL);
				break;
			case TIME_ZONE_GOAL:
				dayActivity.setChartTypeEnum(ChartTypeEnum.TIME_FRAME_CONTROL);
				break;
			default:
				throw new IllegalArgumentException("Unknown goal type");
		}
		return dayActivity;
	}


	private Object updateDayActivity(DayActivity activity, DataLoadListener listener)
	{
		YonaGoal currentYonaGoal = findYonaGoal(activity.getLinks().getYonaGoal()) != null ? findYonaGoal(activity.getLinks().getYonaGoal()) : findYonaBuddyGoal(activity.getLinks().getYonaGoal());
		activity.setYonaGoal(currentYonaGoal);
		setActivityChartEnumType(activity);
		String createdTime = activity.getDate();
		try
		{
			Calendar futureCalendar = Calendar.getInstance();
			futureCalendar.setTime(sdf.parse(createdTime));
			activity.setStickyTitle(DateUtility.getRelativeDate(futureCalendar));
		}
		catch (ParseException e)
		{
			AppUtils.reportException(NotificationManagerImpl.class.getSimpleName(), e, Thread.currentThread());
		}
		listener.onDataLoad(generateTimeZoneSpread(activity));
		return null; // Dummy return value, to allow use as data load handler
	}

	private void getBuddyDetailOfEachSpread()
	{
		List<DayActivity> dayActivities = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList();
		for (DayActivity dayActivity : dayActivities)
		{
			if (dayActivity.getTimeZoneSpread() == null || (dayActivity.getTimeZoneSpread() != null && dayActivity.getTimeZoneSpread().size() == 0))
			{
				DataLoadListenerImpl dataLoadListenerImpl = new DataLoadListenerImpl((result) -> handleDayDetailActivityFetchSuccess(result), null, null);
				APIManager.getInstance().getActivityManager().getDayDetailActivity(dayActivity.getLinks().getYonaDayDetails().getHref(), dataLoadListenerImpl);
			}
		}
	}

	private Object handleDayDetailActivityFetchSuccess(Object result)
	{
		try
		{
			if (result instanceof DayActivity && YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity() != null)
			{
				DayActivity resultActivity = generateTimeZoneSpread((DayActivity) result);
				setupDayActivityListWithTimeZoneSpread(resultActivity);
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread());
		}
		return null; // Dummy return value, to allow use as data load handler
	}

	private void setupDayActivityListWithTimeZoneSpread(DayActivity resultActivity)
	{
		List<DayActivity> dayActivityList = YonaApplication.getEventChangeManager().getDataState().getEmbeddedWithBuddyActivity().getDayActivityList();
		if (dayActivityList == null)
		{
			return;
		}
		for (int i = 0; i < dayActivityList.size(); i++)
		{
			try
			{
				if (dayActivityList.get(i).getLinks().getYonaDayDetails().getHref().equals(resultActivity.getLinks().getSelf().getHref()))
				{
					dayActivityList.get(i).setTimeZoneSpread(resultActivity.getTimeZoneSpread());
					dayActivityList.set(i, updateLinks(dayActivityList.get(i), resultActivity));
					break;
				}
			}
			catch (Exception e)
			{
				AppUtils.reportException(ActivityManagerImpl.class.getSimpleName(), e, Thread.currentThread());
			}
		}
	}

	private DayActivity updateLinks(DayActivity actualActivity, DayActivity resultActivity)
	{
		Links resultLinks = resultActivity.getLinks();
		if (resultLinks == null)
		{
			return actualActivity;
		}
		Links actualLinks = actualActivity.getLinks();
		updateActivityLinks(resultLinks, actualLinks);
		return actualActivity;
	}

	private WeekActivity updateLinks(WeekActivity actualActivity, WeekActivity resultActivity)
	{
		Links resultLinks = resultActivity.getLinks();
		if (resultLinks == null)
		{
			return actualActivity;
		}
		Links actualLinks = actualActivity.getLinks();
		updateActivityLinks(resultLinks, actualLinks);
		return actualActivity;
	}

	private void updateActivityLinks(Links resultLinks, Links actualLinks)
	{
		populatePaginationLinks(resultLinks, actualLinks);
		updateActivityLinksFromResponse(resultLinks, actualLinks);
	}

	private Links populatePaginationLinks(Links resultLinks, Links actualLinks)
	{
		if (resultLinks.getSelf() != null)
		{
			actualLinks.setSelf(resultLinks.getSelf());
		}
		if (resultLinks.getNext() != null)
		{
			actualLinks.setNext(resultLinks.getNext());
		}
		if (resultLinks.getPrev() != null)
		{
			actualLinks.setPrev(resultLinks.getPrev());
		}
		if (resultLinks.getFirst() != null)
		{
			actualLinks.setFirst(resultLinks.getFirst());
		}
		if (resultLinks.getLast() != null)
		{
			actualLinks.setLast(resultLinks.getLast());
		}
		return actualLinks;
	}

	private void updateActivityLinksFromResponse(Links resultLinks, Links actualLinks)
	{
		if (resultLinks.getEdit() != null)
		{
			actualLinks.setEdit(resultLinks.getEdit());
		}
		if (resultLinks.getReplyComment() != null)
		{
			actualLinks.setEdit(resultLinks.getReplyComment());
		}
		if (resultLinks.getYonaMessages() != null)
		{
			actualLinks.setYonaMessages(resultLinks.getYonaMessages());
		}
		if (resultLinks.getYonaUser() != null)
		{
			actualLinks.setYonaUser(resultLinks.getYonaUser());
		}
		if (resultLinks.getYonaDayDetails() != null)
		{
			actualLinks.setYonaDayDetails(resultLinks.getYonaDayDetails());
		}
		if (resultLinks.getYonaBuddy() != null)
		{
			actualLinks.setYonaBuddy(resultLinks.getYonaBuddy());
		}
		if (resultLinks.getAddComment() != null)
		{
			actualLinks.setAddComment(resultLinks.getAddComment());
		}
	}
}

