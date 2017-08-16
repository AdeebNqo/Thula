package com.adeebnqo.Thula.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.util.Log;
import com.adeebnqo.Thula.common.AnalyticsManager;
import com.adeebnqo.Thula.common.ConversationPrefsHelper;
import com.adeebnqo.Thula.data.Message;
import com.adeebnqo.Thula.service.NotificationService;
import com.adeebnqo.Thula.spam.SharedPreferenceSpamNumberStorage;
import com.adeebnqo.Thula.spam.SpamNumberStorage;
import com.adeebnqo.Thula.transaction.NotificationManager;
import com.adeebnqo.Thula.transaction.SmsHelper;
import org.json.JSONObject;
import static com.adeebnqo.Thula.common.AnalyticsManager.ACTION_RECEIVED_MSG;
import static com.adeebnqo.Thula.common.AnalyticsManager.ACTION_RECEIVED_SPAM;

public class MessagingReceiver extends BroadcastReceiver {
    private final String TAG = "MessagingReceiver";

    String address;
    String body;
    long date;

    Uri uri;

    @Override
    public void onReceive(Context context, Intent intent) {
        abortBroadcast();

        if (intent.getExtras() != null) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            SmsMessage sms = messages[0];
            if (messages.length == 1 || sms.isReplace()) {
                body = sms.getDisplayMessageBody();
            } else {
                StringBuilder bodyText = new StringBuilder();
                for (SmsMessage message : messages) {
                    bodyText.append(message.getMessageBody());
                }
                body = bodyText.toString();
            }

            address = sms.getDisplayOriginatingAddress();
            date = sms.getTimestampMillis();

            uri = SmsHelper.addMessageToInbox(context, address, body, date);

            Message message = new Message(context, uri);
            ConversationPrefsHelper prefs = new ConversationPrefsHelper(context, message.getThreadId());

            SpamNumberStorage spamStorage = new SharedPreferenceSpamNumberStorage(context);

            //initialize analytics manager - in case the app is not currently open
            AnalyticsManager.getInstance().init(context);
            boolean numberContainsWhitespace = message.getAddress().contains("\\s+");

            if (spamStorage.contains(message)) {

                Log.i(TAG, "Received spam message");

                try {
                    //send spam event
                    JSONObject eventData = new JSONObject();
                    eventData.put("spam_number", message.getAddress());
                    eventData.put("message", message.getBody());
                    eventData.put("numberHasWhitespace", numberContainsWhitespace);
                    AnalyticsManager.getInstance().sendEvent(ACTION_RECEIVED_SPAM, eventData);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "exception thrown while trying to send event" );
                }

                message.markSeen();
                message.markRead();

            } else {

                Log.i(TAG, "Received normal message");

                try {
                    //send message event
                    JSONObject eventData = new JSONObject();
                    eventData.put("number", message.getAddress());
                    eventData.put("numberHasWhitespace", numberContainsWhitespace);
                    AnalyticsManager.getInstance().sendEvent(ACTION_RECEIVED_MSG, eventData);
                } catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "exception thrown while trying to send event" );
                }

                if (prefs.getNotificationsEnabled()) {
                    Intent messageHandlerIntent = new Intent(context, NotificationService.class);
                    messageHandlerIntent.putExtra(NotificationService.EXTRA_POPUP, true);
                    messageHandlerIntent.putExtra(NotificationService.EXTRA_URI, uri.toString());
                    context.startService(messageHandlerIntent);

                    UnreadBadgeService.update(context);
                    NotificationManager.create(context);
                } else {
                    message.markSeen();
                }

            }

            if (prefs.getWakePhoneEnabled()) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "MessagingReceiver");
                wakeLock.acquire();
                wakeLock.release();
            }
        }
    }
}
