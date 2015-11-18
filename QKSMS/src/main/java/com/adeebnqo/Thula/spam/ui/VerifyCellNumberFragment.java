package com.adeebnqo.Thula.spam.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.adeebnqo.Thula.R;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;

public class VerifyCellNumberFragment extends Fragment {

    public static final String IS_PHONE_NUMBER_SAVED = "pref_key_cell_number_saved";
    public static final String PHONE_NUMBER = "pref_key_cell_number";

    public static VerifyCellNumberFragment newInstance() {
        VerifyCellNumberFragment fragment = new VerifyCellNumberFragment();
        return fragment;
    }

    private AuthCallback authCallback;
    private CellPhoneVerifyListener verifyPhoneListener;

    public VerifyCellNumberFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // Do something with the session
                Activity context = getActivity();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putBoolean(IS_PHONE_NUMBER_SAVED, true);
                editor.putString(PHONE_NUMBER, phoneNumber);

                editor.apply();

                verifyPhoneListener.onVerified(phoneNumber);
            }

            @Override
            public void failure(DigitsException exception) {
                verifyPhoneListener.onFailed();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_cellnumber, container, false);

        DigitsAuthButton digitsButton = (DigitsAuthButton) view.findViewById(R.id.auth_button);
        digitsButton.setCallback(authCallback);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            verifyPhoneListener = (CellPhoneVerifyListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CellPhoneVerifyListener");
        }
    }

    public interface CellPhoneVerifyListener{
        void onVerified(String cellnumber);
        void onFailed();
    }
}
