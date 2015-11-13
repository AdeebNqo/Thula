package com.adeebnqo.Thula.ui.base;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.ui.ThemeManager;
import com.adeebnqo.Thula.ui.settings.SettingsFragment;
import com.adeebnqo.Thula.ui.view.QKFrameLayout;
import com.adeebnqo.Thula.ui.view.QKLinearLayout;

public abstract class QKPopupActivity extends QKActivity {

    protected SharedPreferences mPrefs;
    protected Resources mRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mRes = getResources();

        setFinishOnTouchOutside(mPrefs.getBoolean(SettingsFragment.QUICKREPLY_TAP_DISMISS, true));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setContentView(getLayoutResource());
        ThemeManager.loadThemeProperties(this);

        ((QKFrameLayout) findViewById(R.id.popup)).setBackgroundTint(ThemeManager.getBackgroundColor());

        View title = findViewById(R.id.title);
        if (title != null && title instanceof AppCompatTextView) {
            title.setVisibility(View.GONE);
        }
    }

    protected abstract int getLayoutResource();
}
