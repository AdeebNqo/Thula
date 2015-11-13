package com.adeebnqo.Thula.ui.conversationlist;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.data.Contact;
import com.adeebnqo.Thula.data.ContactList;
import com.adeebnqo.Thula.data.Conversation;
import com.adeebnqo.Thula.data.ConversationLegacy;
import com.adeebnqo.Thula.ui.MainActivity;
import com.adeebnqo.Thula.ui.ThemeManager;
import com.adeebnqo.Thula.ui.base.ClickyViewHolder;
import com.adeebnqo.Thula.ui.settings.SettingsFragment;
import com.adeebnqo.Thula.ui.view.AvatarView;
import com.adeebnqo.Thula.ui.view.QKTextView;

public class ConversationListViewHolder extends ClickyViewHolder<Conversation> implements Contact.UpdateListener {

    private final SharedPreferences mPrefs;

    protected View root;
    protected QKTextView snippetView;
    protected QKTextView fromView;
    protected QKTextView dateView;
    protected ImageView mutedView;
    protected ImageView unreadView;
    protected ImageView errorIndicator;
    protected AvatarView mAvatarView;
    protected ImageView mSelected;

    public ConversationListViewHolder(View view) {
        super(view);
        mPrefs = MainActivity.getPrefs(context);

        root = view;
        fromView = (QKTextView) view.findViewById(R.id.conversation_list_name);
        snippetView = (QKTextView) view.findViewById(R.id.conversation_list_snippet);
        dateView = (QKTextView) view.findViewById(R.id.conversation_list_date);
        mutedView = (ImageView) view.findViewById(R.id.conversation_list_muted);
        unreadView = (ImageView) view.findViewById(R.id.conversation_list_unread);
        errorIndicator = (ImageView) view.findViewById(R.id.conversation_list_error);
        mAvatarView = (AvatarView) view.findViewById(R.id.conversation_list_avatar);
        mSelected = (ImageView) view.findViewById(R.id.selected);
    }

    @Override
    public void onUpdate(final Contact updated) {
        boolean shouldUpdate = true;
        final Drawable drawable;
        final String name;

        if (data.getRecipients().size() == 1) {
            Contact contact = data.getRecipients().get(0);
            if (contact.getNumber().equals(updated.getNumber())) {
                drawable = contact.getAvatar(context, null);
                name = contact.getName();

                if (contact.existsInDatabase()) {
                    mAvatarView.assignContactUri(contact.getUri());
                } else {
                    mAvatarView.assignContactFromPhone(contact.getNumber(), true);
                }
            } else {
                // onUpdate was called because *some* contact was loaded, but it wasn't the contact for this
                // conversation, and thus we shouldn't update the UI because we won't be able to set the correct data
                drawable = null;
                name = "";
                shouldUpdate = false;
            }
        } else if (data.getRecipients().size() > 1) {
            drawable = null;
            name = "" + data.getRecipients().size();
            mAvatarView.assignContactUri(null);
        } else {
            drawable = null;
            name = "#";
            mAvatarView.assignContactUri(null);
        }

        final ConversationLegacy conversationLegacy = new ConversationLegacy(context, data.getThreadId());

        if (shouldUpdate) {
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAvatarView.setImageDrawable(drawable);
                    mAvatarView.setContactName(name);
                    fromView.setText(formatMessage(data, conversationLegacy));
                }
            });
        }
    }

    private CharSequence formatMessage(Conversation conversation, ConversationLegacy conversationLegacy) {
        String from = conversation.getRecipients().formatNames(", ");

        SpannableStringBuilder buf = new SpannableStringBuilder(from);

        if (conversation.getMessageCount() > 1 && mPrefs.getBoolean(SettingsFragment.MESSAGE_COUNT, false)) {
            int before = buf.length();
            buf.append(context.getResources().getString(R.string.message_count_format, conversation.getMessageCount()));
            buf.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.grey_light)), before, buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        if (conversationLegacy.hasDraft()) {
            buf.append(context.getResources().getString(R.string.draft_separator));
            int before = buf.length();
            buf.append(context.getResources().getString(R.string.has_draft));
            buf.setSpan(new ForegroundColorSpan(ThemeManager.getColor()), before, buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        return buf;
    }
}
