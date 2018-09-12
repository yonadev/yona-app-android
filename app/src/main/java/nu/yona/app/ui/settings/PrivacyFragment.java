/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.settings;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import nu.yona.app.R;
import nu.yona.app.analytics.AnalyticsConstant;
import nu.yona.app.analytics.YonaAnalytics.BackHook;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.YonaActivity;
import nu.yona.app.utils.ApiList;

/**
 * Created by kinnarvasa on 11/05/16.
 */
public class PrivacyFragment extends BaseFragment
{

	private WebView webView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.privacy_fragment, null);

		setupToolbar(view);

		webView = (WebView) view.findViewById(R.id.webView);
		webView.loadUrl(ApiList.PRIVACY_PAGE);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon)
			{
				super.onPageStarted(view, url, favicon);
				YonaActivity.getActivity().showLoadingView(true, null);
			}

			@Override
			public void onPageFinished(WebView view, String url)
			{
				super.onPageFinished(view, url);
				YonaActivity.getActivity().showLoadingView(false, null);
			}
		});
		setHook(new BackHook(AnalyticsConstant.BACK_FROM_SCREEN_PRIVACY));
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		setTitleAndIcon();
	}

	private void setTitleAndIcon()
	{
		profileCircleImageView.setVisibility(View.GONE);
		toolbarTitle.setText(getString(R.string.privacy));
		rightIcon.setVisibility(View.GONE);
	}

	@Override
	public String getAnalyticsCategory()
	{
		return AnalyticsConstant.SCREEN_PRIVACY;
	}
}
