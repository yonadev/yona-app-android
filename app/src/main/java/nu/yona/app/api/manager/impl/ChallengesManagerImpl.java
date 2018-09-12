/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.manager.ChallengesManager;
import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.api.model.Goals;
import nu.yona.app.api.model.Href;
import nu.yona.app.api.model.Links;
import nu.yona.app.api.model.PostBudgetYonaGoal;
import nu.yona.app.api.model.PostTimeZoneYonaGoal;
import nu.yona.app.api.model.YonaActivityCategories;
import nu.yona.app.api.model.YonaGoal;
import nu.yona.app.enums.GoalsEnum;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by bhargavsuthar on 20/04/16.
 */
public class ChallengesManagerImpl implements ChallengesManager
{

	private final List<YonaActivityCategories> mYonaActivityCategoriesList;
	private final HashMap<String, String> mGoalCategoriesMap;
	private final List<YonaGoal> budgetCategoriesGoalList;
	private final List<YonaGoal> timeZoneCategoriesGoalList;
	private final List<YonaGoal> noGoCategoriesGoalList;

	/**
	 * Instantiates a new Challenges manager.
	 */
	public ChallengesManagerImpl()
	{
		mYonaActivityCategoriesList = new ArrayList<>();
		budgetCategoriesGoalList = new ArrayList<>();
		timeZoneCategoriesGoalList = new ArrayList<>();
		noGoCategoriesGoalList = new ArrayList<>();
		mGoalCategoriesMap = new HashMap<>();
		getListOfCategory();
		filterCategoriesGoal(APIManager.getInstance().getGoalManager().getUserGoalFromDb());
	}

	private synchronized void getListOfCategory()
	{
		mYonaActivityCategoriesList.clear();
		mGoalCategoriesMap.clear();
		Goals mYonaGoals = APIManager.getInstance().getGoalManager().getUserGoalFromDb();
		ActivityCategories embeddedActivityCategories = APIManager.getInstance().getActivityCategoryManager().getListOfActivityCategories();
		if (embeddedActivityCategories != null && embeddedActivityCategories.getEmbeddedActivityCategories() != null && embeddedActivityCategories.getEmbeddedActivityCategories().getYonaActivityCategories() != null)
		{
			for (YonaActivityCategories activityCategories : embeddedActivityCategories.getEmbeddedActivityCategories().getYonaActivityCategories())
			{
				if (activityCategories != null)
				{
					mYonaActivityCategoriesList.add(activityCategories);
					if (!TextUtils.isEmpty(activityCategories.getName()) && !TextUtils.isEmpty(activityCategories.get_links().getSelf().getHref()))
					{
						mGoalCategoriesMap.put(activityCategories.getName(), activityCategories.get_links().getSelf().getHref());
					}
					if (mYonaGoals != null && mYonaGoals.getEmbedded() != null && mYonaGoals.getEmbedded().getYonaGoals() != null && mYonaGoals.getEmbedded().getYonaGoals().size() > 0
							&& activityCategories != null && activityCategories.get_links() != null && activityCategories.get_links().getSelf() != null)
					{
						List<YonaGoal> yonaGoals = sortGoals(mYonaGoals.getEmbedded().getYonaGoals());
						for (YonaGoal mYonaGoal : yonaGoals)
						{
							if (mYonaGoal != null && mYonaGoal.getLinks() != null && mYonaGoal.getLinks().getYonaActivityCategory() != null)
							{
								if (!TextUtils.isEmpty(mYonaGoal.getLinks().getYonaActivityCategory().getHref()) && !TextUtils.isEmpty(activityCategories.get_links().getSelf().getHref()) && mYonaGoal.getLinks().getYonaActivityCategory().getHref().equalsIgnoreCase(activityCategories.get_links().getSelf().getHref()))
								{
									mYonaActivityCategoriesList.remove(activityCategories);
								}
							}
						}
					}
				}
			}
		}
	}

