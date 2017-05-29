package com.keluokeda.basecommonadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.keluokeda.basecommomadapter.BaseCommonAdapter;
import com.keluokeda.basecommomadapter.AdapterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.lv_user);


        List<User> users = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            String name = String.valueOf(i);
            String sign = UUID.randomUUID().toString();
            User user = new User(name, sign);
            users.add(user);
        }

        BaseCommonAdapter<User> baseCommonAdapter = AdapterFactory.createAdapter(User.class, users);
        listView.setAdapter(baseCommonAdapter);


    }
}
