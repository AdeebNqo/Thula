package com.adeebnqo.Thula.ui.base;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.adeebnqo.Thula.common.utils.CursorUtils;

import java.util.ArrayList;

public abstract class RecyclerCursorAdapter<VH extends RecyclerView.ViewHolder, DataType>
        extends RecyclerView.Adapter<VH> {

    public interface ItemClickListener<DataType> {
        void onItemClick(DataType object, View view);

        void onItemLongClick(DataType object, View view);
    }

    public interface MultiSelectListener {
        void onMultiSelectStateChanged(boolean enabled);
    }

    protected Context mContext;
    protected Cursor mCursor;

    protected ArrayList<Long> mSelectedItems = new ArrayList<>();

    protected ItemClickListener<DataType> mItemClickListener;
    protected RecyclerCursorAdapter.MultiSelectListener mMultiSelectListener;

    public void setItemClickListener(ItemClickListener<DataType> conversationClickListener) {
        mItemClickListener = conversationClickListener;
    }

    public void setMultiSelectListener(RecyclerCursorAdapter.MultiSelectListener multiSelectListener) {
        mMultiSelectListener = multiSelectListener;
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    private Cursor swapCursor(Cursor cursor) {
        if (mCursor == cursor) {
            return null;
        }

        Cursor oldCursor = mCursor;
        mCursor = cursor;
        if (cursor != null) {
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    protected abstract DataType getItem(int position);

    public boolean isInMultiSelectMode() {
        return mSelectedItems.size() > 0;
    }

    public ArrayList<Long> getSelectedItems() {
        return mSelectedItems;
    }

    public void disableMultiSelectMode(boolean requestCallback) {
        if (isInMultiSelectMode()) {
            mSelectedItems.clear();
            notifyDataSetChanged();

            if (requestCallback && mMultiSelectListener != null) {
                mMultiSelectListener.onMultiSelectStateChanged(false);
            }
        }
    }

    public boolean isSelected(long threadId) {
        return mSelectedItems.contains(threadId);
    }

    public void setSelected(long threadId) {
        if (!mSelectedItems.contains(threadId)) {
            mSelectedItems.add(threadId);
            notifyDataSetChanged();

            if (mSelectedItems.size() == 1 && mMultiSelectListener != null) {
                mMultiSelectListener.onMultiSelectStateChanged(true);
            }
        }
    }

    public void setUnselected(long threadId) {
        if (mSelectedItems.contains(threadId)) {
            mSelectedItems.remove(threadId);
            notifyDataSetChanged();

            if (mSelectedItems.size() == 0 && mMultiSelectListener != null) {
                mMultiSelectListener.onMultiSelectStateChanged(false);
            }
        }
    }

    public void toggleSelection(long threadId) {
        if (isSelected(threadId)) {
            setUnselected(threadId);
        } else {
            setSelected(threadId);
        }
    }

    @Override
    public int getItemCount() {
        return CursorUtils.isValid(mCursor) ? mCursor.getCount() : 0;
    }
}
