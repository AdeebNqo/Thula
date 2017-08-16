package com.adeebnqo.Thula.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.transaction.NotificationManager;

public class MessageFailedReceiver extends BroadcastReceiver {
    private final String TAG = "MessageFailedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, R.string.toast_message_failure, Toast.LENGTH_LONG).show();
        NotificationManager.notifyFailed(context);
    }
}
