/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
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

package com;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.drm.DrmManagerClient;
import android.location.Country;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.adeebnqo.Thula.BuildConfig;
import com.adeebnqo.Thula.LogTag;
import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.spam.SDCardStorage;
import com.android.mms.transaction.MmsSystemEventReceiver;
import com.android.mms.util.DownloadManager;
import com.android.mms.util.RateController;
import com.adeebnqo.Thula.common.LiveViewManager;
import com.adeebnqo.Thula.common.google.DraftCache;
import com.adeebnqo.Thula.common.google.PduLoaderManager;
import com.adeebnqo.Thula.common.google.ThumbnailManager;
import com.adeebnqo.Thula.data.Contact;
import com.adeebnqo.Thula.data.Conversation;
import com.adeebnqo.Thula.transaction.NotificationManager;
import com.adeebnqo.Thula.ui.mms.layout.LayoutManager;
import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class ThulaApp extends Application {
    public static final String LOG_TAG = "Mms";

    private SearchRecentSuggestions mRecentSuggestions;
    private TelephonyManager mTelephonyManager;
    private String mCountryIso;
    private static ThulaApp sThulaApp = null;
    private PduLoaderManager mPduLoaderManager;
    private ThumbnailManager mThumbnailManager;
    private DrmManagerClient mDrmManagerClient;
    private RefWatcher refWatcher;

    public static String FIRST_RUN_KEY;

    @Override
    public void onCreate() {
        super.onCreate();

        FIRST_RUN_KEY = BuildConfig.VERSION_CODE+"_first_run";

        if (Log.isLoggable(LogTag.STRICT_MODE_TAG, Log.DEBUG)) {
            // Log tag for enabling/disabling StrictMode violation log. This will dump a stack
            // in the log that shows the StrictMode violator.
            // To enable: adb shell setprop log.tag.Mms:strictmode DEBUG
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        }

        sThulaApp = this;

        loadDefaultPreferenceValues();

        // Initialize leakcanary
        refWatcher = LeakCanary.install(this);

        // Figure out the country *before* loading contacts and formatting numbers
        Country country = new Country(Locale.getDefault().getCountry(), Country.COUNTRY_SOURCE_LOCALE);
        mCountryIso = country.getCountryIso();

        Context context = getApplicationContext();
        mPduLoaderManager = new PduLoaderManager(context);
        mThumbnailManager = new ThumbnailManager(context);

        com.android.mms.MmsConfig.init(this);
        Contact.init(this);
        DraftCache.init(this);
        Conversation.init(this);
        DownloadManager.init(this);
        RateController.init(this);
        LayoutManager.init(this);
        NotificationManager.init(this);
        LiveViewManager.init(this);
        Fabric.with(this, new Crashlytics());
        //MessagingNotification.init(this);

        activePendingMessages();

        SDCardStorage sdCardStorage = new SDCardStorage(this);
        if (sdCardStorage.hasExternalStorage()) {
            sdCardStorage.loadSpamList();
        }
    }

    public static RefWatcher getRefWatcher(Context context) {
        ThulaApp application = (ThulaApp) context.getApplicationContext();
        return application.refWatcher;
    }

    @SuppressLint("CommitPrefEdits")
    private void loadDefaultPreferenceValues() {
        // Load the default values
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    /**
     * Try to process all pending messages(which were interrupted by user, OOM, Mms crashing,
     * etc...) when Mms app is (re)launched.
     */
    private void activePendingMessages() {
        // For Mms: try to process all pending transactions if possible
        MmsSystemEventReceiver.wakeUpService(this);

        // For Sms: retry to send smses in outbox and queued box
        //sendBroadcast(new Intent(SmsReceiverService.ACTION_SEND_INACTIVE_MESSAGE, null, this, SmsReceiver.class));
    }

    synchronized public static ThulaApp getApplication() {
        return sThulaApp;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mPduLoaderManager.onLowMemory();
        mThumbnailManager.onLowMemory();
    }

    public PduLoaderManager getPduLoaderManager() {
        return mPduLoaderManager;
    }

    public ThumbnailManager getThumbnailManager() {
        return mThumbnailManager;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutManager.getInstance().onConfigurationChanged(newConfig);
    }

    /**
     * @return Returns the TelephonyManager.
     */
    public TelephonyManager getTelephonyManager() {
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager)getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }

    /**
     * Returns the content provider wrapper that allows access to recent searches.
     * @return Returns the content provider wrapper that allows access to recent searches.
     */
    public SearchRecentSuggestions getRecentSuggestions() {
        return mRecentSuggestions;
    }

    // This function CAN return null.
    public String getCurrentCountryIso() {
        if (mCountryIso == null) {
            Country country = new Country(Locale.getDefault().getCountry(), Country.COUNTRY_SOURCE_LOCALE);
            mCountryIso = country.getCountryIso();
        }
        return mCountryIso;
    }

    public DrmManagerClient getDrmManagerClient() {
        if (mDrmManagerClient == null) {
            mDrmManagerClient = new DrmManagerClient(getApplicationContext());
        }
        return mDrmManagerClient;
    }

}
