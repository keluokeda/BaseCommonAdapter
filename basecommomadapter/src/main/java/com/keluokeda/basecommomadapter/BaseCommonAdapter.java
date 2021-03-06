package com.keluokeda.basecommomadapter;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class BaseCommonAdapter<T> extends BaseAdapter implements OnChildViewClickListener<T> {
    private List<T> mTList;
    private OnChildViewClickListener<T> mOnChildViewClickListener;


    protected BaseCommonAdapter(List<T> list) {
        this.mTList = list == null ? new ArrayList<T>(0) : list;
    }

    @Override
    public int getCount() {
        return mTList.size();
    }

    @Override
    public T getItem(int position) {
        return mTList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder<T> baseViewHolder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), getItemResource(), null);
            baseViewHolder = createViewHolder(convertView);
            convertView.setTag(baseViewHolder);
        } else {
            baseViewHolder = (BaseViewHolder<T>) convertView.getTag();
        }

        baseViewHolder.bindData(getItem(position), position);
        return convertView;
    }


    protected abstract BaseViewHolder<T> createViewHolder(View convertView);


    protected abstract int getItemResource();

    public void addDataList(List<T> list) {
        mTList.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(T t) {
        mTList.add(t);
        notifyDataSetChanged();
    }

    public void remove(T t) {
        mTList.remove(t);
        notifyDataSetChanged();
    }

    public void insert(int position, T t) {
        mTList.add(position, t);
        notifyDataSetChanged();
    }

    public void clear() {
        mTList.clear();
        notifyDataSetChanged();
    }

    public void sort(Comparator<? super T> comparator) {
        Collections.sort(mTList, comparator);
        notifyDataSetChanged();
    }

    private static boolean enabled = true;

    private static final Runnable ENABLE_AGAIN = new Runnable() {
        @Override
        public void run() {
            enabled = true;
        }
    };

    @Override
    public void onChildViewClick(int position, T t, View view) {
        if (enabled) {
            enabled = false;
            view.post(ENABLE_AGAIN);
            if (mOnChildViewClickListener != null) {
                mOnChildViewClickListener.onChildViewClick(position, t, view);
            }
        }


    }

    public OnChildViewClickListener<T> getOnChildViewClickListener() {
        return mOnChildViewClickListener;
    }

    public void setOnChildViewClickListener(OnChildViewClickListener<T> onChildViewClickListener) {
        mOnChildViewClickListener = onChildViewClickListener;
    }
}
