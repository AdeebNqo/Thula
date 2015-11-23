package com.adeebnqo.Thula.ui.conversationlist;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.common.ConversationPrefsHelper;
import com.adeebnqo.Thula.common.LiveViewManager;
import com.adeebnqo.Thula.common.emoji.EmojiRegistry;
import com.adeebnqo.Thula.common.utils.DateFormatter;
import com.adeebnqo.Thula.data.Contact;
import com.adeebnqo.Thula.data.Conversation;
import com.adeebnqo.Thula.interfaces.LiveView;
import com.adeebnqo.Thula.ui.MainActivity;
import com.adeebnqo.Thula.ui.ThemeManager;
import com.adeebnqo.Thula.ui.base.RecyclerCursorAdapter;
import com.adeebnqo.Thula.ui.settings.SettingsFragment;
import com.google.gson.Gson;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ConversationListAdapter extends RecyclerCursorAdapter<ConversationListViewHolder, Conversation>
        implements LiveView {


    private final SharedPreferences mPrefs;

    private final Drawable mMuted;
    private final Drawable mUnread;
    private final Drawable mError;

    private Activity activity;
    public static final String SPAM = "pref_key_SPAM";
    private MaterialShowcaseView showcaseAddToSpamList;

    public ConversationListAdapter(Context context) {
        mContext = context;
        mPrefs = MainActivity.getPrefs(mContext);

        mMuted = ContextCompat.getDrawable(context, R.drawable.ic_mute);
        mUnread = ContextCompat.getDrawable(context, R.drawable.ic_unread);
        mError = ContextCompat.getDrawable(context, R.drawable.ic_error);

        LiveViewManager.registerView(this);
        LiveViewManager.registerPreference(this, SettingsFragment.THEME);
        refresh();
    }

    protected void setActivity(Activity activity) {
        if (activity != null) {
            this.activity = activity;
        }
    }

    protected Conversation getItem(int position) {
        mCursor.moveToPosition(position);
        return Conversation.from(mContext, mCursor);
    }

    @Override
    public ConversationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_conversation, null);
        return new ConversationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationListViewHolder holder, int position) {
        final Conversation conversation = getItem(position);

        holder.data = conversation;
        holder.context = mContext;
        holder.clickListener = mItemClickListener;
        holder.root.setOnClickListener(holder);
        holder.root.setOnLongClickListener(holder);

        // Have to clear the image drawable first, or else it won't reload it at all.
        holder.mutedView.setImageDrawable(null);
        holder.unreadView.setImageDrawable(null);
        holder.errorIndicator.setImageDrawable(null);
        holder.mutedView.setImageDrawable(mMuted);
        holder.unreadView.setImageDrawable(mUnread);
        holder.errorIndicator.setImageDrawable(mError);

        holder.mutedView.setVisibility(new ConversationPrefsHelper(mContext, conversation.getThreadId())
                .getNotificationsEnabled() ? View.GONE : View.VISIBLE);

        holder.errorIndicator.setVisibility(conversation.hasError() ? View.VISIBLE : View.GONE);

        if (conversation.hasUnreadMessages()) {
            holder.unreadView.setVisibility(View.VISIBLE);
            holder.snippetView.setTextColor(ThemeManager.getTextOnBackgroundPrimary());
            holder.dateView.setTextColor(ThemeManager.getColor());
        } else {
            holder.unreadView.setVisibility(View.GONE);
            holder.snippetView.setTextColor(ThemeManager.getTextOnBackgroundSecondary());
            holder.dateView.setTextColor(ThemeManager.getTextOnBackgroundSecondary());
        }

        if (isInMultiSelectMode()) {
            holder.mSelected.setVisibility(View.VISIBLE);
            if (isSelected(conversation.getThreadId())) {
                holder.mSelected.setImageResource(R.drawable.btn_radio_on);
                holder.mSelected.setColorFilter(ThemeManager.getColor());
            } else {
                holder.mSelected.setImageResource(R.drawable.btn_radio_off);
                holder.mSelected.setColorFilter(ThemeManager.getTextOnBackgroundSecondary());
            }
        } else {
            holder.mSelected.setVisibility(View.GONE);
        }

        if (mPrefs.getBoolean(SettingsFragment.HIDE_AVATAR_CONVERSATIONS, false)) {
            holder.mAvatarView.setVisibility(View.GONE);
        } else {
            holder.mAvatarView.setVisibility(View.VISIBLE);
        }

        // Date
        holder.dateView.setText(DateFormatter.getConversationTimestamp(mContext, conversation.getDate()));

        // Subject
        String emojiSnippet = conversation.getSnippet();
        if (mPrefs.getBoolean(SettingsFragment.AUTO_EMOJI, false)) {
            emojiSnippet = EmojiRegistry.parseEmojis(emojiSnippet);
        }
        holder.snippetView.setText(emojiSnippet);

        Contact.addListener(holder);

        // Update the avatar and name
        holder.onUpdate(conversation.getRecipients().size() == 1 ? conversation.getRecipients().get(0) : null);

        if (activity != null && position == 0) {
            showcaseAddToSpamList  = new MaterialShowcaseView.Builder(activity)
                    .setTarget(holder.mAvatarView)
                    .setDismissText(activity.getString(R.string.showcase_done))
                    .setContentText(activity.getString(R.string.showcase_add_spam))
                    .singleUse(SPAM)
                    .show();
        }
    }

    @Override
    public void refresh() {
        mMuted.setColorFilter(ThemeManager.getColor(), PorterDuff.Mode.MULTIPLY);
        mUnread.setColorFilter(ThemeManager.getColor(), PorterDuff.Mode.MULTIPLY);
        mError.setColorFilter(ThemeManager.getColor(), PorterDuff.Mode.MULTIPLY);
        notifyDataSetChanged();
    }
}
