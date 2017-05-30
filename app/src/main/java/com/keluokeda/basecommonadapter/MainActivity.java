package com.keluokeda.basecommonadapter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.keluokeda.basecommomadapter.BaseCommonAdapter;
import com.keluokeda.basecommomadapter.AdapterFactory;
import com.keluokeda.basecommomadapter.OnChildViewClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }

    public void list(View view){
        startActivity(new Intent(this,ListActivity.class));
    }

    public void grid(View view){

    }
}
