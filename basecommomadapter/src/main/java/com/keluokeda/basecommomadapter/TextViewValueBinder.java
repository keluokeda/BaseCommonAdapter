package com.keluokeda.basecommomadapter;

import android.widget.TextView;


public class TextViewValueBinder implements ValueBinder<TextView, String> {
    @Override
    public void setValue(TextView textView, String charSequence) {
        textView.setText(charSequence);
    }
}