	private synchronized Goals filterCategoriesGoal(Goals userGoals)
	{
		budgetCategoriesGoalList.clear();
		timeZoneCategoriesGoalList.clear();
		noGoCategoriesGoalList.clear();
		if (userGoals != null && userGoals.getEmbedded() != null && userGoals.getEmbedded().getYonaGoals() != null && userGoals.getEmbedded().getYonaGoals().size() > 0)
		{
			List<YonaGoal> yonaGoals = sortGoals(userGoals.getEmbedded().getYonaGoals());
			for (YonaGoal mYonaGoal : yonaGoals)
			{
				if (mYonaGoal != null)
				{
					for (Map.Entry<String, String> entry : mGoalCategoriesMap.entrySet())
					{
						if (entry.getValue().equals(mYonaGoal.getLinks().getYonaActivityCategory().getHref()))
						{
							mYonaGoal.setActivityCategoryName(entry.getKey());
							break;
						}
					}
					if (!mYonaGoal.isHistoryItem())
					{
						if (mYonaGoal.getType().equalsIgnoreCase(GoalsEnum.BUDGET_GOAL.getActionString()) && (mYonaGoal.getMaxDurationMinutes() > 0))
						{
							budgetCategoriesGoalList.add(mYonaGoal);
						}
						else if (mYonaGoal.getType().equalsIgnoreCase(GoalsEnum.TIME_ZONE_GOAL.getActionString()) && (mYonaGoal.getZones() != null))
						{
							timeZoneCategoriesGoalList.add(mYonaGoal);
						}
						else
						{
							noGoCategoriesGoalList.add(mYonaGoal);
						}
					}
				}
			}
			userGoals.getEmbedded().setYonaGoals(yonaGoals);
		}
		hasUserCreatedGoal();
		return userGoals;
	}

	private List<YonaGoal> sortGoals(List<YonaGoal> yonaGoals)
	{
		for (YonaGoal mYonaGoal : yonaGoals)
		{
			if (mYonaGoal != null)
			{
				for (Map.Entry<String, String> entry : mGoalCategoriesMap.entrySet())
				{
					if (entry.getValue().equals(mYonaGoal.getLinks().getYonaActivityCategory().getHref()))
					{
						mYonaGoal.setActivityCategoryName(entry.getKey());
						break;
					}
				}
			}
		}
		Collections.sort(yonaGoals, new Comparator<YonaGoal>()
		{
			@Override
			public int compare(YonaGoal o1, YonaGoal o2)
			{
				if (!TextUtils.isEmpty(o1.getActivityCategoryName()) && !TextUtils.isEmpty(o2.getActivityCategoryName()))
				{
					return o1.getActivityCategoryName().compareTo(o2.getActivityCategoryName());
				}
				return 0;
			}
		});
		return yonaGoals;
	}

	/**
	 * Checked wheather user has created any goal or not
	 *
	 * @return boolean boolean
	 */
	public boolean hasUserCreatedGoal()
	{
		Goals userGoals = APIManager.getInstance().getGoalManager().getUserGoalFromDb();
		try
		{
			if (userGoals != null
					&& !YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getBoolean(PreferenceConstant.STEP_CHALLENGES, false))
			{
				int serversetGoal = 0;
				if (userGoals != null && userGoals.getEmbedded() != null && userGoals.getEmbedded().getYonaGoals() != null)
				{
					for (int i = 0; i < userGoals.getEmbedded().getYonaGoals().size(); i++)
					{
						if (userGoals.getEmbedded().getYonaGoals().get(i).getLinks() != null && userGoals.getEmbedded().getYonaGoals().get(i).getLinks().getEdit() == null)
						{
							serversetGoal++;
						}
					}
				}
				if (YonaApplication.getEventChangeManager().getDataState().getUser().getEmbedded().getYonaGoals().getEmbedded().getYonaGoals().size() > serversetGoal)
				{
					YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_CHALLENGES, true).commit();
					return true;
				}
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(ActivityCategoryManagerImpl.class.getSimpleName(), e, Thread.currentThread());
		}
		return false;
	}


