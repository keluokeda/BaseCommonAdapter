package com.keluokeda.basecommonadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.keluokeda.basecommomadapter.AdapterFactory;
import com.keluokeda.basecommomadapter.BaseCommonAdapter;
import com.keluokeda.basecommomadapter.OnChildViewClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView listView = (ListView) findViewById(R.id.lv_han);


        int max = 100;
        List<Han> hanList = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            String sign = UUID.randomUUID().toString();
            if (i % 3 == 0) {
                hanList.add(new Han(R.mipmap.han001, sign));
            } else if (i % 3 == 1) {
                hanList.add(new Han(R.mipmap.han002, sign));
            } else {
                hanList.add(new Han(R.mipmap.han003, sign));
            }
        }

        BaseCommonAdapter<Han> adapter = AdapterFactory.createAdapter(Han.class, hanList);
        adapter.setOnChildViewClickListener(new OnChildViewClickListener<Han>() {
            @Override
            public void onChildViewClick(int position, Han han, View view) {
                Toast.makeText(ListActivity.this, "position = "+position, Toast.LENGTH_SHORT).show();
            }
        });
        listView.setAdapter(adapter);
    }
}
