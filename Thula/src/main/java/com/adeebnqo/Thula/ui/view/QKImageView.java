package com.adeebnqo.Thula.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.adeebnqo.Thula.interfaces.LiveView;
import com.adeebnqo.Thula.common.LiveViewManager;
import com.adeebnqo.Thula.ui.ThemeManager;
import com.adeebnqo.Thula.ui.settings.SettingsFragment;

public class QKImageView extends ImageView implements LiveView {

    private static final String TAG = "QKImageView";
    private Drawable mDrawable;

    public QKImageView(Context context) {
        super(context);
        init();
    }

    public QKImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QKImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        // Register this view for live updates.
        LiveViewManager.registerView(this);
        LiveViewManager.registerPreference(this, SettingsFragment.THEME);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        // Have to set this as null to refresh
        super.setImageDrawable(null);
        mDrawable = drawable;

        if (mDrawable != null) {
            mDrawable.setColorFilter(new PorterDuffColorFilter(ThemeManager.getColor(), PorterDuff.Mode.MULTIPLY));
            super.setImageDrawable(drawable);
        }
    }

    @Override
    public void refresh() {
        setImageDrawable(mDrawable);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
