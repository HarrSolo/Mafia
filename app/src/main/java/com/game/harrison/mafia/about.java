package com.game.harrison.mafia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Harrison on 15-01-2018.
 */

public class about extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }

    public void pageChange(View v){
        String id_s=getResources().getResourceEntryName(v.getId());
        Intent intent;
        if(id_s.equalsIgnoreCase("characters"))
        {
            intent=new Intent(about.this,characters.class);

        }
        else
        {
            intent=new Intent(about.this,about.class);
        }
        about.this.startActivity(intent);

    }
}
