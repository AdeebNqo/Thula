package com.adeebnqo.Thula.ui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.MultiAutoCompleteTextView;
import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.adeebnqo.Thula.interfaces.LiveView;
import com.adeebnqo.Thula.common.FontManager;
import com.adeebnqo.Thula.common.LiveViewManager;
import com.adeebnqo.Thula.common.TypefaceManager;
import com.adeebnqo.Thula.ui.MainActivity;
import com.adeebnqo.Thula.ui.ThemeManager;
import com.adeebnqo.Thula.ui.settings.SettingsFragment;

public class AutoCompleteContactView extends RecipientEditTextView implements LiveView {
    public static final String TAG = "AutoCompleteContactView";

    private Context mContext;
    private BaseRecipientAdapter mAdapter;

    public AutoCompleteContactView(Context context) {
        this(context, null);
        init(context);
    }

    public AutoCompleteContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        SharedPreferences prefs = MainActivity.getPrefs(context);

        // Setup text size, typeface, etc.
        refresh();

        mAdapter = new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, getContext());
        mAdapter.setShowMobileOnly(prefs.getBoolean(SettingsFragment.MOBILE_ONLY, false));

        setThreshold(1);
        setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        setAdapter(mAdapter);
        setOnItemClickListener(this);

        // Register this view for live updates.
        LiveViewManager.registerView(this);
        LiveViewManager.registerPreference(this, SettingsFragment.FONT_FAMILY);
        LiveViewManager.registerPreference(this, SettingsFragment.FONT_SIZE);
        LiveViewManager.registerPreference(this, SettingsFragment.FONT_WEIGHT);
        LiveViewManager.registerPreference(this, SettingsFragment.MOBILE_ONLY);
        LiveViewManager.registerPreference(this, SettingsFragment.BACKGROUND);
    }

    @Override
    public void refresh() {
        setTypeface(TypefaceManager.obtainTypeface(mContext, FontManager.getFontFamily(mContext),
                FontManager.getFontWeight(mContext, false), TypefaceManager.TextStyle.NORMAL));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, FontManager.getTextSize(mContext, FontManager.TEXT_TYPE_PRIMARY));

        setTextColor(ThemeManager.getTextOnBackgroundPrimary());
        setHintTextColor(ThemeManager.getTextOnBackgroundSecondary());

        if (mAdapter != null) {
            SharedPreferences prefs = MainActivity.getPrefs(mContext);
            mAdapter.setShowMobileOnly(prefs.getBoolean(SettingsFragment.MOBILE_ONLY, false));
        }
    }
}