	/**
	 * Get YonaActivityCategories by selection of budgetType
	 *
	 * @param budgetType
	 * @return
	 */
	@Override
	public YonaActivityCategories getSelectedGoalCategories(String budgetType)
	{
		ActivityCategories embeddedActivityCategories = APIManager.getInstance().getActivityCategoryManager().getListOfActivityCategories();
		if (embeddedActivityCategories != null && embeddedActivityCategories.getEmbeddedActivityCategories() != null && embeddedActivityCategories.getEmbeddedActivityCategories().getYonaActivityCategories() != null)
		{
			for (YonaActivityCategories activityCategories : embeddedActivityCategories.getEmbeddedActivityCategories().getYonaActivityCategories())
			{
				if (!TextUtils.isEmpty(activityCategories.getName()) && activityCategories.getName().equalsIgnoreCase(budgetType))
				{
					return activityCategories;
				}
			}
		}

		return null;
	}

	@Override
	public List<YonaActivityCategories> getListOfCategories()
	{
		getListOfCategory();
		Collections.sort(mYonaActivityCategoriesList, new Comparator<YonaActivityCategories>()
		{
			@Override
			public int compare(YonaActivityCategories o1, YonaActivityCategories o2)
			{
				if (!TextUtils.isEmpty(o1.getName()) && !TextUtils.isEmpty(o2.getName()))
				{
					return o1.getName().compareTo(o2.getName());
				}
				return 0;
			}
		});
		return mYonaActivityCategoriesList;
	}

	/**
	 * Get list of Budget Goals
	 *
	 * @return
	 */
	@Override
	public List<YonaGoal> getListOfBudgetGoals()
	{
		return budgetCategoriesGoalList;
	}

	/**
	 * Get list of Timezone Goals
	 *
	 * @return
	 */
	@Override
	public List<YonaGoal> getListOfTimeZoneGoals()
	{
		return timeZoneCategoriesGoalList;
	}

	/**
	 * Get list of NOGO Goals
	 *
	 * @return
	 */
	@Override
	public List<YonaGoal> getListOfNoGoGoals()
	{
		return noGoCategoriesGoalList;
	}

	@Override
	public YonaGoal getYonaGoalByCategoryType(YonaActivityCategories activityCategories)
	{
		Goals userGoals = APIManager.getInstance().getGoalManager().getUserGoalFromDb();
		if (userGoals != null && userGoals.getEmbedded() != null && userGoals.getEmbedded().getYonaGoals().size() > 0)
		{
			for (YonaGoal mYonaGoal : userGoals.getEmbedded().getYonaGoals())
			{
				if (mYonaGoal != null && activityCategories.get_links().getSelf().getHref().equalsIgnoreCase(mYonaGoal.getLinks().getYonaActivityCategory().getHref()))
				{
					mYonaGoal.setActivityCategoryName(activityCategories.getName());
					return mYonaGoal;
				}
			}
		}

		return null;
	}

	/**
	 * @param time     milliseconds
	 * @param goal     YonaGoal selected object
	 * @param listener
	 */
	@Override
	public void postBudgetGoals(long time, YonaGoal goal, final DataLoadListener listener)
	{
		APIManager.getInstance().getGoalManager().postBudgetGoals(getPostYonaGoalForBudget(time, goal), new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLEAR_ACTIVITY_LIST, null);
				getUserGoal(listener);
			}

