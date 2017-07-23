package com.adeebnqo.Thula.common;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import org.json.JSONObject;
import java.util.Iterator;

public enum AnalyticsManager {
    INSTANCE;

    private final static String TAG = "AnalyticsManager";
    private final static boolean LOCAL_LOGV = false;

    /**
     * Event categories
     */
    public final static String CATEGORY_MESSAGES = "messages";
    // Note: For preferences events, the action will just be the preference value.
    public final static String CATEGORY_PREFERENCE_CHANGE = "preference_change";
    public final static String CATEGORY_PREFERENCE_CLICK = "preference_click";
    public final static String CATEGORY_REPORT = "report";


    /**
     * Event actions/names
     */
    public final static String ACTION_SEND_MESSAGE = "send_message";
    public final static String ACTION_ATTACH_IMAGE = "attach_image";
    public final static String ACTION_ATTACH_FROM_CAMERA = "attach_from_camera";
    public final static String ACTION_RECEIVED_SPAM = "recieved_spam";
    public final static String ACTION_RECEIVED_MSG = "recieved_new_msg";
    public final static String ACTION_USING_VERSION = "using_version";
    public final static String ACTION_USING_NIGHTMODE = "using_night_mode";

    private boolean mNeedsInit = true;
    private Context mContext;
    private Answers fabricAnswers;

    public static AnalyticsManager getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        if (LOCAL_LOGV) Log.v(TAG, "init called. mNeedsInit: " + mNeedsInit);

        if (mNeedsInit) {
            mNeedsInit = false;
            mContext = context;
            fabricAnswers = Answers.getInstance();
        }
    }

    public void sendEvent(String eventName, JSONObject details) {
        try {
            CustomEvent event = new CustomEvent(eventName);
            Iterator<String> keyIt = details.keys();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                String value = details.getString(key);
                event.putCustomAttribute(key, value);
            }
            fabricAnswers.logCustom(event);
        } catch(Exception e) {
            if (LOCAL_LOGV) Log.d(TAG, "Sending event failed.");
        }
    }

    public void cleanUp(){

    }
}
