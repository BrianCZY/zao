package com.hzu.zao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 *
 * Created by Nearby Yang on 2015-04-04.
 */
public class SearchDetailActivity extends Activity {
    private TextView Search_content;
    private  String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        Intent intent=getIntent();
        content=intent.getStringExtra("content");
        initView();
    }
    private void initView(){
        Search_content=(TextView)findViewById(R.id.search_detail);
        Search_content.setText(content);
    }
}
