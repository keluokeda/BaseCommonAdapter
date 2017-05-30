package com.keluokeda.basecommonadapter;


import android.widget.ImageView;
import android.widget.TextView;

import com.keluokeda.Bind;
import com.keluokeda.Item;
import com.keluokeda.basecommomadapter.ImageViewValueBinder;
import com.keluokeda.basecommomadapter.TextViewValueBinder;

@Item(resource = R.layout.item_han)
public class Han {
    private Integer resource;
    private String name;

    public Han(Integer resource, String name) {
        this.resource = resource;
        this.name = name;
    }

    @Bind(viewId = R.id.iv_han, binderClass = ImageViewValueBinder.class, viewClass = ImageView.class)
    public Integer getResource() {
        return resource;
    }

    @Bind(viewId = R.id.tv_sign, binderClass = TextViewValueBinder.class, viewClass = TextView.class, click = true)
    public String getName() {
        return name;
    }
}
