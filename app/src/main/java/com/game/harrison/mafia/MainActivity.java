package com.game.harrison.mafia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void pageChange(View v){
        String id_s=getResources().getResourceEntryName(v.getId());
        Intent intent;
        if(id_s.equalsIgnoreCase("mod"))
        {
            intent=new Intent(MainActivity.this,characterSelection.class);

        }
        else
        {
            intent=new Intent(MainActivity.this,about.class);
        }
        MainActivity.this.startActivity(intent);

    }


}
