/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.PasscodeManager;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.PreferenceConstant;

/**
 * Created by bhargavsuthar on 3/31/16.
 */
public class PasscodeManagerImpl implements PasscodeManager
{

	private int counter = 0;

	/**
	 * Validating the passcode which user has entered
	 *
	 * @param passCode
	 * @return
	 */
	@Override
	public boolean validatePasscode(String passCode)
	{
		if (passCode.equalsIgnoreCase(getStoredPassCode()))
		{
			counter = 0;
			updateWrongPasscodeCounter(counter);
			return true;
		}
		else
		{
			updateWrongPasscodeCounter(counter++);
		}
		return false;
	}

	/**
	 * Validate passcode Length
	 *
	 * @param passcode
	 * @return
	 */
	@Override
	public boolean checkPasscodeLength(String passcode)
	{
		int PASSCODE_LENGTH = 4;
		return passcode.length() == PASSCODE_LENGTH;
	}


	/**
	 * Validate passcode while creating
	 *
	 * @param passcode
	 * @param passcode2
	 * @return
	 */
	@Override
	public boolean validateTwoPasscode(String passcode, String passcode2)
	{
		if (passcode.equalsIgnoreCase(passcode2))
		{
			storedPassCode(passcode);
			return true;
		}
		return false;
	}

	/**
	 * Checked the counter of wrong enter passocode limit is reached or not
	 *
	 * @return boolean boolean
	 */
	@Override
	public boolean isWrongCounterReached()
	{
		return getWrongPasscodeCounter() >= AppConstant.MAX_COUNTER;
	}

	/**
	 * update the counter while entering wrong passcode
	 *
	 * @return int
	 */
	private int getWrongPasscodeCounter()
	{
		return YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getInt(PreferenceConstant.YONA_WRONG_PASSCODE_COUNTER, 0);
	}

	/**
	 * Reset the Passocde wrong counter once user has successfull log in
	 */
	@Override
	public void resetWrongCounter()
	{
		counter = 0;
		updateWrongPasscodeCounter(counter);
	}

	/**
	 * Update the passcode counter into preference storage
	 *
	 * @param counter
	 */
	private void updateWrongPasscodeCounter(int counter)
	{
		YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putInt(PreferenceConstant.YONA_WRONG_PASSCODE_COUNTER, counter).apply();
	}


	/**
	 * Get the value of passcode which is stored into preference storage
	 *
	 * @return int
	 */
	private String getStoredPassCode()
	{
		return YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getString(PreferenceConstant.YONA_PASSCODE, "");
	}

	/**
	 * Stored User passcode into pref
	 *
	 * @param code
	 */
	private void storedPassCode(String code)
	{
		YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putString(PreferenceConstant.YONA_PASSCODE, code).commit();
		YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_PASSCODE, true).commit();
	}

}
