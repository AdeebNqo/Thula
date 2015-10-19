package com.adeebnqo.Thula.ui.compose;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.ex.chips.recipientchip.DrawableRecipientChip;
import com.adeebnqo.Thula.mmssms.Utils;
import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.interfaces.ActivityLauncher;
import com.adeebnqo.Thula.interfaces.RecipientProvider;
import com.adeebnqo.Thula.common.utils.KeyboardUtils;
import com.adeebnqo.Thula.common.utils.PhoneNumberUtils;
import com.adeebnqo.Thula.ui.MainActivity;
import com.adeebnqo.Thula.ui.base.QKContentFragment;
import com.adeebnqo.Thula.ui.view.AutoCompleteContactView;
import com.adeebnqo.Thula.ui.view.ComposeView;
import com.adeebnqo.Thula.ui.view.StarredContactsView;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ComposeFragment extends QKContentFragment implements ActivityLauncher, RecipientProvider,
        ComposeView.OnSendListener, AdapterView.OnItemClickListener, MainActivity.OnBackPressedListener {

    private final String TAG = "ComposeFragment";

    /**
     * Set to true in the bundle if the ComposeFragment should show the keyboard. Defaults to false.
     */
    public static final String ARG_SHOW_KEYBOARD = "showKeyboard";

    /**
     * Set a FOCUS string to indicate where the focus should be for the keyboard. Defaults to
     * FOCUS_NOTHING.
     */
    public static final String ARG_FOCUS = "focus";

    public static final String FOCUS_NOTHING = "nothing";
    public static final String FOCUS_RECIPIENTS = "recipients";
    public static final String FOCUS_REPLY = "reply";

    private Context mContext;

    private AutoCompleteContactView mRecipients;
    private ComposeView mComposeView;
    private StarredContactsView mStarredContactsView;

    private FrameLayout mComposeButton;
    private MaterialShowcaseView showcaseViewAttach;
    private View activityRootView;

    public static final String ATTACH = "pref_key_attach8hasdaxasaasaddqwassadsasdaasddsaasdasdsadfasdsadasfrefasassaddsaddaasaaadaaasddaads";

    // True if the fragment's arguments have changed, and we need to potentially perform a focus
    // operation when the fragment opens.
    private boolean mPendingFocus = false;

    /**
     * Returns a new ComposeFragment, configured with the args.
     *
     * @param args A Bundle with options for configuring this fragment. See the ARG_ constants for
     *             configuration options.
     * @return the new ComposeFragment
     */
    public static ComposeFragment getInstance(Bundle args) {
        return getInstance(args, null);
    }

    /**
     * Returns a ComposeFragment, configured with the args. If possible, the given fragment
     * is used instead of creating a new ComposeFragment.
     *
     * @param args A Bundle with options for configuring this fragment. See the ARG_ constants for
     *             configuration options.
     * @param reuseFragment A fragment that can be used instead of creating a new one.
     * @return the ComposeFragment, which may be recycled
     */
    public static ComposeFragment getInstance(Bundle args, Fragment reuseFragment) {
        ComposeFragment f;

        // Check if we can reuse the passed fragment.
        if (reuseFragment != null && reuseFragment instanceof ComposeFragment) {
            f = (ComposeFragment)reuseFragment;
        } else {
            f = new ComposeFragment();
        }

        // Set the arguments in this fragment.
        f.updateArguments(args);

        return f;
    }

    @Override
    public void onNewArguments() {
        // Set pending focus, because the new configuration means that we may need to focus.
        mPendingFocus = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_compose, container, false);

        mRecipients = (AutoCompleteContactView) view.findViewById(R.id.compose_recipients);

        activityRootView = view.findViewById(R.id.compose_root_container);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Activity activity = getActivity();
                if (activity != null) {
                    int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int height = size.y;

                    if (heightDiff > height / 2) {
                        if (mRecipients.hasFocus()) {
                            mComposeView.setVisibility(View.INVISIBLE);
                        }
                        if (showcaseViewAttach != null && showcaseViewAttach.isShown()) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        }
                    } else {
                        mComposeView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mRecipients.setOnItemClickListener(this);

        view.findViewById(R.id.compose_view_stub).setVisibility(View.VISIBLE);
        mComposeView = (ComposeView) view.findViewById(R.id.compose_view);
        mComposeButton = (FrameLayout) view.findViewById(R.id.compose_button);
        mComposeView.setActivityLauncher(this);
        mComposeView.setRecipientProvider(this);
        mComposeView.setOnSendListener(this);
        mComposeView.setLabel("Compose");

        mStarredContactsView = (StarredContactsView) view.findViewById(R.id.starred_contacts);
        mStarredContactsView.setComposeScreenViews(mRecipients, mComposeView);

        setHasOptionsMenu(true);

        mRecipients.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (showcaseViewAttach != null && showcaseViewAttach != null && showcaseViewAttach.isShown()) {
                    mRecipients.clearFocus();
                }
            }
        });

        Activity activity = getActivity();
        if (activity instanceof  MainActivity) {
            ((MainActivity) activity).setOnBackPressedListener(this);
        }

        return view;
    }

    public void revealShowcaseOfAttachmentView() {
        Activity activity = getActivity();
        if (activity != null && mComposeButton!=null) {

            showcaseViewAttach = new MaterialShowcaseView.Builder(activity)
                    .setTarget(mComposeButton)
                    .setDismissText(getString(R.string.showcase_done))
                    .setContentText(getString(R.string.showcase_attach))
                    .singleUse(ATTACH)
                    .show();
        }
    }

    @Override
    public boolean onBack() {
        if (showcaseViewAttach != null && showcaseViewAttach.isShown()){
            showcaseViewAttach.hide();
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean handledByComposeView = mComposeView.onActivityResult(requestCode, resultCode, data);
        if (!handledByComposeView) {
            // ...
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_attach:
                mComposeView.showAttachPanel();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSend(String[] recipients, String body) {
        long threadId = Utils.getOrCreateThreadId(mContext, recipients[0]);
        if (recipients.length == 1) {
            ((MainActivity) mContext).setConversation(threadId);
        } else {
            ((MainActivity) mContext).showMenu();
        }
    }

    @Override
    protected void onContentOpened() {
        setupInput();
    }

    /**
     * Shows the keyboard and focuses on the recipients text view.
     */
    public void setupInput() {
        Bundle args = getArguments() == null ? new Bundle() : getArguments();

        if (mPendingFocus) {
            if (args.getBoolean(ARG_SHOW_KEYBOARD, false)) {
                KeyboardUtils.show(mContext);
            }

            String focus = args.getString(ARG_FOCUS, FOCUS_NOTHING);
            if (FOCUS_RECIPIENTS.equals(focus)) {
                mRecipients.requestFocus();
            } else if (FOCUS_REPLY.equals(focus)) {
                mComposeView.requestReplyTextFocus();
            }
        }
    }

    @Override
    protected void onContentClosing() {
        // Clear the focus from this fragment.
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            getActivity().getCurrentFocus().clearFocus();
        }
    }

    /**
     * @return the addresses of all the contacts in the AutoCompleteContactsView.
     */
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mRecipients.onItemClick(parent, view, position, id);
        mStarredContactsView.collapse();
        mComposeView.requestReplyTextFocus();
    }
}
