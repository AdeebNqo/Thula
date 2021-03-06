package com.adeebnqo.Thula.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.view.ViewGroup;

import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.ui.ThemeManager;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.lang.reflect.Field;


public class DefaultSmsHelper implements ActionClickListener {
    public interface DefaultSmsResultListener {
        public void onDefaultSmsResult(boolean success);
    }

    private Context mContext;
    private int mMessage;
    private DefaultSmsResultListener mListener;
    private static long sLastShown;
    private boolean mIsDefault = true;

    // Listener is currently useless because we can't listen for response from the system dialog
    public DefaultSmsHelper(Context context, DefaultSmsResultListener listener, int messageRes) {
        mContext = context;
        mListener = listener;
        mMessage = messageRes != 0 ? messageRes : R.string.default_info;

        //some phones do not defaultSmsPackage, they will be assigned to null
        String defaultSmsPackage = null;
        if (Build.VERSION.SDK_INT >= 19) {
            defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(mContext);
        }

        if (Build.VERSION.SDK_INT < 19 || (defaultSmsPackage != null && defaultSmsPackage.equals
                (mContext.getPackageName()))) {
            if (mListener != null) {
                mListener.onDefaultSmsResult(true);
            }
            mIsDefault = true;
        } else {
            mIsDefault = false;
        }
    }

    public void showIfNotDefault(ViewGroup viewGroup) {
        if (!mIsDefault) {
            long deltaTime = (System.nanoTime() / 1000000) - sLastShown;
            long duration = deltaTime > 60 * 1000 ? 8000 : 3000;

            Snackbar snackBar = Snackbar.with(mContext)
                    .type(getSnackBarType())
                    .text(mMessage)
                    .duration(duration)
                    .actionColor(ThemeManager.getColor())
                    .actionLabel(R.string.upgrade_now)
                    .actionListener(this);

            if (viewGroup == null) {
                SnackbarManager.show(snackBar);
            } else {
                SnackbarManager.show(snackBar, viewGroup);
            }

            sLastShown = System.nanoTime() / 1000000;
        }
    }

    @Override
    public void onActionClicked(Snackbar snackbar) {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, mContext.getPackageName());
        mContext.startActivity(intent);

        if (mListener != null) {
            // TODO figure out how to get response from system dialog. Maybe set up BaseActivity and use startActivityForResult?
            // http://developer.android.com/training/basics/intents/result.html
        }
    }

    private SnackbarType getSnackBarType() {
        SnackbarType snackbarType = SnackbarType.MULTI_LINE;

        try {
            Field maxLines = SnackbarType.class.getDeclaredField("maxLines");
            maxLines.setAccessible(true);
            maxLines.setInt(snackbarType, 3);

            Field maxHeight = SnackbarType.class.getDeclaredField("maxHeight");
            maxHeight.setAccessible(true);
            maxHeight.setInt(snackbarType, 112);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return snackbarType;
    }
}
