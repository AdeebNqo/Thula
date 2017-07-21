package com.adeebnqo.Thula.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.adeebnqo.Thula.R;

public class QKFrameLayout extends FrameLayout{
    private final String TAG = "QKFrameLayout";

    private int mBackgroundTint = 0xFFFFFFFF;

    public QKFrameLayout(Context context) {
        super(context);
        init(context, null);
    }

    public QKFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QKLinearLayout);

            for (int i = 0; i < a.length(); i++) {
                int attr = a.getIndex(i);

                switch (attr) {
                    case R.styleable.QKLinearLayout_backgroundTint:
                        mBackgroundTint = a.getInt(i, 0xFFFFFFFF);
                        break;
                }
            }

            if (Build.VERSION.SDK_INT < 16) {
                setBackgroundDrawable(getBackground());
            } else {
                setBackground(getBackground());
            }

            a.recycle();
        }
    }

    public void setBackgroundTint(int backgroundTint) {
        if (mBackgroundTint != backgroundTint) {
            mBackgroundTint = backgroundTint;

            if (Build.VERSION.SDK_INT < 16) {
                setBackgroundDrawable(getBackground());
            } else {
                setBackground(getBackground());
            }
        }
    }

    @TargetApi(16)
    @Override
    public void setBackground(Drawable background) {
        background.mutate().setColorFilter(mBackgroundTint, PorterDuff.Mode.MULTIPLY);
        super.setBackground(background);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        background.mutate().setColorFilter(mBackgroundTint, PorterDuff.Mode.MULTIPLY);
        super.setBackgroundDrawable(background);
    }
}
