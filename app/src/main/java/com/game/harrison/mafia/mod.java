package com.game.harrison.mafia;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

/**
 * Created by Harrison on 29-11-2017.
 */


public class mod extends AppCompatActivity {

    int sum,rows,kills,first_night_kills,roles_display_count,current_round_death_count=0,end=0,summary_mode=0;
    int m_kills,km_kills,day=1,mafia_target_count=-1,king_mafia_target_count=-1,mafia_count=0,isNight=1;
    String active_role=null;int active_role_index=99;//the index of the active_role in roles_display array
    String[] deathNames;
    String[] roles; //holds the roles selected
    String[] names; //hold the names of the participants
    ArrayList<String> summary_details = new ArrayList<String>();
    int[] target;  //holds the target of each participant in the round
    int[] target_of_mafia;//separate target for mafia as they can choose multiple people
    int[] target_of_king_mafia;//separate target for ing mafia as he can also choose multiple people
    int[] active_person_status;//if the participant is dead or active
    int[] current_round_death;
    int[] active_role_status;
    int[] unvisited_character;
    int eliminate=99;
    String[] action;//the action selected by each role
    String [] roles_display; //roles with redundant mafia
    int[] name_index_of_roles_display;
    int[] name_index_mafia={99,99,99,99};
    //view initialization and adapter initialization for both the list-views
    ListView role_name,p_name,deaths,summary;CustomAdapterRoles adapter_roles;CustomAdapterNames adapter_names;CustomAdapterSummary adapter_summary;
    ViewSwitcher options_tab,info_tab;TextView mafia,civilian,gf,silenced_player,info_day;CustomAdapterDeath death_names_adapter;
    //Initialization of the three action buttons
    Button b1,b2,b3,thumbs,eliminate_player,mod_kill;TextView night;SlidingUpPanelLayout s;
    String[] r={"Witch","Mafia","King Mafia","Terrorist","Assassin","Detective","Angel","Sheela","Silencer","God father"};
    String[] action_preset={"reverses","kills","kills","targets","kills","detects","protects","goes with","silences","kills"};
    int[] img={R.drawable.witch,R.drawable.kill,R.drawable.kill,R.drawable.terrorist,R.drawable.kill,R.drawable.detective,
            R.drawable.protect,R.drawable.woman,R.drawable.silencer,R.drawable.kill};
    int[] img_select={R.drawable.witch_select,R.drawable.kill_select,R.drawable.kill_select,R.drawable.terrorist_select,R.drawable.kill_select,R.drawable.detective_select,
            R.drawable.protect_select,R.drawable.woman_select,R.drawable.silencer_select,R.drawable.kill_select};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod);
        getData();
        calc_kills();
        createRolesLV();
        createNamesLV();
        b1=(Button)findViewById(R.id.one);
        b2=(Button)findViewById(R.id.two);
        b3=(Button)findViewById(R.id.three);
        thumbs=(Button)findViewById(R.id.thumbs);
        night=(TextView)findViewById(R.id.night);
        s=(SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        s.setDragView(R.id.panel_header);
        options_tab= (ViewSwitcher) findViewById(R.id.options);
        info_tab= (ViewSwitcher) findViewById(R.id.info_tabs);
        mafia=(TextView)findViewById(R.id.mafia);
        civilian=(TextView)findViewById(R.id.civilian);
        gf=(TextView)findViewById(R.id.godf);
        silenced_player=(TextView)findViewById(R.id.silenced_player);
        info_day=(TextView)findViewById(R.id.info_day);
        eliminate_player=(Button)findViewById(R.id.eliminate);
        mod_kill=(Button)findViewById(R.id.mod_kill);
        updateGroupStrength();
        modifyActionButtons();
        if(end==1)
        {
            terminateMod();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit MOD session ?")
                .setMessage("All data will be lost !")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        mod.super.onBackPressed();
                    }
                }).create().show();
    }




    public void calc_kills(){

        if(day==1)
        {
            if(first_night_kills>1)
            {
                km_kills=1;
                m_kills=first_night_kills-1;
            }
            else if(first_night_kills==1)
            {
                km_kills=0;
                m_kills=1;
            }

        }
        else if(day!=1)
        {
            if(kills>1)
            {
                km_kills=1;
                m_kills=kills-1;
            }
            else if(kills==1)
            {
                km_kills=0;
                m_kills=1;
            }
        }


    }

    public  void createNamesLV(){//setting adapter for names display
        p_name=(ListView)findViewById(R.id.name_display);
        adapter_names=new CustomAdapterNames();
        p_name.setAdapter(adapter_names);
    }

    public void createRolesLV(){//setting adapter for roles list view
        role_name=(ListView)findViewById(R.id.roles_display);
        adapter_roles=new CustomAdapterRoles();
        role_name.setAdapter(adapter_roles);
    }

    public void createSummaryLV(){
        summary=(ListView)findViewById(R.id.summary_details);
        adapter_summary=new CustomAdapterSummary();
        summary.setAdapter(adapter_summary);
    }

    public void createDeathLV()
    {
        deaths=(ListView)findViewById(R.id.death_names);
        death_names_adapter=new CustomAdapterDeath();
        deaths.setAdapter(death_names_adapter);
    }


    public void getData()//for getting the intent data from previous activity
    {
        Intent intent=getIntent();
        sum=intent.getIntExtra("sum",0);
        kills=intent.getIntExtra("k",0);
        first_night_kills=intent.getIntExtra("f_k",0);
        calc_rows();

        roles=new String [rows*2];//initializing array length after calculating the rows
        names=new String[rows*2]; //initializing array length
        target_of_mafia=new int[kills];
        current_round_death=new int[sum];
        deathNames=new String[sum];
        target_of_king_mafia=new int[kills];
        active_person_status=new int[rows*2];

        for(int i=0;i<sum;i++)
        {
            String key="role"+i;
            roles[i]=intent.getStringExtra(key);
        }

        for(int i=0;i<sum;i++)
        {
            String key="name"+i;
            names[i]=intent.getStringExtra(key);
        }
        create_roles_display_array();
        target=new int[roles_display_count];
        active_role_status=new int[roles_display_count];


        for(int i=0;i<roles_display_count;i++)
        {
            target[i]=99; //set all target as none till now
            active_role_status[i]=1;//all roles are active
        }
        for(int i=0;i<kills;i++)
        {
            target_of_mafia[i]=99;
            target_of_king_mafia[i]=99;//set all target as none till now
        }
        for(int i=0;i<roles.length;i++)
        {
            if(roles[i]!=null)
            {
                if(roles[i].toLowerCase().contains("god"))
                {
                    active_person_status[i]=2;
                }
                else
                {
                    active_person_status[i]=1; //all characters are active
                }
                current_round_death[i]=1;
            }


        }
        //reorder();


    }

    public void calc_rows(){ //calculate the no of rows to be created for names list view
        int q=sum/2;
        int r=sum%2;
        if(r==0)
            rows=q;
        else
            rows=q+1;

    }

    public void create_roles_display_array(){  //create array with mafia occurring only once

        int mafia_flag=0;//since mafia is required only once
        int index=0,m_index=0;
        for(int i=0;i<sum;i++){  //calculating the size of the array with mafia only once
            if(!(roles[i].equals("Mafia")))
                roles_display_count++;
            else if(mafia_flag==0) //it is mafia
            {
                mafia_flag=1;
                roles_display_count++;
            }
        }
        roles_display=new String[roles_display_count];//initializing array with calculated count
        unvisited_character=new int[roles_display_count];
        for(int i=0;i<roles_display_count;i++)
            unvisited_character[i]=1;
        action=new String[roles_display_count];
        name_index_of_roles_display=new int[roles_display_count];
        mafia_flag=0;
        for(int i=0;i<sum;i++){
            if(!(roles[i].equals("Mafia")))
            {
                roles_display[index]=roles[i];
                name_index_of_roles_display[index]=i;
                index++;
            }
            else if(mafia_flag==0) //it is mafia
            {
                mafia_flag=1;
                roles_display[index]=roles[i];
                name_index_of_roles_display[index]=i;
                name_index_mafia[m_index]=i;
                m_index++;
                index++;
            }
            else if(mafia_flag==1)
            {
                name_index_mafia[m_index]=i;
                m_index++;
            }
        }
        for(int j=0;j<4;j++)
        {
            if(name_index_mafia[j]!=99)
            {
                mafia_count++;
            }
        }
    }



    private class RolesHolder { //To hold the listview role items permanently
        Button role;
        int ref;
    }

    private class NamesHolder{
        Button n1;
        Button n2;
        int ref,v1,v2;
    }

    private class DeathNamesHolder{
        TextView t;
        int ref;
    }

    private class SummaryHolder{
        TextView t;
        int ref;
    }

    class CustomAdapterRoles extends BaseAdapter//adapter for roles listview
    {

        @Override
        public int getCount() {
            return roles_display_count;
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
            final RolesHolder holder;
            if(convertView==null)
            {
                holder=new RolesHolder();
                convertView=getLayoutInflater().inflate(R.layout.roles_display,null);
                holder.role=(Button)convertView.findViewById(R.id.r1);
                convertView.setTag(holder);
            }
            else
                holder=(RolesHolder)convertView.getTag();

            holder.ref=position;
            holder.role.setText(""+roles_display[position]);

            //denoting dead character
            if(active_role_status[holder.ref]==0)
            {
                holder.role.setBackgroundResource(R.drawable.strike);
            }
            else
            {
                holder.role.setBackgroundResource(R.drawable.wood2);
            }

            //for denoting unvisited characters !
            if(unvisited_character[position]==1 && active_role_status[position]!=0)
                holder.role.setBackgroundResource(R.drawable.unvisited);
            else if(active_role_status[position]!=0)
                holder.role.setBackgroundResource(R.drawable.wood2);

            //actively selected role as green
            if(active_role!=null)
            {

                if( active_role.equalsIgnoreCase(roles_display[position]))
                {
                    holder.role.setBackgroundColor(Color.GREEN);
                    unvisited_character[position]=0;
                }
                else if(active_role_status[holder.ref]!=0 && unvisited_character[position]!=1)
                {
                    holder.role.setBackgroundResource(R.drawable.wood2);
                }
            }
            holder.role.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if(active_role_status[holder.ref]!=0 && isNight==1 && end==0)
                        {
                            if(active_role==null)
                            {
                                holder.role.setBackgroundColor(Color.GREEN);
                                active_role=roles_display[holder.ref];
                                active_role_index=holder.ref;
                                createRolesLV();
                                createNamesLV();
                                detective_result();
                                //Toast.makeText(getApplicationContext(),""+action[active_role_index],Toast.LENGTH_SHORT).show();
                            }
                            else if(!active_role.equalsIgnoreCase(roles_display[holder.ref]))
                            {
                                holder.role.setBackgroundColor(Color.GREEN);
                                active_role=roles_display[holder.ref];
                                active_role_index=holder.ref;
                                createRolesLV();
                                createNamesLV();
                                detective_result();
                                //Toast.makeText(getApplicationContext(),""+action[active_role_index],Toast.LENGTH_SHORT).show();
                            }
                            else if(active_role.equalsIgnoreCase(roles_display[holder.ref]))
                            {
                                holder.role.setBackgroundResource(R.drawable.wood2);
                                active_role=null; active_role_index=99;
                                createNamesLV();
                            }
                            modifyActionButtons();
                        }

                }
            });
            return convertView;
        }
    }

    class CustomAdapterNames extends BaseAdapter{ //adpated for names list view

        @Override
        public int getCount() {
            return rows;
        }//times of iteration

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
            final NamesHolder holder;
            if(convertView==null)
            {
                holder=new NamesHolder();
                convertView=getLayoutInflater().inflate(R.layout.names_display,null);
                holder.n1=(Button)convertView.findViewById(R.id.n1);
                holder.n2=(Button)convertView.findViewById(R.id.n2);
                convertView.setTag(holder);
            }
            else
                holder=(NamesHolder)convertView.getTag();

            holder.ref=position;
            if(active_person_status[position*2]!=0)
                holder.n1.setText(""+names[(position*2)]);
            else
                holder.n1.setText("");
            if(names[(position*2)+1]!=null && active_person_status[(position*2)+1]!=0)
                holder.n2.setText(""+names[(position*2)+1]);
            else
                holder.n2.setText("");

            holder.v1=(position*2);
            holder.v2=(position*2)+1;


            //for denoting dead players
            if(active_person_status[holder.v1]==0)
            {
                  holder.n1.setBackgroundColor(Color.parseColor("#00000000"));
            }
            else
            {
                holder.n1.setBackgroundResource(R.drawable.wood2);
            }

            if(active_person_status[holder.v2]==0)
            {
                holder.n2.setBackgroundColor(Color.parseColor("#00000000"));
            }
            else
            {
                holder.n2.setBackgroundResource(R.drawable.wood2);
            }

            //for denoting eliminate and mod killed players as red
            if(isNight==0 && active_role!=null)
            {
                if(active_role.equalsIgnoreCase("eliminate"))
                {
                    if(eliminate==holder.v1)
                    {
                        holder.n1.setBackgroundColor(Color.RED);
                    }


                    if(eliminate==holder.v2)
                    {
                        holder.n2.setBackgroundColor(Color.RED);
                    }

                }
                else if(active_role.equalsIgnoreCase("mod_kill"))
                {
                    if(active_person_status[holder.v1]>2)
                    {
                        holder.n1.setBackgroundColor(Color.RED);
                    }
                    if(active_person_status[holder.v2]>2)
                    {
                        holder.n2.setBackgroundColor(Color.RED);
                    }

                }

            }





            //for setting the buttons with names of a particular role as green others as default (if A is a mafia then Button A will be green when Mafia role is selected)
            if(isNight==1 && active_role!=null)
            {
                if(names[position*2]!=null && roles[position*2].equalsIgnoreCase(active_role) && active_person_status[position*2]!=0){
                    holder.n1.setBackgroundColor(Color.GREEN);}
                else if(active_person_status[holder.v1]!=0){
                    holder.n1.setBackgroundResource(R.drawable.wood2);
                }
                if(names[(position*2)+1]!=null && roles[(position*2)+1].equalsIgnoreCase(active_role) && active_person_status[(position*2)+1]!=0){
                    holder.n2.setBackgroundColor(Color.GREEN);
                }else if(active_person_status[holder.v2]!=0){
                    holder.n2.setBackgroundResource(R.drawable.wood2);
                }

                if(active_role.equalsIgnoreCase("Mafia"))
                {
                    if(names[holder.v1]!=null)
                    {
                        if(roles[holder.v1].equalsIgnoreCase("King Mafia") && active_person_status[position*2]!=0)
                        {
                            holder.n1.setBackgroundColor(Color.GREEN);
                        }
                    }
                    if(names[holder.v2]!=null)
                    {
                        if(roles[holder.v2].equalsIgnoreCase("King Mafia") && active_person_status[(position*2)+1]!=0)
                        {
                            holder.n2.setBackgroundColor(Color.GREEN);
                        }
                    }

                }
            }







            //setting the background as red if he is the target of the active_role

             if(active_role!=null && !active_role.equalsIgnoreCase("eliminate") && !active_role.equalsIgnoreCase("mod_kill") )//else it will crash when active role is null since index will be 99
            {
                if("Mafia".equalsIgnoreCase(active_role))
                {
                    for(int i=0;i<m_kills;i++)
                    {
                        if(target_of_mafia[i]==holder.v1)
                        {
                            holder.n1.setBackgroundColor(Color.RED);
                        }
                        if(target_of_mafia[i]==holder.v2)
                        {
                            holder.n2.setBackgroundColor(Color.RED);
                        }
                    }
                }
                else if("King Mafia".equalsIgnoreCase(active_role))
                {
                    for(int i=0;i<km_kills;i++)
                    {
                        if(target_of_king_mafia[i]==holder.v1)
                        {
                            holder.n1.setBackgroundColor(Color.RED);
                        }
                        if(target_of_king_mafia[i]==holder.v2)
                        {
                            holder.n2.setBackgroundColor(Color.RED);
                        }
                    }
                }
                else
                {
                    if(target[active_role_index]==holder.v1)
                    {
                        if(names[holder.v1]!=null && roles[holder.v1].equalsIgnoreCase(active_role) && active_person_status[holder.v1]!=0){
                            holder.n1.setBackgroundResource(R.drawable.self_protect);}
                        else
                            holder.n1.setBackgroundColor(Color.RED);
                    }

                    if(target[active_role_index]==holder.v2)
                    {
                        //holder.n2.setBackgroundColor(Color.RED);
                        if(names[holder.v2]!=null && roles[holder.v2].equalsIgnoreCase(active_role) && active_person_status[holder.v2]!=0){
                            holder.n2.setBackgroundResource(R.drawable.self_protect);}
                        else
                            holder.n2.setBackgroundColor(Color.RED);
                    }
                }



            }



            holder.n1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(active_role!=null && active_person_status[holder.v1]!=0 && isNight==1 && end==0)
                    {
                        if(names[holder.v1]!=null  && !active_role.equalsIgnoreCase("Mafia") && !active_role.equalsIgnoreCase("King Mafia") && !active_role.toLowerCase().contains("angel") && !roles[holder.v1].equalsIgnoreCase(active_role))
                        {
                            if(target[active_role_index]!=holder.v1)//clicking the button
                            {
                                holder.n1.setBackgroundColor(Color.RED);
                                target[active_role_index]=holder.v1;
                                createNamesLV();//for removing color of prev.

                            }
                            else if(target[active_role_index]==holder.v1)//de-clicking the button
                            {
                                holder.n1.setBackgroundResource(R.drawable.wood2);
                                target[active_role_index]=99;

                            }
                        }
                        else if(names[holder.v1]!=null && active_role.toLowerCase().contains("angel") )
                        {
                            if(target[active_role_index]!=holder.v1)//clicking the button
                            {
                                holder.n1.setBackgroundColor(Color.RED);
                                target[active_role_index]=holder.v1;
                                createNamesLV();//for removing color of prev.

                            }
                            else if(target[active_role_index]==holder.v1)//de-clicking the button
                            {
                                holder.n1.setBackgroundResource(R.drawable.wood2);
                                createNamesLV();
                                target[active_role_index]=99;

                            }
                        }
                        else if( names[holder.v1]!=null && active_role.equalsIgnoreCase("Mafia") && !roles[holder.v1].equalsIgnoreCase(active_role ) && !roles[holder.v1].equalsIgnoreCase("King Mafia" )   )
                        {
                            if(checkTargetOfMafia(holder.v1)==99 && mafia_target_count<(m_kills-1) )//not selected and possible to select
                            {
                                mafia_target_count++;
                                holder.n1.setBackgroundColor(Color.RED);
                                target_of_mafia[mafia_target_count]=holder.v1;

                            }
                            else if(checkTargetOfMafia(holder.v1)==99 && mafia_target_count==(m_kills-1))//not selected but already full
                            {
                                holder.n1.setBackgroundColor(Color.RED);
                                target_of_mafia[0]=99;
                                refreshMafiaTarget();
                                target_of_mafia[mafia_target_count]=holder.v1;
                                createNamesLV();
                            }
                            else if(checkTargetOfMafia(holder.v1)!=99)//we want to deselect the button
                            {
                                int index=checkTargetOfMafia(holder.v1);
                                holder.n1.setBackgroundResource(R.drawable.wood2);
                                target_of_mafia[index]=99; //since we are removing
                                refreshMafiaTarget();
                                mafia_target_count--;
                            }
                        }
                        else if( names[holder.v1]!=null && active_role.equalsIgnoreCase("King Mafia") && !roles[holder.v1].equalsIgnoreCase(active_role))
                        {
                            if(km_kills>0)
                            {
                                if(checkTargetOfKingMafia(holder.v1)==99 && king_mafia_target_count<(km_kills-1) )//not selected and possible to select
                                {
                                    king_mafia_target_count++;
                                    holder.n1.setBackgroundColor(Color.RED);
                                    target_of_king_mafia[king_mafia_target_count]=holder.v1;

                                }
                                else if(checkTargetOfKingMafia(holder.v1)==99 && king_mafia_target_count==(km_kills-1))//not selected but already full
                                {
                                    holder.n1.setBackgroundColor(Color.RED);
                                    target_of_king_mafia[0]=99;
                                    refreshKingMafiaTarget();
                                    target_of_king_mafia[king_mafia_target_count]=holder.v1;
                                    createNamesLV();
                                }
                                else if(checkTargetOfKingMafia(holder.v1)!=99)//we want to deselect the button
                                {
                                    int index=checkTargetOfKingMafia(holder.v1);
                                    holder.n1.setBackgroundResource(R.drawable.wood2);
                                    target_of_king_mafia[index]=99; //since we are removing
                                    refreshKingMafiaTarget();
                                    king_mafia_target_count--;
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"King Mafia Kills:0",Toast.LENGTH_SHORT).show();
                            }
                        }
                        modifyActionButtons();
                        detective_result();
                    }
                    else if(isNight==0 && end==0)
                    {
                        if(active_role!=null && active_person_status[holder.v1]!=0)
                        {
                            if(active_role.equalsIgnoreCase("eliminate"))
                            {
                                if(eliminate==holder.v1)
                                {
                                    eliminate=99;
                                    holder.n1.setBackgroundResource(R.drawable.wood2);
                                   // Toast.makeText(getApplicationContext(),""+active_role+"-"+eliminate,Toast.LENGTH_SHORT).show();
                                    createNamesLV();
                                }
                                else if(eliminate!=holder.v1)
                                {
                                    eliminate=holder.v1;
                                    holder.n1.setBackgroundColor(Color.RED);
                                   // Toast.makeText(getApplicationContext(),""+active_role+"-"+eliminate,Toast.LENGTH_SHORT).show();
                                    createNamesLV();
                                }


                            }
                            else if(active_role.equalsIgnoreCase("mod_kill"))
                            {
                                if(active_person_status[holder.v1]>2 )
                                {
                                    active_person_status[holder.v1]=active_person_status[holder.v1]-2;
                                    createNamesLV();
                                   // Toast.makeText(getApplicationContext(),""+active_role+"-"+active_person_status[holder.v1],Toast.LENGTH_SHORT).show();
                                }
                                else if(active_person_status[holder.v1]<=2 )
                                {
                                    active_person_status[holder.v1]=active_person_status[holder.v1]+2;
                                    createNamesLV();
                                   // Toast.makeText(getApplicationContext(),""+active_role+"-"+active_person_status[holder.v1],Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                }
            });

            holder.n2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(active_role!=null && active_person_status[holder.v2]!=0 && isNight==1 && end==0)
                    {
                        if(names[holder.v2]!=null && !active_role.equalsIgnoreCase("Mafia") && !active_role.toLowerCase().contains("angel") && !active_role.equalsIgnoreCase("King Mafia") && !roles[holder.v2].equalsIgnoreCase(active_role) )
                        {
                            if( target[active_role_index]!=holder.v2)
                            {
                                holder.n2.setBackgroundColor(Color.RED);
                                target[active_role_index]=holder.v2;
                                createNamesLV();//removing color of prev.

                            }
                            else if(target[active_role_index]==holder.v2)
                            {
                                holder.n2.setBackgroundResource(R.drawable.wood2);
                                target[active_role_index]=99;


                            }
                        }
                        else if(names[holder.v2]!=null && active_role.toLowerCase().contains("angel") )
                        {
                            if( target[active_role_index]!=holder.v2)
                            {
                                holder.n2.setBackgroundColor(Color.RED);
                                target[active_role_index]=holder.v2;
                                createNamesLV();//removing color of prev.

                            }
                            else if(target[active_role_index]==holder.v2)
                            {
                                holder.n2.setBackgroundResource(R.drawable.wood2);
                                createNamesLV();
                                target[active_role_index]=99;


                            }
                        }
                        else if( names[holder.v2]!=null && active_role.equalsIgnoreCase("Mafia") && !roles[holder.v2].equalsIgnoreCase(active_role) && !roles[holder.v2].equalsIgnoreCase("King Mafia" ))
                        {
                            if(checkTargetOfMafia(holder.v2)==99 && mafia_target_count<(m_kills-1) )//not selected and possible to select
                            {
                                mafia_target_count++;
                                holder.n2.setBackgroundColor(Color.RED);
                                target_of_mafia[mafia_target_count]=holder.v2;

                            }
                            else if(checkTargetOfMafia(holder.v2)==99 && mafia_target_count==(m_kills-1))//not selected but already full
                            {
                                holder.n2.setBackgroundColor(Color.RED);
                                target_of_mafia[0]=99;
                                refreshMafiaTarget();
                                target_of_mafia[mafia_target_count]=holder.v2;
                                createNamesLV();
                            }
                            else if(checkTargetOfMafia(holder.v2)!=99)//we want to deselect the button
                            {
                                int index=checkTargetOfMafia(holder.v2);
                                holder.n2.setBackgroundResource(R.drawable.wood2);
                                target_of_mafia[index]=99; //since we are removing
                                refreshMafiaTarget();
                                mafia_target_count--;
                            }
                        }
                        else if( names[holder.v2]!=null && active_role.equalsIgnoreCase("King Mafia") && !roles[holder.v2].equalsIgnoreCase(active_role))
                        {
                            if(km_kills>0)
                            {
                                if(checkTargetOfKingMafia(holder.v2)==99 && king_mafia_target_count<(km_kills-1) )//not selected and possible to select
                                {
                                    king_mafia_target_count++;
                                    holder.n2.setBackgroundColor(Color.RED);
                                    target_of_king_mafia[king_mafia_target_count]=holder.v2;

                                }
                                else if(checkTargetOfKingMafia(holder.v2)==99 && king_mafia_target_count==(km_kills-1))//not selected but already full
                                {
                                    holder.n2.setBackgroundColor(Color.RED);
                                    target_of_king_mafia[0]=99;
                                    refreshKingMafiaTarget();
                                    target_of_king_mafia[king_mafia_target_count]=holder.v2;
                                    createNamesLV();
                                }
                                else if(checkTargetOfKingMafia(holder.v2)!=99)//we want to deselect the button
                                {
                                    int index=checkTargetOfKingMafia(holder.v2);
                                    holder.n2.setBackgroundResource(R.drawable.wood2);
                                    target_of_king_mafia[index]=99; //since we are removing
                                    refreshKingMafiaTarget();
                                    king_mafia_target_count--;
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"King Mafia Kills:0",Toast.LENGTH_SHORT).show();
                            }

                        }
                        modifyActionButtons();
                        detective_result();
                    }
                    else if(isNight==0 && end==0)
                    {
                        if(active_role!=null && active_person_status[holder.v2]!=0)
                        {
                            if(active_role.equalsIgnoreCase("eliminate"))
                            {
                                if(eliminate!=holder.v2)
                                {
                                    eliminate=holder.v2;
                                    createNamesLV();
                                    //Toast.makeText(getApplicationContext(),""+active_role+"-"+eliminate,Toast.LENGTH_SHORT).show();
                                }
                                else if(eliminate==holder.v2)
                                {
                                    eliminate=99;
                                    createNamesLV();
                                   // Toast.makeText(getApplicationContext(),""+active_role+"-"+eliminate,Toast.LENGTH_SHORT).show();
                                }

                            }
                            else if(active_role.equalsIgnoreCase("mod_kill"))
                            {
                                if(active_person_status[holder.v2]>2 )
                                {
                                    active_person_status[holder.v2]-=2;
                                    createNamesLV();
                                   // Toast.makeText(getApplicationContext(),""+active_role+"-"+active_person_status[holder.v2],Toast.LENGTH_SHORT).show();
                                }
                                else if(active_person_status[holder.v2]<=2 )
                                {
                                    active_person_status[holder.v2]+=2;
                                    createNamesLV();
                                    //Toast.makeText(getApplicationContext(),""+active_role+"-"+active_person_status[holder.v2],Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }


                }
            });




            return convertView;
        }
    }

    public int checkTargetOfMafia(int t)
    {

        for(int i=0;i<m_kills;i++)
        {
            if(target_of_mafia[i]==t)
                return i;
        }
        return 99;
    }

    public void refreshMafiaTarget(){
        for (int j=0; j<m_kills; j++){
            if (target_of_mafia[j]==99){
                for (int k=j+1; k<m_kills; k++){
                    target_of_mafia[k-1] = target_of_mafia[k];
                }
                target_of_mafia[m_kills-1] = 99;
                break;
            }
        }
    }

    public int checkTargetOfKingMafia(int t)
    {

        for(int i=0;i<km_kills;i++)
        {
            if(target_of_king_mafia[i]==t)
                return i;
        }
        return 99;
    }

    public void refreshKingMafiaTarget(){
        for (int j=0; j<km_kills; j++){
            if (target_of_king_mafia[j]==99){
                for (int k=j+1; k<km_kills; k++){
                    target_of_king_mafia[k-1] = target_of_king_mafia[k];
                }
                target_of_king_mafia[km_kills-1] = 99;
                break;
            }
        }
    }


    public void modifyActionButtons()
    {
        if(active_role==null)
        {
            b1.setBackgroundColor(Color.parseColor("#00000000"));
            b1.setEnabled(false);
            b2.setBackgroundColor(Color.parseColor("#00000000"));
            b2.setEnabled(false);
            b3.setBackgroundColor(Color.parseColor("#00000000"));
            b3.setEnabled(false);
        }
        else if(active_role.toLowerCase().contains("joker"))
        {
            b1.setEnabled(true);
            b2.setEnabled(true);
            b3.setEnabled(true);
            if(target[active_role_index]!=99)
            {
                if(action[active_role_index]==null)
                {
                    b1.setBackgroundResource(R.drawable.kill_select);
                    b2.setBackgroundResource(R.drawable.detective);
                    b3.setBackgroundResource(R.drawable.protect);
                    action[active_role_index]="kills";
                }
                else
                {
                    if(action[active_role_index].equalsIgnoreCase("kills"))
                    {
                        b1.setBackgroundResource(R.drawable.kill_select);
                        b2.setBackgroundResource(R.drawable.detective);
                        b3.setBackgroundResource(R.drawable.protect);
                    }
                    else if(action[active_role_index].equalsIgnoreCase("detects"))
                    {
                        b1.setBackgroundResource(R.drawable.kill);
                        b2.setBackgroundResource(R.drawable.detective_select);
                        b3.setBackgroundResource(R.drawable.protect);
                    }
                    else if(action[active_role_index].equalsIgnoreCase("protects"))
                    {
                        b1.setBackgroundResource(R.drawable.kill);
                        b2.setBackgroundResource(R.drawable.detective);
                        b3.setBackgroundResource(R.drawable.protect_select);
                    }
                }
            }
            else {
                b1.setBackgroundResource(R.drawable.kill);
                b2.setBackgroundResource(R.drawable.detective);
                b3.setBackgroundResource(R.drawable.protect);
                action[active_role_index]=null;
            }

        }
        else
        {
            b1.setBackgroundColor(Color.parseColor("#00000000"));
            b1.setEnabled(false);
            b2.setEnabled(true);
            b3.setBackgroundColor(Color.parseColor("#00000000"));
            b3.setEnabled(false);

            if(active_role.equalsIgnoreCase("Mafia"))
            {
                for(int i=0;i<m_kills;i++)
                {
                    if(target_of_mafia[i]!=99)
                    {
                        b2.setBackgroundResource(R.drawable.kill_select);
                        action[active_role_index]="kills";
                        return;
                    }
                }
                b2.setBackgroundResource(R.drawable.kill);
                action[active_role_index]=null;
            }
            else if(active_role.equalsIgnoreCase("King Mafia"))
            {
                for(int i=0;i<km_kills;i++)
                {
                    if(target_of_king_mafia[i]!=99)
                    {
                        b2.setBackgroundResource(R.drawable.kill_select);
                        action[active_role_index]="kills";
                        return;
                    }
                }
                b2.setBackgroundResource(R.drawable.kill);
                action[active_role_index]=null;
            }
            else
            {
                for(int i=0;i<10;i++)
                {
                    if(active_role.toLowerCase().contains(r[i].toLowerCase()))
                    {
                        if(target[active_role_index]!=99)
                        {
                            b2.setBackgroundResource(img_select[i]);
                            action[active_role_index]=action_preset[i];
                        }
                        else
                        {
                            b2.setBackgroundResource(img[i]);
                            action[active_role_index]=null;
                        }

                    }
                }
            }

        }

    }

    public void jokerSet(View v)
    {
        if(active_role.toLowerCase().contains("joker"))
        {
            String b=getResources().getResourceEntryName(v.getId());
            if(b.equalsIgnoreCase("one"))
            {
                action[active_role_index]="kills";
            }
            else if(b.equalsIgnoreCase("two"))
            {
                action[active_role_index]="detects";
            }
            else if(b.equalsIgnoreCase("three"))
            {
                action[active_role_index]="protects";
            }
            modifyActionButtons();
            detective_result();
        }

    }

    //ALL METHODS BELOW ARE FOR RESULT COMPUTATION AND DISPLAY



    public void detective_result(){
        if(active_role!=null)
        {
            if(active_role.toLowerCase().contains("detective")) {
                if (target[active_role_index] != 99)
                {
                    if(!isWitchTarget(name_index_of_roles_display[active_role_index]))
                    {
                        if(roles[target[active_role_index]].equalsIgnoreCase("Mafia"))
                        {
                            thumbs.setBackgroundResource(R.drawable.thumbs_up);
                        }
                        else
                        {
                            thumbs.setBackgroundResource(R.drawable.thumbs_down);
                        }
                    }
                    else
                    {
                        thumbs.setBackgroundResource(R.drawable.thumbs_down);
                    }
                }
                else
                    thumbs.setBackgroundColor(Color.parseColor("#00000000"));

            }
            else if(active_role.toLowerCase().contains("joker")) {
                if (target[active_role_index] != 99 && action[active_role_index].equalsIgnoreCase("detects"))
                {
                    if(!isWitchTarget(name_index_of_roles_display[active_role_index]))
                    {
                        if(roles[target[active_role_index]].equalsIgnoreCase("Mafia") || roles[target[active_role_index]].equalsIgnoreCase("King Mafia"))
                        {
                            thumbs.setBackgroundResource(R.drawable.thumbs_up);
                        }
                        else
                        {
                            thumbs.setBackgroundResource(R.drawable.thumbs_down);
                        }
                    }
                    else
                    {
                        thumbs.setBackgroundResource(R.drawable.thumbs_down);
                    }
                }
                else
                    thumbs.setBackgroundColor(Color.parseColor("#00000000"));

            }
            else
            {
                thumbs.setBackgroundColor(Color.parseColor("#00000000"));
            }
        }
        else
        {
            thumbs.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    public void closeInfo(View v)
    {
        String b=getResources().getResourceEntryName(v.getId());
        if(b.equalsIgnoreCase("btm_s"))
        {
            switch_info(v);
        }
        s.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public boolean isWitchTarget(int index)
    {   int flag=0;
        for(int i=0;i<roles_display_count;i++)
        {
            if(roles_display[i].toLowerCase().contains("witch") && i!=index)
            {
                if(target[i]==index)
                {
                    flag=1;
                }

            }
        }
        if(flag==1)
            return true;
        else
            return false;
    }

    public void compute(View v)
    {
        int death_index=0;current_round_death_count=0;
        summary_details.add("ROUND "+day);
        summary_details.add("ROUND "+day);
        for(int i=0;i<roles_display_count;i++)
        {
            if(active_role_status[i]!=0)
            {
                if(!roles_display[i].equalsIgnoreCase("mafia") && !roles_display[i].equalsIgnoreCase("King Mafia") )
                {
                    if(target[i]==99)
                    {
                        summary_details.add(""+roles_display[i]+" makes no move");
                        summary_details.add(""+names[name_index_of_roles_display[i]]+" makes no move");
                    }
                    else
                    {
                        summary_details.add(""+roles_display[i]+" "+action[i]+" "+roles[target[i]]);
                        summary_details.add(""+names[name_index_of_roles_display[i]]+" "+action[i]+" "+names[target[i]]);
                    }
                }
                else if(roles_display[i].equalsIgnoreCase("mafia"))
                {
                    int count=0;String t="";
                    String t_names=active_mafia();
                    t_names=t_names.substring(0,t_names.length()-1);
                    for(int k=0;k<m_kills;k++)
                    {
                        if(target_of_mafia[k]!=99)
                        {
                            if(count==0)
                            {
                                t=t+roles[target_of_mafia[k]];
                                t_names=t_names+" kills "+names[target_of_mafia[k]];
                            }
                            else
                            {
                                t=t+","+roles[target_of_mafia[k]];
                                t_names=t_names+","+names[target_of_mafia[k]];
                            }
                            count++;
                        }
                    }
                    if(count>0)
                    {
                        summary_details.add("Mafia kills "+t);
                        summary_details.add(t_names);
                    }
                    else
                    {
                        summary_details.add("Mafia makes no move");
                        summary_details.add(""+t_names+" makes no move");
                    }
                }
                else if(roles_display[i].equalsIgnoreCase("king mafia"))
                {
                    int count=0;String t="King Mafia kills ";
                    String t_name=""+names[name_index_of_roles_display[i]];
                    for(int k=0;k<km_kills;k++)
                    {
                        if(target_of_king_mafia[k]!=99)
                        {
                            if(count==0)
                            {
                                t=t+roles[target_of_king_mafia[k]];
                                t_name=t_name+" kills "+names[target_of_king_mafia[k]];
                            }
                            else
                            {
                                t=t+","+roles[target_of_king_mafia[k]];
                                t_name=t_name+","+names[target_of_king_mafia[k]];
                            }
                            count++;
                        }
                    }
                    if(count>0)
                    {
                        summary_details.add(t);
                        summary_details.add(t_name);
                    }
                    else
                    {
                        summary_details.add("King Mafia makes no move");
                        summary_details.add(""+t_name+"  makes no move");
                    }
                }
            }


        }
        /*for (int i = 0; i<summary_details.size(); i++){
            Toast.makeText(getApplicationContext(),""+summary_details.get(i),Toast.LENGTH_SHORT).show();
        }*/
        for(int i=0;i<roles_display_count;i++)//to cancel witch target if it is targeted by another witch
        {
            if(roles_display[i].toLowerCase().contains("witch") && active_role_status[i]!=0)
            {
                if(isWitchTarget(name_index_of_roles_display[i]))
                {
                    target[i]=99;
                }
            }
        }

        for(int i=0;i<roles_display_count;i++)
        {
            if(roles_display[i].equalsIgnoreCase("King Mafia") && active_role_status[i]!=0 )
            {
                if( isWitchTarget(name_index_of_roles_display[i]))
                {
                    for(int j=0;j<km_kills;j++)
                    {
                        target_of_king_mafia[j]=99;
                    }
                    for(int k=0;k<m_kills;k++)
                    {
                        target_of_mafia[k]=99;
                    }
                    //Toast.makeText(getApplicationContext(),"All km and mtarget down",Toast.LENGTH_SHORT).show();
                    break;

                }

            }
        }
        for(int i=0;i<roles_display_count;i++)
        {   int flag=0;
            if(roles_display[i].equalsIgnoreCase("Mafia") && active_role_status[i]!=0 )
            {
                for(int j=0;j<mafia_count;j++)
                {
                   if(isWitchTarget(name_index_mafia[j]))
                   {
                       flag++;
                       break;
                   }
                }
                if(flag>0)
                {
                    for(int k=0;k<m_kills;k++)
                    {
                        target_of_mafia[k]=99;
                    }
                    //Toast.makeText(getApplicationContext(),"All m target down",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

       for(int i=0;i<roles_display_count;i++)
        {
            //silenced_player.setText("No One");
            if(!roles_display[i].equalsIgnoreCase("mafia") && !roles_display[i].equalsIgnoreCase("King Mafia") && active_role_status[i]!=0)
            {
               if(target[i]!=99 && action[i]!=null)
               {
                   if(action[i].equalsIgnoreCase("silences"))
                   {
                       if(isWitchTarget(name_index_of_roles_display[i]))
                       {
                           target[i]=99;action[i]=null;
                           //Toast.makeText(getApplicationContext(),"S=-ve",Toast.LENGTH_SHORT).show();
                           silenced_player.setText("No One");
                       }
                       else
                       {
                           //Toast.makeText(getApplicationContext(),"S=+ve"+names[target[i]],Toast.LENGTH_SHORT).show();
                           silenced_player.setText(""+names[target[i]]);
                       }

                   }
                   else if(roles_display[i].toLowerCase().contains("god") && !isWitchTarget(name_index_of_roles_display[i]) )
                   {   if(roles[target[i]].toLowerCase().contains("god"))
                       {
                          if(active_person_status[target[i]]==1)
                          {
                              active_person_status[target[i]]=0;
                              current_round_death[target[i]]=0;
                          }

                       }
                       else{
                           active_person_status[target[i]]=0;
                           current_round_death[target[i]]=0;
                       }


                   }
                   else if(action[i].equalsIgnoreCase("kills") && !isWitchTarget(name_index_of_roles_display[i]))
                   {
                       if(roles[target[i]].toLowerCase().contains("god"))
                       {
                           if(active_person_status[target[i]]==1){
                               active_person_status[target[i]]=0;
                               current_round_death[target[i]]=0;
                           }
                       }
                       else
                       {
                           active_person_status[target[i]]=0;
                           current_round_death[target[i]]=0;
                       }
                   }
                   else if(action[i].equalsIgnoreCase("goes with") && !isWitchTarget(name_index_of_roles_display[i]))
                   {
                       if(roles[target[i]].equalsIgnoreCase("Mafia") || roles[target[i]].equalsIgnoreCase("King Mafia"))
                       {
                           active_person_status[i]=0;
                           current_round_death[i]=0;
                       }
                   }
               }


            }
            else if(roles_display[i].equalsIgnoreCase("King Mafia") && king_mafia_target_count!=-1 && active_role_status[i]!=0 )
            {
                for(int j=0;j<km_kills;j++)
                {
                    if(target_of_king_mafia[j]!=99)
                    {
                        if(roles[target_of_king_mafia[j]].toLowerCase().contains("god"))
                        {
                            if(active_person_status[target_of_king_mafia[j]]==1){
                                active_person_status[target_of_king_mafia[j]]=0;
                                current_round_death[target_of_king_mafia[j]]=0;
                            }
                        }
                        else
                        {
                            active_person_status[target_of_king_mafia[j]]=0;
                            current_round_death[target_of_king_mafia[j]]=0;
                        }

                    }
                }
            }
            else if(roles_display[i].equalsIgnoreCase("Mafia") && mafia_target_count!=-1 && active_role_status[i]!=0 )
            {
                for(int j=0;j<m_kills;j++)
                {
                    if(target_of_mafia[j]!=99)
                    {
                        if(roles[target_of_mafia[j]].toLowerCase().contains("god"))
                        {
                            if(active_person_status[target_of_mafia[j]]==1){
                                active_person_status[target_of_mafia[j]]=0;
                                current_round_death[target_of_mafia[j]]=0;
                            }
                        }
                        else{
                             active_person_status[target_of_mafia[j]]=0;
                             current_round_death[target_of_mafia[j]]=0;}
                    }
                }
            }
        }
        for(int i=0;i<roles_display_count;i++)
        {
            if(roles_display[i].toLowerCase().contains("god") && target[i]!=99 && active_role_status[i]!=0 ) {
                active_person_status[name_index_of_roles_display[i]] = 1;
            }
        }
        for(int i=0;i<roles_display_count;i++)
        {
            if(roles_display[i].toLowerCase().contains("terrorist") && !isWitchTarget(name_index_of_roles_display[i])  && target[i]!=99 && active_role_status[i]!=0)
            {
                if(active_person_status[i]==0)
                {
                    active_person_status[target[i]]=0;
                    current_round_death[target[i]]=0;
                }
            }
        }
        for(int i=0;i<roles_display_count;i++)
        {
            if(action[i]!=null)
            {
                if(action[i].equalsIgnoreCase("protects") && !isWitchTarget(name_index_of_roles_display[i]) && target[i]!=99 && active_role_status[i]!=0 )
                {
                    if(active_person_status[target[i]]==0)
                    {
                        active_person_status[target[i]]=1;
                        current_round_death[target[i]]=1;
                    }
                }
            }

        }
        for(int i=0;i<sum;i++)
        {
            if(current_round_death[i]==0 && names[i]!=null)
            {
                deathNames[death_index]=names[i];
                current_round_death_count++;
                death_index++;
            }
        }
        s.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        createSummaryLV();
        dayBreaks();


    }

    public void nextRound(View v)
    {       int mk_count=0;String mk="MOD KILL - ",mk_names="MOD KILL - ";
            isNight=1;
            day++;
            night.setText("NIGHT "+day);
            if(eliminate!=99)
            {
                active_person_status[eliminate]=0;
                summary_details.add("ELIMINATED - "+roles[eliminate]);
                summary_details.add("ELIMINATED - "+names[eliminate]);
            }
            for(int i=0;i<sum;i++)
            {
                if(active_person_status[i]>2)
                {
                    active_person_status[i]=0;
                    if(mk_count==0)
                    {
                        mk=mk+roles[i];
                        mk_names=mk_names+names[i];
                    }
                    else
                    {
                        mk=mk+","+roles[i];
                        mk_names=mk_names+","+names[i];

                    }
                    mk_count++;
                }
            }
            if(mk_count>0)
            {
                summary_details.add(mk);
                summary_details.add(mk_names);
            }
            active_role=null;eliminate=99;
            updateGroupStrength();
            updateRolesEliminated();
            createNamesLV();
            createRolesLV();
            createSummaryLV();
            for(int i=0;i<sum;i++)
            {
                current_round_death[i]=1;
            }
            for(int i=0;i<roles_display_count;i++)
            {
                unvisited_character[i]=1;
            }
            eliminate_player.setBackgroundResource(R.drawable.wood2);
            mod_kill.setBackgroundResource(R.drawable.wood2);
            for(int i=0;i<roles_display_count;i++)
            {
                target[i]=99;
                action[i]=null;
            }

            for(int i=0;i<kills;i++)
            {
                target_of_mafia[i]=99;
                target_of_king_mafia[i]=99;
            }
            if(end==0)
            {
                calc_kills();
                options_tab.showNext();
                silenced_player.setText("No One");
            }
            else
            {
                terminateMod();
            }


    }

    public void terminateMod(){
        View sep=findViewById(R.id.seperator);
        p_name.setVisibility(View.GONE);
        role_name.setVisibility(View.GONE);
        sep.setVisibility(View.GONE);
        options_tab.setVisibility(View.GONE);
        s.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void dayActions(View v)
    {
        String b=getResources().getResourceEntryName(v.getId());
        if(b.equalsIgnoreCase("eliminate"))
        {  if(active_role!=null)
           {
              if(active_role.equalsIgnoreCase("eliminate"))
              {
                  eliminate_player.setBackgroundResource(R.drawable.wood2);
                  active_role=null;
              }
              else
              {
                  eliminate_player.setBackgroundColor(Color.parseColor("#F44336"));
                  mod_kill.setBackgroundResource(R.drawable.wood2);
                  active_role="eliminate";
              }
           }
           else
           {
               eliminate_player.setBackgroundColor(Color.parseColor("#F44336"));
               active_role="eliminate";
           }

        }
        else if(b.equalsIgnoreCase("mod_kill"))
        {
            if(active_role!=null)
            {
                if(active_role.equalsIgnoreCase("mod_kill"))
                {
                    mod_kill.setBackgroundResource(R.drawable.wood2);
                    active_role=null;
                }
                else
                {
                    mod_kill.setBackgroundColor(Color.parseColor("#F44336"));
                    eliminate_player.setBackgroundResource(R.drawable.wood2);
                    active_role="mod_kill";
                }
            }
            else
            {
                mod_kill.setBackgroundColor(Color.parseColor("#F44336"));
                active_role="mod_kill";
            }
        }
        createNamesLV();
    }

    public void updateGroupStrength()
    {
        int m=0,c=0,g=0;
        for(int i=0;i<sum;i++)
        {
            if(active_person_status[i]!=0)
            {
                if(roles[i].toLowerCase().contains("god"))
                {
                    g++;
                }
                else if(roles[i].equalsIgnoreCase("King Mafia") || roles[i].equalsIgnoreCase("Mafia") || roles[i].toLowerCase().contains("witch") || roles[i].toLowerCase().contains("terrorist"))
                {
                    m++;
                }
                else
                {
                    c++;
                }

            }
        }
        gf.setText(""+g);
        mafia.setText(""+m);
        civilian.setText(""+c);
        computeWin(m,c,g);

    }

    public void updateRolesEliminated()
    {
        int flag=0;
        for (int i = 0; i < roles_display_count; i++){//for finding the roles eliminated
            if(roles_display[i].equalsIgnoreCase("Mafia"))
            {
                for(int j=0;j<mafia_count;j++)
                {
                    if(active_person_status[name_index_mafia[j]]==0)
                        flag++;
                }
                for(int k=0;k<roles_display_count;k++)
                {
                    if(roles_display[k].equalsIgnoreCase("King Mafia") && active_person_status[name_index_of_roles_display[k]]==1)
                    {
                     flag=0;
                    }
                }
                if(flag==mafia_count)
                    active_role_status[i]=0;

            }
            else
            {
                if(active_person_status[name_index_of_roles_display[i]]==0)
                {
                    active_role_status[i]=0;
                }
            }
        }
    }

    public void dayBreaks() {
            isNight = 0;
            mafia_target_count = -1;
            king_mafia_target_count = -1;
            active_role = null;
            active_role_index = 99;
            createDeathLV();
            updateRolesEliminated();
            createRolesLV();
            createNamesLV();
            modifyActionButtons();
            updateGroupStrength();
            if (end == 0) {
                night.setText("DAY " + day);
                info_day.setText("DAY" + day);
                options_tab.showNext();
            } else {
                terminateMod();
            }
    }

    public void computeWin(int m,int c,int g)
    {

        Toast toast = new Toast(getBaseContext());
        ImageView view = new ImageView(getBaseContext());
        if(m==0 && c==0 && g!=0)
        {
            view.setImageResource(R.drawable.gf_win);
            end=1;
        }
        else if(m==0 && c!=0 && g==0)
        {
            view.setImageResource(R.drawable.civ_win);
            end=1;
        }
        else if(m!=0 && c==0 && g==0)
        {
            view.setImageResource(R.drawable.maf_win);
            end=1;
        }
        else if(m==0 && c==0 && g==0)
        {
            view.setImageResource(R.drawable.draw);
            end=1;
        }
        if(end==1)
        {
            toast.setView(view);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();

        }

    }


    class CustomAdapterDeath extends BaseAdapter{

        @Override
        public int getCount() {
            return current_round_death_count;
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
            final DeathNamesHolder holder;
            if(convertView==null)
            {
                holder=new DeathNamesHolder();
                convertView=getLayoutInflater().inflate(R.layout.death_names,null);
                holder.t=(TextView) convertView.findViewById(R.id.name_inactive);
                convertView.setTag(holder);
            }
            else
                holder=(DeathNamesHolder) convertView.getTag();

            holder.ref=position;
            holder.t.setText(""+deathNames[position]);
            return convertView;
        }
    }

    public void switch_info(View v)
    {
        info_tab.showNext();
    }

    class CustomAdapterSummary extends BaseAdapter{

        @Override
        public int getCount() {
            return summary_details.size()/2;
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

            final SummaryHolder holder;
            if(convertView==null)
            {
                holder=new SummaryHolder();
                convertView=getLayoutInflater().inflate(R.layout.summary_text,null);
                holder.t=(TextView) convertView.findViewById(R.id.summary_line);
                convertView.setTag(holder);
            }
            else
                holder=(SummaryHolder) convertView.getTag();

            if(summary_mode==0)
            {
                holder.ref=position*2;
            }
            else
            {
                holder.ref=(position*2)+1;
            }

            if(summary_details.get(holder.ref).toLowerCase().contains("round"))
            {
                holder.t.setTextColor(Color.parseColor("#F44336"));;
                holder.t.setBackgroundColor(Color.parseColor("#000000"));
                holder.t.setText(""+summary_details.get(holder.ref));
            }
            else
            {
                holder.t.setTextColor(Color.parseColor("#000000"));;
                holder.t.setBackgroundColor(Color.parseColor("#00000000"));
                holder.t.setText(""+summary_details.get(holder.ref));
            }

            return convertView;
        }


    }

    public void toggle_summary(View v)
    {
        if(summary_mode==0)
        {
            summary_mode++;
        }
        else
        {
            summary_mode--;
        }
        createSummaryLV();
    }

    public String active_mafia()
    {
        String m="";
        for(int i=0;i<sum;i++)
        {
            if((roles[i].equalsIgnoreCase("mafia") || roles[i].equalsIgnoreCase("king mafia")) && active_person_status[i]!=0)
            {
                m=m+names[i]+",";

            }
        }
        return m;
    }






}
