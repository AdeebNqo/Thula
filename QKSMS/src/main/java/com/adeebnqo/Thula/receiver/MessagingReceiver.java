package com.adeebnqo.Thula.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.util.Log;
import com.adeebnqo.Thula.common.ConversationPrefsHelper;
import com.adeebnqo.Thula.data.Message;
import com.adeebnqo.Thula.service.NotificationService;
import com.adeebnqo.Thula.spam.SharedPreferenceSpamNumberStorage;
import com.adeebnqo.Thula.spam.SpamNumberStorage;
import com.adeebnqo.Thula.transaction.NotificationManager;
import com.adeebnqo.Thula.transaction.SmsHelper;

public class MessagingReceiver extends BroadcastReceiver {
    private final String TAG = "MessagingReceiver";

    String address;
    String body;
    long date;

    Uri uri;

    @Override
    public void onReceive(Context context, Intent intent) {
        abortBroadcast();

        Log.i(TAG, "Received text message");

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

            if (spamStorage.contains(message)){

                message.markSeen();

            } else {
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
