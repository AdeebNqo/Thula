package com.adeebnqo.Thula.receiver;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.adeebnqo.Thula.transaction.SmsHelper;
import com.adeebnqo.Thula.ui.widget.WidgetProvider;
import me.leolin.shortcutbadger.ShortcutBadger;

public class UnreadBadgeService extends IntentService {

    public static final String UNREAD_COUNT_UPDATED = "com.adeebnqo.Thula.intent.action.UNREAD_COUNT_UPDATED";

    public UnreadBadgeService() {
        super("UnreadBadgeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (UNREAD_COUNT_UPDATED.equals(intent.getAction())) {
            ShortcutBadger.with(getApplicationContext()).count(SmsHelper.getUnreadMessageCount(this));
            WidgetProvider.notifyDatasetChanged(this);
        }
    }

    public static void update(Context context) {
        Intent intent = new Intent(context, UnreadBadgeService.class);
        intent.setAction(UNREAD_COUNT_UPDATED);
        context.startService(intent);
    }
}
