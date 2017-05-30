package com.keluokeda.basecommonadapter;


import android.widget.TextView;

import com.keluokeda.Bind;
import com.keluokeda.Item;
import com.keluokeda.basecommomadapter.TextViewValueBinder;

@Item(resource = R.layout.item_user)
public class User {
    private String name;
    private String sign;

    public User() {
    }

    public User(String name, String sign) {
        this.name = name;
        this.sign = sign;
    }


    @Bind(viewId = R.id.tv_name, binderClass = TextViewValueBinder.class,viewClass = TextView.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bind(viewId = R.id.tv_sign,binderClass = TextViewValueBinder.class,viewClass = TextView.class)
    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
