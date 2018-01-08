/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import nu.yona.app.YonaApplication;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.listener.DataLoader;

/**
 * Created by kinnarvasa on 18/07/16.
 */

public class DownloadFileFromURL {
    String stringUrl;
    private DataLoadListener listener;
    private static int BUFFER_SIZE = 65536;

    public DownloadFileFromURL(String url, DataLoadListener listener) {
        this.stringUrl = url;
        this.listener = listener;
        downloadFile();
    }

    private void downloadFile() {
        DataLoader loader = new DataLoader() {

            URL url;

            @Override
            public Object doDBCall() {
                int count;
                FileOutputStream output = null;
                InputStream input = null;
                String path = "";

                HttpURLConnection connection = null;
                try {
                    url = new URL(stringUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("http.keepAlive", "false");

                    input = new BufferedInputStream(connection.getInputStream());
                    String fileName = stringUrl;
                    while (fileName.contains("/")) {
                        fileName = fileName.substring(fileName.indexOf("/") + 1);
                    }
                    path = YonaApplication.getAppContext().getFilesDir() + "/" + fileName;
                    output = YonaApplication.getAppContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                    byte data[] = new byte[BUFFER_SIZE];

                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    Logger.loge(DownloadFileFromURL.class.getSimpleName(), e.toString());
                } finally

                {
                    try {
                        connection.disconnect();
                        if (output != null) {
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                    } catch (Exception e) {
                        Logger.loge(DownloadFileFromURL.class.getSimpleName(), e.toString());
                    }
                }

                return path;
            }

            @Override
            protected void onPostExecute(Object path) {
                try {
                    if (!TextUtils.isEmpty(path.toString())) {

                        listener.onDataLoad(path);
                    } else {
                        listener.onError(path);
                    }
                } catch (Exception e) {
                    Logger.loge(DownloadFileFromURL.class.getSimpleName(), e.toString());
                }
                //super.onPostExecute(path);
            }
        };
        loader.executeAsync();
    }
}
