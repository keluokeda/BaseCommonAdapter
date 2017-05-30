package com.keluokeda.basecommomadapter;


import android.view.View;

public interface BaseViewHolder<T> extends View.OnClickListener{
    void bindData(T t,int position);
}
