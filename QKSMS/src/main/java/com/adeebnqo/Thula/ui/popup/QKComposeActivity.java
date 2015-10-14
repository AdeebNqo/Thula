package com.adeebnqo.Thula.ui.popup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.adeebnqo.Thula.ui.ThemeManager;
import com.adeebnqo.Thula.ui.base.QKActivity;
import com.adeebnqo.Thula.ui.view.QKFrameLayout;
import com.adeebnqo.Thula.ui.view.QKLinearLayout;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;
import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.data.Contact;
import com.adeebnqo.Thula.data.ContactHelper;
import com.adeebnqo.Thula.interfaces.ActivityLauncher;
import com.adeebnqo.Thula.interfaces.RecipientProvider;
import com.adeebnqo.Thula.common.utils.KeyboardUtils;
import com.adeebnqo.Thula.ui.base.QKPopupActivity;
import com.adeebnqo.Thula.ui.view.AutoCompleteContactView;
import com.adeebnqo.Thula.ui.view.ComposeView;
import com.adeebnqo.Thula.ui.view.StarredContactsView;

import java.net.URLDecoder;

public class QKComposeActivity extends QKActivity implements ComposeView.OnSendListener, RecipientProvider,
        ActivityLauncher, AdapterView.OnItemClickListener {

    private final String TAG = "QKComposeActivity";

    private Context mContext = this;
    private AutoCompleteContactView mRecipients;
    private StarredContactsView mStarredContactsView;
    private ComposeView mCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        ThemeManager.loadThemeProperties(this);

        ((QKFrameLayout) findViewById(R.id.popup)).setBackgroundTint(ThemeManager.getBackgroundColor());

        View title = findViewById(R.id.title);
        if (title != null && title instanceof AppCompatTextView) {
            title.setVisibility(View.GONE);
        }

        setTitle(R.string.title_compose);

        mRecipients = (AutoCompleteContactView) findViewById(R.id.compose_recipients);
        mRecipients.setOnItemClickListener(this);

        // Get the compose view, and set it up with all the listeners.
        findViewById(R.id.compose_view_stub).setVisibility(View.VISIBLE);
        mCompose = (ComposeView) findViewById(R.id.compose_view);
        mCompose.setActivityLauncher(this);
        mCompose.setOnSendListener(this);
        mCompose.setRecipientProvider(this);
        mCompose.setLabel("QKCompose");

        mStarredContactsView = (StarredContactsView) findViewById(R.id.starred_contacts);
        mStarredContactsView.setComposeScreenViews(mRecipients, mCompose);

        // Apply different attachments based on the type.
        mCompose.loadMessageFromIntent(getIntent());

        // Check for {sms,mms}{,to}: schemes, in which case we can set the recipients.
        if (getIntent().getData() != null) {
            String data = getIntent().getData().toString();
            String scheme = getIntent().getData().getScheme();

            if (scheme.startsWith("smsto") || scheme.startsWith("mmsto")) {
                String address = data.replace("smsto:", "").replace("mmsto:", "");
                final Contact contact = Contact.get(formatPhoneNumber(address), true);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecipients.submitItem(contact.getName(), contact.getNumber(),
                                ContactHelper.getPhotoUri(mContext, contact.getUri()));
                    }
                }, 100);

            } else if (scheme.startsWith("sms") || (scheme.startsWith("mms"))) {
                String address = data.replace("sms:", "").replace("mms:", "");
                final Contact contact = Contact.get(formatPhoneNumber(address), true);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecipients.submitItem(contact.getName(), contact.getNumber(),
                                ContactHelper.getPhotoUri(mContext, contact.getUri()));
                    }
                }, 100);

            }

            mCompose.requestReplyTextFocus();
        }
    }

    protected int getLayoutResource() {
        return R.layout.activity_qkcompose;
    }

    private String formatPhoneNumber(String address) {
        address = URLDecoder.decode(address);
        address = "" + Html.fromHtml(address);
        address = PhoneNumberUtils.formatNumber(address);
        return address;
    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.hide(mContext, mCompose);
    }

    @Override
    public void finish() {
        // Override pending transitions so that we don't see black for a second when QuickReply closes
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public String[] getRecipientAddresses() {
        DrawableRecipientChip[] chips = mRecipients.getRecipients();
        String[] addresses = new String[chips.length];

        for (int i = 0; i < chips.length; i++) {
            addresses[i] = PhoneNumberUtils.stripSeparators(chips[i].getEntry().getDestination());
        }

        return addresses;
    }

    @Override
    public void onSend(String[] recipients, String body) {
        // When the SMS is sent, close this activity.
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!mCompose.onActivityResult(requestCode, resultCode, data)) {
            // Handle other results here, since the ComposeView didn't handle them.
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mRecipients.onItemClick(parent, view, position, id);
        mStarredContactsView.collapse();
        mCompose.requestReplyTextFocus();
    }
}
