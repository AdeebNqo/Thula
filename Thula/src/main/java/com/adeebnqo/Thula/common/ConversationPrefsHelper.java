package com.adeebnqo.Thula.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.ui.settings.SettingsFragment;

public class ConversationPrefsHelper {

    public static final String CONVERSATIONS_FILE = "conversation_";

    private Context mContext;
    private SharedPreferences mPrefs;
    private SharedPreferences mConversationPrefs;

    public ConversationPrefsHelper(Context context, long threadId) {
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mConversationPrefs = context.getSharedPreferences(CONVERSATIONS_FILE + threadId, Context.MODE_PRIVATE);
    }

    public boolean getNotificationsEnabled() {
        return getBoolean(SettingsFragment.NOTIFICATIONS, true);
    }

    public boolean getNotificationLedEnabled() {
        return getBoolean(SettingsFragment.NOTIFICATION_LED, true);
    }

    public String getNotificationLedColor() {
        return getString(SettingsFragment.NOTIFICATION_LED_COLOR, "" + mContext.getResources().getColor(R.color.red_light));
    }

    public boolean getWakePhoneEnabled() {
        return getBoolean(SettingsFragment.WAKE, false);
    }

    public boolean getTickerEnabled() {
        return getBoolean(SettingsFragment.NOTIFICATION_TICKER, true);
    }

    public boolean getPrivateNotificationsEnabled() {
        return getBoolean(SettingsFragment.NOTIFICATION_PRIVATE, false);
    }

    public boolean getVibrateEnabled() {
        return getBoolean(SettingsFragment.NOTIFICATION_VIBRATE, true);
    }

    public String getNotificationSound() {
        return getString(SettingsFragment.NOTIFICATION_TONE, SettingsFragment.DEFAULT_NOTIFICATION_TONE);
    }

    public Uri getNotificationSoundUri() {
        return Uri.parse(getNotificationSound());
    }

    public boolean getCallButtonEnabled() {
        return getBoolean(SettingsFragment.NOTIFICATION_CALL_BUTTON, false);
    }

    public void putString(String key, String value) {
        mConversationPrefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        String globalValue = mPrefs.getString(key, defaultValue);
        return mConversationPrefs.getString(key, globalValue);
    }

    public void putBoolean(String key, boolean value) {
        mConversationPrefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        boolean globalValue = mPrefs.getBoolean(key, defaultValue);
        return mConversationPrefs.getBoolean(key, globalValue);
    }

    public SharedPreferences getConversationPrefs() {
        return mPrefs;
    }
}