package com.adeebnqo.Thula.ui.search;

import android.view.View;
import com.adeebnqo.Thula.R;
import com.adeebnqo.Thula.ui.base.ClickyViewHolder;
import com.adeebnqo.Thula.ui.view.AvatarView;
import com.adeebnqo.Thula.ui.view.QKTextView;

public class SearchViewHolder extends ClickyViewHolder<SearchData> {

    protected View root;
    protected AvatarView avatar;
    protected QKTextView name;
    protected QKTextView date;
    protected QKTextView snippet;

    public SearchViewHolder(View view) {
        super(view);

        root = view;
        avatar = (AvatarView) view.findViewById(R.id.search_avatar);
        name = (QKTextView) view.findViewById(R.id.search_name);
        date = (QKTextView) view.findViewById(R.id.search_date);
        snippet = (QKTextView) view.findViewById(R.id.search_snippet);
    }
}
