/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import android.content.Context;

import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.manager.ActivityCategoryManager;
import nu.yona.app.api.manager.dao.ActivityCategoriesDAO;
import nu.yona.app.api.manager.dao.AuthenticateDAO;
import nu.yona.app.api.manager.network.ActivityCategoriesNetworkImpl;
import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.listener.DataLoadListenerImpl;
import nu.yona.app.utils.AppUtils;

import static nu.yona.app.YonaApplication.getSharedAppDataState;

/**
 * Created by bhargavsuthar on 14/04/16.
 */
public class ActivityCategoryManagerImpl implements ActivityCategoryManager
{

	private final ActivityCategoriesNetworkImpl activityCategoriesNetwork;
	private final ActivityCategoriesDAO activityCategoriesDAO;
	private final AuthenticateDAO authenticateDao;
	private final Context mContext;

	/**
	 * Instantiates a new Activity category manager.
	 *
	 * @param context the context
	 */
	public ActivityCategoryManagerImpl(Context context)
	{
		activityCategoriesNetwork = new ActivityCategoriesNetworkImpl();
		activityCategoriesDAO = new ActivityCategoriesDAO(DatabaseHelper.getInstance(context), context);
		authenticateDao = new AuthenticateDAO(context);
		this.mContext = context;
	}

	/**
	 * Get list of Categories and also can get categories by Id
	 *
	 * @param listener
	 */
	@Override
	public void getActivityCategoriesById(DataLoadListener listener)
	{
		DataLoadListenerImpl listenerWrapper = new DataLoadListenerImpl((result) -> updateActivityCategories(result, listener), null, listener);
		activityCategoriesNetwork.getActivityCategories(listenerWrapper);
	}

	/**
	 * Get ActivityCategories from Database
	 *
	 * @return
	 */
	@Override
	public ActivityCategories getListOfActivityCategories()
	{
		return activityCategoriesDAO.getActivityCategories();
	}

	/**
	 * storing categories into database
	 *
	 * @param result
	 * @param listener
	 */
	private Object updateActivityCategories(Object result, DataLoadListener listener)
	{
		try
		{
			activityCategoriesDAO.saveActivityCategories(((ActivityCategories) result), listener);
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityCategoryManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
		return null; // No value to return from here
	}

	@Override
	public void updateNetworkAPIEnvironment(String environmentURL)
	{
		getSharedAppDataState().setServerUrl(environmentURL);
		activityCategoriesNetwork.updateNeworkEnvironment();
	}

	@Override
	public void validateNewEnvironment(DataLoadListener listener)
	{
		try
		{
			activityCategoriesNetwork.getActivityCategories(listener);
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityCategoryManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}
}
