/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.File;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.manager.AuthenticateManager;
import nu.yona.app.api.manager.dao.AuthenticateDAO;
import nu.yona.app.api.manager.network.AuthenticateNetworkImpl;
import nu.yona.app.api.model.ErrorMessage;
import nu.yona.app.api.model.OTPVerficationCode;
import nu.yona.app.api.model.ProfilePhoto;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.AppUtils;
import nu.yona.app.utils.Logger;
import nu.yona.app.utils.MobileNumberFormatter;
import nu.yona.app.utils.PreferenceConstant;

import static nu.yona.app.YonaApplication.getSharedAppDataState;
import static nu.yona.app.YonaApplication.getSharedUserPreferences;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class AuthenticateManagerImpl implements AuthenticateManager
{

	private AuthenticateDAO authenticateDao;
	private final Context mContext;
	private AuthenticateNetworkImpl authNetwork;


	public AuthenticateDAO getAuthenticateDao()
	{
		return authenticateDao;
	}

	public void setAuthenticateDao(AuthenticateDAO authenticateDao)
	{
		this.authenticateDao = authenticateDao;
	}

	public AuthenticateNetworkImpl getAuthNetwork()
	{
		return authNetwork;
	}

	public void setAuthNetwork(AuthenticateNetworkImpl authNetwork)
	{
		this.authNetwork = authNetwork;
	}

	/**
	 * Instantiates a new Authenticate manager.
	 *
	 * @param context the context
	 */
	public AuthenticateManagerImpl(Context context)
	{
		authenticateDao = new AuthenticateDAO(context);
		authNetwork = new AuthenticateNetworkImpl();
		this.mContext = context;
	}

	/**
	 * Validate user's first and last name
	 *
	 * @return true if first name and last name are correct.
	 */
	@Override
	public boolean validateText(String string)
	{
		// do validation for first name and last name
		return !TextUtils.isEmpty(string);
	}

	/**
	 * @param mobileNumber user's mobile number
	 * @return true if number is in expected format
	 */
	@Override
	public boolean isMobileNumberValid(String mobileNumber)
	{

		return MobileNumberFormatter.isValid(mobileNumber);
	}

	/**
	 * @param registerUser RegisterUser object
	 * @param listener
	 */
	@Override
	public void registerUser(RegisterUser registerUser, boolean isEditMode, final DataLoadListener listener)
	{
		try
		{
			String url = null;
			if (isEditMode)
			{
				url = getSharedAppDataState().getUser().getLinks().getEdit().getHref();
			}
			authNetwork.registerUser(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), registerUser, isEditMode, new DataLoadListener()
			{
				@Override
				public void onDataLoad(Object result)
				{
					getSharedUserPreferences().edit().putBoolean(PreferenceConstant.STEP_REGISTER, true).commit();
					updateDataForRegisterUser(result, listener);
				}

				@Override
				public void onError(Object errorMessage)
				{
					listener.onError(errorMessage);
				}
			});
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	@Override
	public void registerUser(String url, RegisterUser user, final DataLoadListener listener)
	{
		authNetwork.registerUser(url, user, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				if (result != null)
				{
					getSharedUserPreferences().edit().putBoolean(PreferenceConstant.STEP_REGISTER, true).commit();
					updateDataForRegisterUser(result, listener);
				}
			}

			@Override
			public void onError(Object errorMessage)
			{
				if (errorMessage instanceof ErrorMessage)
				{
					listener.onError(errorMessage);
				}
				else
				{
					listener.onError(new ErrorMessage(errorMessage.toString() != null ? errorMessage.toString() : ""));
				}
			}
		});
	}

	@Override
	public void uploadUserPhoto(String url, String password, File file, final DataLoadListener listener)
	{
		authNetwork.uploadUserPhoto(url, password, file, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				if (result != null && result instanceof ProfilePhoto)
				{
					User newUser = getUser();
					newUser.getLinks().setUserPhoto(((ProfilePhoto) result).getLinks().getUserPhoto());
					updateDataForRegisterUser(newUser, listener);
				}
			}

			@Override
			public void onError(Object errorMessage)
			{
				if (errorMessage instanceof ErrorMessage)
				{
					listener.onError(errorMessage);
				}
				else
				{
					listener.onError(new ErrorMessage(errorMessage.toString() != null ? errorMessage.toString() : ""));
				}
			}
		});
	}


	@Override
	public void readDeepLinkUserInfo(String url, final DataLoadListener listener)
	{
		authNetwork.readDeepLinkData(url, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				if (listener != null)
				{
					listener.onDataLoad(result);
				}
			}

			@Override
			public void onError(Object errorMessage)
			{
				if (listener != null)
				{
					if (errorMessage instanceof ErrorMessage)
					{
						listener.onError(errorMessage);
					}
					else
					{
						listener.onError(new ErrorMessage(errorMessage.toString()));
					}
				}
			}
		});
	}

	/**
	 * This will get response of server in case of register successful and store it in database, update on UI after that via listener.
	 *
	 * @param result
	 * @param listener
	 */
	private void updateDataForRegisterUser(Object result, final DataLoadListener listener)
	{

		authenticateDao.updateDataForRegisterUser(result, new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				getSharedAppDataState().reloadUser();
				if (listener != null)
				{
					listener.onDataLoad(result);
				}
			}

			@Override
			public void onError(Object errorMessage)
			{
				if (listener != null)
				{
					if (errorMessage instanceof ErrorMessage)
					{
						listener.onError(errorMessage);
					}
					else
					{
						listener.onError(new ErrorMessage(errorMessage.toString()));
					}
				}
			}
		});
	}

	/**
	 * @param user     Register User object
	 * @param otp      OTP - Sms received value
	 * @param listener
	 */
	@Override
	public void verifyOTP(RegisterUser user, String otp, final DataLoadListener listener)
	{
		try
		{
			if (user == null)
			{
				verifyOTP(otp, listener);
			}
			else
			{
				authNetwork.registerUserOverride(YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), user, otp, new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						SharedPreferences.Editor editor = getSharedUserPreferences().edit();
						editor.putBoolean(PreferenceConstant.STEP_REGISTER, true);
						editor.putBoolean(PreferenceConstant.STEP_OTP, true);
						editor.commit();
						updateDataForRegisterUser(result, listener);
					}

					@Override
					public void onError(Object errorMessage)
					{
						listener.onError(errorMessage);
					}
				});
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	@Override
	public void verifyOTP(final String otp, final DataLoadListener listener)
	{
		try
		{
			if (getSharedAppDataState().getUser() != null && getSharedAppDataState().getUser().getLinks() != null
					&& getSharedAppDataState().getUser().getLinks().getSelf() != null && !TextUtils.isEmpty(getSharedAppDataState().getUser().getLinks().getSelf().getHref()))
			{
				getUser(getSharedAppDataState().getUser().getLinks().getSelf().getHref(), new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						verifyOTPAfterUser(otp, listener);
					}

					@Override
					public void onError(Object errorMessage)
					{
						if (errorMessage instanceof ErrorMessage)
						{
							listener.onError(errorMessage);
						}
						else
						{
							listener.onError(new ErrorMessage(errorMessage.toString()));
						}
					}
				});
			}
			else
			{
				verifyOTPAfterUser(otp, listener);
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	/**
	 * Verify otp after user.
	 *
	 * @param otp      OTP received in sms
	 * @param listener the listener
	 */
	public void verifyOTPAfterUser(String otp, final DataLoadListener listener)
	{
		try
		{
			if (otp.length() == AppConstant.OTP_LENGTH)
			{
				if (getSharedUserPreferences().getBoolean(PreferenceConstant.PROFILE_OTP_STEP, false) || !getSharedUserPreferences().getBoolean(PreferenceConstant.STEP_PASSCODE, false))
				{
					if (authenticateDao.getUser() != null && authenticateDao.getUser().getLinks() != null
							&& authenticateDao.getUser().getLinks().getYonaConfirmMobileNumber() != null
							&& !TextUtils.isEmpty(authenticateDao.getUser().getLinks().getYonaConfirmMobileNumber().getHref()))
					{
						authNetwork.verifyMobileNumber(YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), authenticateDao.getUser().getLinks().getYonaConfirmMobileNumber().getHref(),
								new OTPVerficationCode(otp), new DataLoadListener()
								{

									@Override
									public void onDataLoad(Object result)
									{
										updateDataForRegisterUser(result, listener);
										getSharedUserPreferences().edit().putBoolean(PreferenceConstant.STEP_OTP, true).commit();
									}

									@Override
									public void onError(Object errorMessage)
									{
										if (errorMessage instanceof ErrorMessage)
										{
											listener.onError(errorMessage);
										}
										else
										{
											listener.onError(new ErrorMessage(errorMessage.toString()));
										}
									}
								});
					}
					else
					{
						listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
					}
				}
				else
				{
					if (getSharedAppDataState().getUser() != null
							&& getSharedAppDataState().getUser().getLinks() != null
							&& getSharedAppDataState().getUser().getLinks().getVerifyPinReset() != null
							&& !TextUtils.isEmpty(getSharedAppDataState().getUser().getLinks().getVerifyPinReset().getHref()))
					{
						authNetwork.doVerifyPin(authenticateDao.getUser().getLinks().getVerifyPinReset().getHref(), otp, new DataLoadListener()
						{
							@Override
							public void onDataLoad(Object result)
							{
								updatePreferenceForPinReset();
								authNetwork.doClearPin(authenticateDao.getUser().getLinks().getClearPinReset().getHref());
								listener.onDataLoad(result);
							}

							@Override
							public void onError(Object errorMessage)
							{
								if (errorMessage instanceof ErrorMessage)
								{
									listener.onError(errorMessage);
								}
								else
								{
									listener.onError(new ErrorMessage(errorMessage.toString()));
								}
							}
						});
					}
					else
					{
						listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
					}
				}
			}
			else
			{
				listener.onError(new ErrorMessage(YonaApplication.getAppContext().getString(R.string.invalidotp)));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	private void updatePreferenceForPinReset()
	{
		SharedPreferences.Editor editor = getSharedUserPreferences().edit();
		editor.putBoolean(PreferenceConstant.USER_BLOCKED, false);
		editor.putBoolean(PreferenceConstant.STEP_OTP, true);
		editor.putBoolean(PreferenceConstant.STEP_PASSCODE, false);
		editor.commit();
	}

	@Override
	public void requestPinReset(final DataLoadListener listener)
	{
		try
		{
			storedPassCode("");
			getSharedUserPreferences().edit().putBoolean(PreferenceConstant.STEP_OTP, false).commit();
			User user = authenticateDao.getUser();
			if (user != null && user.getLinks() != null && user.getLinks().getRequestPinReset() != null && !TextUtils.isEmpty(user.getLinks().getRequestPinReset().getHref()))
			{
				authNetwork.doPasscodeReset(user.getLinks().getRequestPinReset().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						getUser(result, listener);
					}

					@Override
					public void onError(Object errorMessage)
					{
						if (errorMessage instanceof ErrorMessage)
						{
							listener.onError(errorMessage);
						}
						else
						{
							listener.onError(new ErrorMessage(errorMessage.toString()));
						}
					}
				});
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	private void getUser(final Object object, final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(getUser().getLinks().getSelf().getHref()))
			{
				getUser(getUser().getLinks().getSelf().getHref(), new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						listener.onDataLoad(object);
					}

					@Override
					public void onError(Object errorMessage)
					{
						listener.onDataLoad(object); // because we want to carry forward object to UI part, we don't worry here about user update.
					}
				});
			}
			else
			{
				listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	/**
	 * @param url      url to fetch user
	 * @param listener
	 */
	@Override
	public void getUser(final String url, final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(url))
			{
				authNetwork.getUser(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						updateDataForRegisterUser(result, listener);
					}

					@Override
					public void onError(Object errorMessage)
					{
						if (listener != null)
						{
							if (errorMessage instanceof ErrorMessage)
							{
								listener.onError(errorMessage);
							}
							else
							{
								listener.onError(new ErrorMessage(errorMessage.toString()));
							}
						}
					}
				});
			}
			else
			{
				if (listener != null)
				{
					listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
				}
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}

	}

	@Override
	public void getFriendProfile(final String url, final DataLoadListener listener)
	{
		try
		{
			if (!TextUtils.isEmpty(url))
			{
				authNetwork.getUser(url, YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						listener.onDataLoad(result);
					}

					@Override
					public void onError(Object errorMessage)
					{
						if (listener != null)
						{
							if (errorMessage instanceof ErrorMessage)
							{
								listener.onError(errorMessage);
							}
							else
							{
								listener.onError(new ErrorMessage(errorMessage.toString()));
							}
						}
					}
				});
			}
			else
			{
				if (listener != null)
				{
					listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
				}
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	@Override
	public void resendOTP(final DataLoadListener listener)
	{
		try
		{
			if (getSharedAppDataState().getUser() != null && getSharedAppDataState().getUser().getLinks() != null
					&& getSharedAppDataState().getUser().getLinks().getSelf() != null && !TextUtils.isEmpty(getSharedAppDataState().getUser().getLinks().getSelf().getHref()))
			{
				getUser(getSharedAppDataState().getUser().getLinks().getSelf().getHref(), new DataLoadListener()
				{
					@Override
					public void onDataLoad(Object result)
					{
						resendOTPAfterUser(listener);
					}

					@Override
					public void onError(Object errorMessage)
					{
						if (errorMessage instanceof ErrorMessage)
						{
							listener.onError(errorMessage);
						}
						else
						{
							listener.onError(new ErrorMessage(errorMessage.toString()));
						}
					}
				});
			}
			else
			{
				resendOTPAfterUser(listener);
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	private void resendOTPAfterUser(final DataLoadListener listener)
	{
		try
		{
			User user = getSharedAppDataState().getUser();
			if (user != null && user.getLinks() != null)
			{
				if (user.getLinks().getResendMobileNumberConfirmationCode() != null
						&& !TextUtils.isEmpty(user.getLinks().getResendMobileNumberConfirmationCode().getHref()))
				{
					authNetwork.resendOTP(authenticateDao.getUser().getLinks().getResendMobileNumberConfirmationCode().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener()
					{
						@Override
						public void onDataLoad(Object result)
						{
							listener.onDataLoad(result);
						}

						@Override
						public void onError(Object errorMessage)
						{
							if (errorMessage instanceof ErrorMessage)
							{
								listener.onError(errorMessage);
							}
							else
							{
								listener.onError(new ErrorMessage(errorMessage.toString()));
							}
						}
					});
				}
				else if (user.getLinks().getResendPinResetConfirmationCode() != null
						&& !TextUtils.isEmpty(user.getLinks().getResendPinResetConfirmationCode().getHref()))
				{
					authNetwork.doPasscodeReset(user.getLinks().getResendPinResetConfirmationCode().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener()
					{
						@Override
						public void onDataLoad(Object result)
						{
							listener.onDataLoad(result);
						}

						@Override
						public void onError(Object errorMessage)
						{
							if (errorMessage instanceof ErrorMessage)
							{
								listener.onError(errorMessage);
							}
							else
							{
								listener.onError(new ErrorMessage(errorMessage.toString()));
							}
						}
					});
				}
				else
				{
					listener.onError(new ErrorMessage(mContext.getString(R.string.urlnotfound)));
				}
			}
			else
			{
				OverrideUser(listener);
			}
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	private void OverrideUser(final DataLoadListener listener)
	{
		APIManager.getInstance().getAuthenticateManager().requestUserOverride(getSharedAppDataState().getRegisterUser().getMobileNumber(), new DataLoadListener()
		{

			@Override
			public void onDataLoad(Object result)
			{
				listener.onDataLoad(result);
			}

			@Override
			public void onError(Object errorMessage)
			{
				listener.onError(new ErrorMessage(errorMessage.toString()));
			}
		});
	}

	@Override
	public void requestUserOverride(String mobileNumber, final DataLoadListener listener)
	{
		try
		{
			authNetwork.requestUserOverride(mobileNumber, new DataLoadListener()
			{
				@Override
				public void onDataLoad(Object result)
				{
					if (getSharedAppDataState().getUser() != null && getSharedAppDataState().getUser().getLinks() != null
							&& getSharedAppDataState().getUser().getLinks().getSelf() != null
							&& !TextUtils.isEmpty(getSharedAppDataState().getUser().getLinks().getSelf().getHref()))
					{
						getUser(getSharedAppDataState().getUser().getLinks().getSelf().getHref(), listener);
					}
					else
					{
						listener.onDataLoad(result);
					}
				}

				@Override
				public void onError(Object errorMessage)
				{
					if (errorMessage instanceof ErrorMessage)
					{
						listener.onError(errorMessage);
					}
					else
					{
						listener.onError(new ErrorMessage(errorMessage.toString()));
					}
				}
			});
		}
		catch (Exception e)
		{
			AppUtils.reportException(AuthenticateManagerImpl.class.getSimpleName(), e, Thread.currentThread(), listener);
		}
	}

	@Override
	public User getUser()
	{
		return authenticateDao.getUser();
	}

	@Override
	public void getUserFromServer()
	{
		if (getSharedAppDataState().getUser() != null && getSharedAppDataState().getUser().getLinks() != null
				&& getSharedAppDataState().getUser().getLinks().getSelf() != null)
		{
			Logger.logi("APPDEV-1241", "getUserFromServer");
			getUser(getSharedAppDataState().getUser().getLinks().getSelf().getHref(), new DataLoadListener()
			{
				@Override
				public void onDataLoad(Object result)
				{

				}

				@Override
				public void onError(Object errorMessage)
				{

				}
			});
		}
	}

	/**
	 * @param listener
	 */
	@Override
	public void deleteUser(final DataLoadListener listener)
	{
		authNetwork.deleteUser(authenticateDao.getUser().getLinks().getEdit().getHref(), YonaApplication.getEventChangeManager().getSharedPreference().getYonaPassword(), new DataLoadListener()
		{
			@Override
			public void onDataLoad(Object result)
			{
				SharedPreferences.Editor editor = getSharedUserPreferences().edit();
				editor.clear();
				editor.putBoolean(PreferenceConstant.STEP_TOUR, true);
				editor.commit();
				listener.onDataLoad(result);
			}

			@Override
			public void onError(Object errorMessage)
			{
				if (errorMessage instanceof ErrorMessage)
				{
					listener.onError(errorMessage);
				}
				else
				{
					listener.onError(new ErrorMessage(errorMessage.toString()));
				}
			}
		});
	}

	/**
	 * Stored User passcode into pref
	 *
	 * @param code
	 */
	private void storedPassCode(String code)
	{
		SharedPreferences.Editor yonaPref = getSharedUserPreferences().edit();
		yonaPref.putBoolean(PreferenceConstant.STEP_PASSCODE, true);
		yonaPref.commit();
	}
}
