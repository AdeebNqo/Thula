package com.adeebnqo.Thula.ui.base;

import android.app.Fragment;
import com.adeebnqo.Thula.ThulaApp;
import com.squareup.leakcanary.RefWatcher;

public class QKFragment extends Fragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ThulaApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
