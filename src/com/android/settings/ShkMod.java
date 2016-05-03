/*
 * Copyright (C) 2016 ShkMod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Scanner;

public class ShkMod extends AsyncTask<String, Void, String> {

    private static final String TAG = ShkMod.class.getSimpleName();
    private static final String URL = "https://raw.githubusercontent.com/shkschneider/android_manifest/master/VERSION.json";

    private Activity mActivity;

    @Override
    protected String doInBackground(final String... params) {
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(URL).openConnection();
            httpURLConnection.setConnectTimeout(1500);
            httpURLConnection.setReadTimeout(3000);
            if (httpURLConnection.getResponseCode() == 200) {
                final InputStream inputStream = httpURLConnection.getInputStream();
                if (inputStream == null) return null;
                final String string = new Scanner(inputStream).useDelimiter("\\A").next();
                if (string == null) return null;
                final JSONObject jsonObject = new JSONObject(string);
                if (jsonObject == null) return null;
                // api
                // buildId
                // securityPatch
                // ro.mod.name
                // ro.mod.version
                // devices[]
                Log.d(TAG, jsonObject.toString());
                final String roModVersion = jsonObject.getString("ro.mod.version");
                final String buildId = jsonObject.getString("buildId");
                return "Latest version: " + roModVersion + " (" + buildId + ")";
            }
            else {
                return httpURLConnection.getResponseMessage();
            }
        }
        catch (final Exception e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(final String s) {
        super.onPostExecute(s);
        if (TextUtils.isEmpty(s)) return;
        Log.i(TAG, s);
        if (mActivity == null) return;
        Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
    }

    public void run(final Activity activity) {
        mActivity = activity;
        execute();
    }

}
