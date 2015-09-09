package com.adeebnqo.Thula.ui.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import com.adeebnqo.Thula.common.TextViewUtils;

public class RobotoTextView extends AppCompatTextView {

    public RobotoTextView(Context context) {
        this(context, null);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            TextViewUtils.initTypeface(this, context, attrs);
        }
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            TextViewUtils.initTypeface(this, context, attrs);
        }
    }

}
