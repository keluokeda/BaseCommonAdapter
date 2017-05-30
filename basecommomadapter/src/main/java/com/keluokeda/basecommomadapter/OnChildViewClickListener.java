package com.keluokeda.basecommomadapter;


import android.view.View;

public interface OnChildViewClickListener<T> {
    void onChildViewClick(int position, T t, View view);
}
