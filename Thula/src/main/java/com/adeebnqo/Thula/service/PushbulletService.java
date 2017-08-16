package com.adeebnqo.Thula.service;

import com.adeebnqo.Thula.mmssms.Message;
import com.adeebnqo.Thula.mmssms.Transaction;
import com.adeebnqo.Thula.data.ConversationLegacy;
import com.adeebnqo.Thula.transaction.NotificationManager;
import com.adeebnqo.Thula.transaction.SmsHelper;
import com.adeebnqo.Thula.ui.popup.QKReplyActivity;
import com.pushbullet.android.extension.MessagingExtension;

public class PushbulletService extends MessagingExtension {
    private final String TAG = "PushbulletService";

    @Override
    protected void onMessageReceived(String conversationIden, String body) {
        long threadId = Long.parseLong(conversationIden);
        ConversationLegacy conversation = new ConversationLegacy(getApplicationContext(), threadId);

        Transaction sendTransaction = new Transaction(getApplicationContext(), SmsHelper.getSendSettings(getApplicationContext()));
        Message message = new com.adeebnqo.Thula.mmssms.Message(body, conversation.getAddress());
        message.setType(com.adeebnqo.Thula.mmssms.Message.TYPE_SMSMMS);
        sendTransaction.sendNewMessage(message, conversation.getThreadId());

        if (QKReplyActivity.sIsShowing && conversation.getThreadId() == QKReplyActivity.sThreadId) {
            QKReplyActivity.close();
        }

        NotificationManager.update(getApplicationContext());
    }

    @Override
    protected void onConversationDismissed(String conversationIden) {
        long threadId = Long.parseLong(conversationIden);
        ConversationLegacy conversation = new ConversationLegacy(getApplicationContext(), threadId);
        conversation.markRead();
    }

}
