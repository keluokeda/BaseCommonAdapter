package com.keluokeda.basecommonadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.keluokeda.basecommomadapter.AdapterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        GridView gridView = (GridView) findViewById(R.id.gv_user);

        int max = 100;
        List<User> users = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            users.add(new User(String.valueOf(i), UUID.randomUUID().toString()));
        }

        gridView.setAdapter(AdapterFactory.createAdapter(User.class,users));
    }
}
