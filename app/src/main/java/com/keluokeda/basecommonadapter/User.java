package com.keluokeda.basecommonadapter;


import com.keluokeda.Bind;
import com.keluokeda.Item;

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


    @Bind(targetId = R.id.tv_name, binderClassName = "com.keluokeda.basecommomadapter.TextViewValueBinder", viewClassName = "android.widget.TextView")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bind(targetId = R.id.tv_sign, binderClassName = "com.keluokeda.basecommomadapter.TextViewValueBinder", viewClassName = "android.widget.TextView")
    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
