package com.game.harrison.mafia;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.Random;

public class assign extends AppCompatActivity {
    int sum,rows,active_button=99,finish=0;//99 means its not selected on any button //finish to initiate viewswitcher
    int[] characters=new int[11];
    int remainder;
    String[] roles;
    String[] names;
    TextView reveal_role;
    ViewSwitcher show_start;
    TextView title_change_all_set;
    TextView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign);
        getData();
        createRoles();
        shuffle();
        CustomAdapter custom=new CustomAdapter();
        ListView d_names=(ListView)findViewById(R.id.name_select);
        d_names.setAdapter(custom);
        reveal_role=(TextView)findViewById(R.id.reveal);
        show_start= (ViewSwitcher) findViewById(R.id.reveal_start);
        title_change_all_set=(TextView)findViewById(R.id.change_title);
        help=(TextView)findViewById(R.id.help);
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return rows;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if(convertView==null)
            {
                holder=new ViewHolder();
                convertView=getLayoutInflater().inflate(R.layout.get_roles,null);
                holder.b1=(Button)convertView.findViewById(R.id.n1);
                holder.b2 =(Button)convertView.findViewById(R.id.n2);
                holder.b3=(Button)convertView.findViewById(R.id.n3);
                convertView.setTag(holder);
            }
            else
                holder=(ViewHolder)convertView.getTag();
            holder.ref=position;

            if(names[(position*3)]!=null)
            {
                holder.b1.setText(""+names[(position*3)]);
                holder.b1.setBackgroundResource(R.drawable.wood2);
            }
            else
            {
                holder.b1.setBackgroundColor(Color.parseColor("#00000000"));
            }
            if(names[(position*3)+1]!=null)
            {
                holder.b2.setText(""+names[(position*3)+1]);
                holder.b2.setBackgroundResource(R.drawable.wood2);
            }
            else
            {
                holder.b2.setBackgroundColor(Color.parseColor("#00000000"));
            }

            if(names[(position*3)+2]!=null)
            {
                holder.b3.setText(""+names[(position*3)+2]);
                holder.b3.setBackgroundResource(R.drawable.wood2);
            }
            else
            {
                holder.b3.setBackgroundColor(Color.parseColor("#00000000"));
            }


            holder.v1=(position*3);
            holder.v2=(position*3)+1;
            holder.v3=(position*3)+2;

            holder.b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(active_button==99 && holder.active1==1)
                    {
                        reveal_role.setText(""+roles[holder.v1]);
                        holder.b1.setBackgroundColor(Color.GREEN);
                        active_button = holder.v1;
                    }
                    else if(active_button==holder.v1 && holder.active1==1)
                    {
                        reveal_role.setText("");
                        holder.b1.setBackgroundResource(R.drawable.strike);
                        active_button=99;
                        holder.active1=0;
                        finish++;
                        check_finish();
                    }

                }
            });

            holder.b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(names[holder.v2]!=null)
                    {
                        if(active_button==99 && holder.active2==1)
                        {
                            reveal_role.setText(""+roles[holder.v2]);
                            holder.b2.setBackgroundColor(Color.GREEN);
                            active_button = holder.v2;
                        }
                        else if(active_button==holder.v2 && holder.active2==1)
                        {
                            reveal_role.setText("");
                            holder.b2.setBackgroundResource(R.drawable.strike);
                            active_button=99;
                            holder.active2=0;
                            finish++;
                            check_finish();
                        }

                    }

                }
            });
            holder.b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(names[holder.v3]!=null)
                    {
                        if(active_button==99 && holder.active3==1)
                        {
                            reveal_role.setText(""+roles[holder.v3]);
                            holder.b3.setBackgroundColor(Color.GREEN);
                            active_button = holder.v3;
                        }
                        else if(active_button==holder.v3 && holder.active3==1)
                        {
                            reveal_role.setText("");
                            holder.b3.setBackgroundResource(R.drawable.strike);
                            active_button=99;
                            holder.active3=0;
                            finish++;
                            check_finish();
                        }
                    }


                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        Button b1;
        Button b2;
        Button b3;
        int ref;
        int v1,v2,v3,active1=1,active2=1,active3=1;
    }

    public void check_finish()
    {
        if(finish==sum)
        {
            show_start.showNext();
            title_change_all_set.setText("ALL SET !");
            help.setText("");
        }

    }

    public void shuffle(){
        int index,index2;String temp1,temp2;
        Random random1 = new Random();
        Random random2 = new Random();

        for (int i = sum-1; i > 0; i--)
        {
            index = random1.nextInt(i+1);
            index2 = random2.nextInt(i+1);

            temp1 = roles[index];
            roles[index] = roles[i];
            roles[i] = temp1;

            temp2 = names[index2];
            names[index2] = names[i];
            names[i] = temp2;
        }
    }


    public void calc_rows(){
        int q=sum/3;
        remainder=sum%3;
        if(remainder==0)
            rows=q;
        else
            rows=q+1;

    }

    public void getData()
    {
        Intent intent=getIntent();
        sum=intent.getIntExtra("sum",0);
        calc_rows();
        for(int i=0;i<11;i++)
        {
            String key="char"+i;
            characters[i]=intent.getIntExtra(key,0);
        }
        names=new String[rows*3];
        for(int i=0;i<sum;i++)
        {
            String key="name"+i;
            names[i]=intent.getStringExtra(key);
        }
        roles=new String [rows*3];
    }


    public void createRoles()
    {   String[] role_names={"Witch","Mafia","King Mafia","Terrorist","Joker","Assassin","Detective","Angel","Sheela","Silencer","God father"};
        String[] color={"Black","Red","Green","Blue"};
        int index=0;
        for(int i=0;i<11;i++) {
            if(i==2)//king mafia
            {
                if(characters[i]==1)
                {
                    roles[index]=""+role_names[i];
                    index++;
                }
            }
            else if(i==9)//silencer
            {
                if(characters[i]==1)
                {
                    roles[index]=""+role_names[i];
                    index++;
                }
            }
            else if (characters[i] == 1) { //for characters with =1 participants
                roles[index] = ""+role_names[i];
                index++;
            }
            else if (characters[i] > 1) {
                for (int j = 0; j < characters[i]; j++) {
                    if (i != 1) {
                        roles[index] = "" + color[j] + " " + role_names[i]; //for all other char with >1 participants
                    } else
                        roles[index] = ""+role_names[i]; // for mafia..as we dont need color mafia's

                    index++;

                }

            }
        }
    }

    public void reorder()
    {   int index=0;
        String temp_roles,temp_names;
        String[] order_of_char={"witch","mafia","king","terrorist","joker","assassin","angel","detective","sheela","god","silencer"};
        for(int i=0;i<10;i++)
        //{
            for(int j=0;j<sum;j++)
            {
                if(i!=1)
                {
                    if(roles[j].toLowerCase().contains(order_of_char[i]))
                    {
                        temp_roles=roles[j];
                        roles[j]=roles[index];
                        roles[index]=temp_roles;

                        temp_names=names[j];
                        names[j]=names[index];
                        names[index]=temp_names;

                        index++;
                    }
                }
                else
                {
                    if(roles[j].equalsIgnoreCase(order_of_char[i]))
                    {
                        temp_roles=roles[j];
                        roles[j]=roles[index];
                        roles[index]=temp_roles;

                        temp_names=names[j];
                        names[j]=names[index];
                        names[index]=temp_names;

                        index++;
                    }
                }


            }
        //}
    }

    public void start(View view)
    {

        EditText kills=(EditText)findViewById(R.id.kills);
        EditText f_kills=(EditText)findViewById(R.id.f_kills);
        int k=Integer.parseInt(kills.getText().toString());
        int f_k=Integer.parseInt(f_kills.getText().toString());

        if(f_k<=k)
        {
            reorder();
            Intent intent=new Intent(assign.this,mod.class);
            intent.putExtra("sum",sum);
            intent.putExtra("k",k); //k is for kills
            intent.putExtra("f_k",f_k); //f_k is for first night kills
            for(int i=0;i<sum;i++)
            {
                String key="name"+i;
                intent.putExtra(key,names[i]);
            }
            for(int i=0;i<sum;i++)
            {
                String key="role"+i;
                intent.putExtra(key,roles[i]);
            }
            assign.this.startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Condition not satisfied",Toast.LENGTH_SHORT).show();
        }

    }
}
