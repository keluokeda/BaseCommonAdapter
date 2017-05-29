package com.keluokeda.basecommomadapter;

import android.widget.ImageView;


public class ImageViewValueBinder implements ValueBinder<ImageView, Integer> {
    @Override
    public void setValue(ImageView imageView, Integer integer) {
        imageView.setImageResource(integer);
    }
}
