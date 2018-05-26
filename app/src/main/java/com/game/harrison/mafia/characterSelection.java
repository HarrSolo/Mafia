package com.game.harrison.mafia;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.ArrayList;
import java.util.Random;

public class characterSelection extends AppCompatActivity  {

    int[] characters={0,1,1,0,0,0,0,0,0,0,0};int flag=1;
    String[] names=new String[20];
    int sum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_selection);
        for(int i=0;i<20;i++)
            names[i]="";

        finalize();
    }

    class CustomAdapter extends BaseAdapter //Adapter for creating listview
    {
        @Override
        public int getCount() {
            return sum;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if(convertView==null) {
                holder=new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.get_names, null);
                holder.textView1= (TextView) convertView.findViewById(R.id.one);
                holder.editText1 = (EditText) convertView.findViewById(R.id.two);
                convertView.setTag(holder);
            }
            else
                holder=(ViewHolder)convertView.getTag();
            holder.ref=position;

            holder.textView1.setText(""+(position+1));
            {
                holder.editText1.setText(""+names[position]);
            }

            holder.editText1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                         names[holder.ref]=s.toString();
                }
            });


            return convertView;
        }
    }

    private class ViewHolder { //To hold the listview items permanently
        TextView textView1;
        EditText editText1;
        int ref;
    }

    void refresh_names(int s) //refresh the names list when the sum(no. of participants) is changed
    {
        for(int i=(names.length-1);i>=s;i--)
        {
            //names[i]="Player "+(i+1);
            names[i]="";
        }
    }


    public void finalize() //calculate total current participants and update parallely
    {
        sum=0;
        for(int i=0;i<11;i++)
         sum+=characters[i];
        TextView total=(TextView)findViewById(R.id.total);
        total.setText("TOTAL PLAYERS : "+sum);
    }

    public void ButtonClick(View v) //respond to button click to add or subtract participants per role
    {
        String id_s=getResources().getResourceEntryName(v.getId());
        int id=Integer.parseInt(id_s.replaceAll("[^0-9]",""));
        String action=id_s.replaceAll("[0-9]","");
        if(action.equals("positive")) {
            if(sum<20)
            {
                if (id != 2 && id!=9)//not kingmafia and silencer
                {
                    if(characters[id]<4)
                        characters[id]++;
                    else
                        Toast.makeText(getApplicationContext(),"Cannot exceed 4 participants per role",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (characters[id] == 0)//its silencer or king-mafia...increase only when 0(0->1)
                        characters[id]++;
                    else
                        Toast.makeText(getApplicationContext(),"Maximum number of Silencers : 1",Toast.LENGTH_SHORT).show();
                }
            }
            else
                Toast.makeText(getApplicationContext(),"Max Limit : 20 Participants",Toast.LENGTH_SHORT).show();

        }
        else if(action.equals("negative")){
            if(id==1)
            {
                if(characters[id]!=1)
                characters[id]--;
                else
                    Toast.makeText(getApplicationContext(),"There must be a minimum of 1 Mafia",Toast.LENGTH_SHORT).show();
            }
            else if(characters[id]!=0)
                characters[id]--;
        }
            int resourceId = this.getResources().getIdentifier("text" + id, "id", this.getPackageName());
            TextView t = (TextView) findViewById(resourceId);
            t.setText("" + characters[id]);
            finalize();
    }

    public void switcher(View v) //perform switch b/e the two views
    {
        String action=getResources().getResourceEntryName(v.getId());
        ViewSwitcher tab= (ViewSwitcher) findViewById(R.id.tab);
        Button c=(Button)findViewById(R.id.c);
        Button p=(Button)findViewById(R.id.p);

        if(action.equals("c") && flag!=1)
        {
            c.setBackgroundColor(Color.parseColor("#FF7C00"));
            c.setTextColor(Color.parseColor("#000000"));
            p.setBackgroundResource(R.drawable.wood1);
            p.setTextColor(Color.parseColor("#FF7C00"));
            flag=1;
            tab.showNext();


        }
        else if(action.equals("p") && flag!=2)
        {
            p.setBackgroundColor(Color.parseColor("#FF7C00"));
            p.setTextColor(Color.parseColor("#000000"));
            c.setBackgroundResource(R.drawable.wood1);
            c.setTextColor(Color.parseColor("#FF7C00"));
            CustomAdapter cus=new CustomAdapter();
            flag=2;
            refresh_names(sum);
            ListView names=(ListView)findViewById(R.id.names);
            names.setAdapter(cus);
            tab.showNext();

        }

    }




    public void start(View v) //to move to next activity
    {

        Intent intent=new Intent(characterSelection.this,assign.class);
        for(int i=0;i<11;i++)
        {
            String key="char"+i;
            intent.putExtra(key,characters[i]);
        }
        for(int i=0;i<sum;i++)
        {
            String key="name"+i;
            intent.putExtra(key,names[i]);
        }
        intent.putExtra("sum",sum);
        this.startActivity(intent);

    }


}