			@Override
			public void onError(Object errorMessage)
			{
				listener.onError(errorMessage);
			}
		});
	}

	@Override
	public void postBudgetGoals(long time, final YonaActivityCategories category, final DataLoadListener listener)
	{
		APIManager.getInstance().getGoalManager().postBudgetGoals(getPostYonaGoalForBudget(time, category), new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLEAR_ACTIVITY_LIST, null);
				getUserGoal(listener);
			}

			@Override
			public void onError(Object errorMessage)
			{
				listener.onError(errorMessage);
			}
		});
	}

	private PostBudgetYonaGoal getPostYonaGoalForBudget(long time, YonaActivityCategories category)
	{
		PostBudgetYonaGoal postBudgetYonaGoal = new PostBudgetYonaGoal();
		postBudgetYonaGoal.setType(GoalsEnum.BUDGET_GOAL.getActionString());
		Links links = new Links();
		links.setYonaActivityCategory(category.get_links().getSelf());
		postBudgetYonaGoal.setMaxDurationMinutes(time);
		postBudgetYonaGoal.setLinks(links);

		return postBudgetYonaGoal;
	}

	private PostBudgetYonaGoal getPostYonaGoalForBudget(long time, YonaGoal goal)
	{
		PostBudgetYonaGoal postBudgetYonaGoal = new PostBudgetYonaGoal();
		postBudgetYonaGoal.setType(GoalsEnum.BUDGET_GOAL.getActionString());
		Links links = new Links();
		Href yonaActivityCategory = new Href();
		yonaActivityCategory.setHref(goal.getLinks().getYonaActivityCategory().getHref());
		links.setYonaActivityCategory(yonaActivityCategory);
		postBudgetYonaGoal.setMaxDurationMinutes(time);
		postBudgetYonaGoal.setLinks(links);

		return postBudgetYonaGoal;
	}

	private PostBudgetYonaGoal getUpdateYonaGoalForBudget(long time, YonaGoal goal)
	{
		PostBudgetYonaGoal postBudgetYonaGoal = new PostBudgetYonaGoal();
		postBudgetYonaGoal.setType(GoalsEnum.BUDGET_GOAL.getActionString());
		Links links = new Links();
		Href yonaActivityCategory = new Href();
		yonaActivityCategory.setHref(goal.getLinks().getYonaActivityCategory().getHref());
		Href yonaSelfLink = new Href();
		yonaSelfLink.setHref(goal.getLinks().getSelf().getHref());
		links.setYonaActivityCategory(yonaActivityCategory);
		links.setSelf(yonaSelfLink);
		postBudgetYonaGoal.setMaxDurationMinutes(time);
		postBudgetYonaGoal.setLinks(links);

		return postBudgetYonaGoal;
	}

	/**
	 * @param timeGoal Array of Time Goals
	 * @param goal     selected yona Goal
	 * @param listener
	 */
	@Override
	public void postTimeGoals(List<String> timeGoal, YonaGoal goal, final DataLoadListener listener)
	{
		APIManager.getInstance().getGoalManager().postTimeZoneGoals(getPostYonaGoalForTimeZone(timeGoal, goal), new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLEAR_ACTIVITY_LIST, null);
				getUserGoal(listener);
			}

			@Override
			public void onError(Object errorMessage)
			{
				listener.onError(errorMessage);
			}
		});
	}

	@Override
	public void postTimeGoals(List<String> timeGoal, YonaActivityCategories categories, final DataLoadListener listener)
	{
		APIManager.getInstance().getGoalManager().postTimeZoneGoals(getPostYonaGoalForTimeZone(timeGoal, categories), new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLEAR_ACTIVITY_LIST, null);
				getUserGoal(listener);
			}

			@Override
			public void onError(Object errorMessage)
			{
				listener.onError(errorMessage);
			}
		});
	}

	@Override
	public void updateTimeGoals(List<String> timeGoal, YonaGoal goal, final DataLoadListener listener)
	{
		APIManager.getInstance().getGoalManager().updateTimeZoneGoals(getUpdateYonaGoalForTimeZone(timeGoal, goal), new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLEAR_ACTIVITY_LIST, null);
				getUserGoal(listener);
			}

			@Override
			public void onError(Object errorMessage)
			{
				listener.onError(errorMessage);
			}
		});
	}

	@Override
	public void updateBudgetGoals(long time, YonaGoal goal, final DataLoadListener listener)
	{
		APIManager.getInstance().getGoalManager().updateBudgetGoals(getUpdateYonaGoalForBudget(time, goal), new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLEAR_ACTIVITY_LIST, null);
				getUserGoal(listener);
			}

			@Override
			public void onError(Object errorMessage)
			{
				listener.onError(errorMessage);
			}
		});
	}

	private PostTimeZoneYonaGoal getPostYonaGoalForTimeZone(List<String> timeGoal, YonaGoal goal)
	{
		PostTimeZoneYonaGoal postBudgetYonaGoal = new PostTimeZoneYonaGoal();
		postBudgetYonaGoal.setType(GoalsEnum.TIME_ZONE_GOAL.getActionString());
		Links links = new Links();
		Href yonaActivityCategory = new Href();
		yonaActivityCategory.setHref(goal.getLinks().getYonaActivityCategory().getHref());
		links.setYonaActivityCategory(yonaActivityCategory);
		postBudgetYonaGoal.setZones(timeGoal);
		postBudgetYonaGoal.setLinks(links);

		return postBudgetYonaGoal;
	}

	private PostTimeZoneYonaGoal getPostYonaGoalForTimeZone(List<String> timeGoal, YonaActivityCategories category)
	{
		PostTimeZoneYonaGoal postBudgetYonaGoal = new PostTimeZoneYonaGoal();
		postBudgetYonaGoal.setType(GoalsEnum.TIME_ZONE_GOAL.getActionString());
		Links links = new Links();
		links.setYonaActivityCategory(category.get_links().getSelf());
		postBudgetYonaGoal.setLinks(links);
		postBudgetYonaGoal.setZones(timeGoal);
		return postBudgetYonaGoal;
	}

	private PostTimeZoneYonaGoal getUpdateYonaGoalForTimeZone(List<String> timeGoal, YonaGoal goal)
	{
		PostTimeZoneYonaGoal postBudgetYonaGoal = new PostTimeZoneYonaGoal();
		postBudgetYonaGoal.setType(GoalsEnum.TIME_ZONE_GOAL.getActionString());
		Links links = new Links();
		Href yonaActivityCategory = new Href();
		yonaActivityCategory.setHref(goal.getLinks().getYonaActivityCategory().getHref());
		Href yonaSelf = new Href();
		yonaSelf.setHref(goal.getLinks().getSelf().getHref());
		links.setYonaActivityCategory(yonaActivityCategory);
		links.setSelf(yonaSelf);
		postBudgetYonaGoal.setZones(timeGoal);
		postBudgetYonaGoal.setLinks(links);

		return postBudgetYonaGoal;
	}

	/**
	 * @param yonaGoal YonaGoal object to delete it.
	 * @param listener
	 */
	@Override
	public void deleteGoal(YonaGoal yonaGoal, final DataLoadListener listener)
	{
		APIManager.getInstance().getGoalManager().deleteGoal(yonaGoal, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_CLEAR_ACTIVITY_LIST, null);
				getUserGoal(listener);
			}

			@Override
			public void onError(Object errorMessage)
			{
				listener.onError(errorMessage);
			}
		});
	}

	/**
	 * It will check and return which type of goal is
	 *
	 * @param yonaGoal
	 * @return
	 */
	@Override
	public GoalsEnum typeOfGoal(YonaGoal yonaGoal)
	{

		if (yonaGoal != null)
		{
			if (yonaGoal.getMaxDurationMinutes() > 0 && GoalsEnum.BUDGET_GOAL.getActionString().equalsIgnoreCase(yonaGoal.getType()))
			{
				return GoalsEnum.BUDGET_GOAL;
			}
			else if (yonaGoal.getZones() != null && GoalsEnum.TIME_ZONE_GOAL.getActionString().equalsIgnoreCase(yonaGoal.getType()))
			{
				return GoalsEnum.TIME_ZONE_GOAL;
			}
			else
			{
				return GoalsEnum.NOGO;
			}
		}
		return null;
	}

	@Override
	public void getUserGoal(final DataLoadListener listener)
	{
		//Require to update user as well
		APIManager.getInstance().getAuthenticateManager().getUserFromServer();
		APIManager.getInstance().getGoalManager().getUserGoal(new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				saveGoals((Goals) result, listener);
			}

			@Override
			public void onError(Object errorMessage)
			{
				listener.onError(errorMessage);
			}
		});
	}

	private void saveGoals(Goals goals, final DataLoadListener listener)
	{
		getListOfCategory();
		filterCategoriesGoal(goals);
		APIManager.getInstance().getGoalManager().saveGoals(goals, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				listener.onDataLoad(result);
				filterCategoriesGoal(APIManager.getInstance().getGoalManager().getUserGoalFromDb());
			}

			@Override
			public void onError(Object errorMessage)
			{
				listener.onError(errorMessage);
			}
		});
	}
}
