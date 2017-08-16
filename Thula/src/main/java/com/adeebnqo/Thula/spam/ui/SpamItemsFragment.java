package com.adeebnqo.Thula.spam.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.spam.SharedPreferenceSpamNumberStorage;
import com.adeebnqo.Thula.spam.SpamNumberStorage;
import com.adeebnqo.Thula.spam.ui.dummy.SpamContent;

public class SpamItemsFragment extends Fragment implements AbsListView.OnItemClickListener {

    private AbsListView mListView;
    private ListAdapter mAdapter;

    public static SpamItemsFragment newInstance() {
        SpamItemsFragment fragment = new SpamItemsFragment();
        return fragment;
    }

    public SpamItemsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spam_list_item, android.R.id.text1, SpamContent.ITEMS);

        SpamContent.loadContent(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spam_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.spamList);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(view.findViewById(R.id.emptyElement));

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Activity activity = getActivity();
        final String identifier = SpamContent.ITEMS.get(position).content;
        final SpamNumberStorage spamNumberStorage = new SharedPreferenceSpamNumberStorage(activity);

        new AlertDialog.Builder(activity)
                // set dialog icon
                .setIcon(android.R.drawable.ic_dialog_info)
                        // set Dialog Title
                .setTitle(getString(R.string.delete))
                        // Set Dialog Message
                .setMessage(getString(R.string.delete_item, identifier))
                        // positive button
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                spamNumberStorage.deleteNumber(identifier);
                                SpamContent.loadContent(getActivity());
                                ((ArrayAdapter) mAdapter).notifyDataSetChanged();
                            }
                        })
                                // negative button
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public void synchronizeSpamList(String phoneNumber){
        Log.d("foobar", "Updating with id = "+phoneNumber);
    }

    public void notifyCannotSync(){
        Log.d("foobar", "Cannot identify you");
    }
}
