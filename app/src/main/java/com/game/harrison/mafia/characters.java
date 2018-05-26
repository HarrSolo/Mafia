package com.game.harrison.mafia;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Harrison on 15-01-2018.
 */

public class characters extends AppCompatActivity {
    TextView page_no,role_name_big,role_name,part,kpt,ddt,jdt;
    ImageView role_action_image,alliance_image;
    int page;int button=1;
    Button p,n,ex;
    String[] role={"WITCH","MAFIA","KING MAFIA","TERRORIST","JOKER","ASSASSIN","ANGEL","DETECTIVE","SHEELA","SILENCER","GOD FATHER"};
    String[] par={"0-4","1-4","1","0-4","0-4","0-4","0-4","0-4","0-4","1","0-4"};
    String[] kp={"NO","YES","YES","NO","YES","YES","NO","NO","NO","NO","YES"};
    String[] dd={"DOWN","UP","DOWN","DOWN","DOWN","DOWN","DOWN","DOWN","DOWN","DOWN","DOWN"};
    String[] jd={"DOWN","UP","UP","DOWN","DOWN","DOWN","DOWN","DOWN","DOWN","DOWN","DOWN"};
    int[] role_action={R.drawable.witch,R.drawable.kill,R.drawable.kill,R.drawable.terrorist,R.drawable.kill,R.drawable.kill,R.drawable.protect,R.drawable.detective,R.drawable.woman,R.drawable.silencer,R.drawable.kill};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.characters);
        page_no=(TextView)findViewById(R.id.page_no);
        role_name_big=(TextView)findViewById(R.id.role_name_big);
        role_name=(TextView)findViewById(R.id.role_name);
        part=(TextView)findViewById(R.id.par);
        kpt=(TextView)findViewById(R.id.kp);
        ddt=(TextView)findViewById(R.id.dd);
        jdt=(TextView)findViewById(R.id.jd);
        role_action_image=(ImageView)findViewById(R.id.role_action_image);
        alliance_image=(ImageView)findViewById(R.id.alliance_image);
        p=(Button)findViewById(R.id.power);
        n=(Button)findViewById(R.id.notes);
        ex=(Button)findViewById(R.id.example);
        page=1;
        update();
    }

    public void tab_change(View v){
        Button b=(Button)v;
        String s=b.getText().toString();
        if(s.equalsIgnoreCase("power"))
            button=1;
        else if(s.equalsIgnoreCase("notes"))
            button=2;
        else
            button=3;

        buttonUpdate();
    }

    public void buttonUpdate(){
        if(button==1)
        {
            p.setBackgroundColor(Color.parseColor("#FF7C00"));
            n.setBackgroundColor(Color.parseColor("#3E2310"));
            ex.setBackgroundColor(Color.parseColor("#3E2310"));

            p.setTextColor(Color.parseColor("#000000"));
            n.setTextColor(Color.parseColor("#ffffff"));
            ex.setTextColor(Color.parseColor("#ffffff"));
        }
        else if(button==2)
        {
            p.setBackgroundColor(Color.parseColor("#3E2310"));
            n.setBackgroundColor(Color.parseColor("#FF7C00"));
            ex.setBackgroundColor(Color.parseColor("#3E2310"));

            p.setTextColor(Color.parseColor("#ffffff"));
            n.setTextColor(Color.parseColor("#000000"));
            ex.setTextColor(Color.parseColor("#ffffff"));
        }
        else
        {
            p.setBackgroundColor(Color.parseColor("#3E2310"));
            n.setBackgroundColor(Color.parseColor("#3E2310"));
            ex.setBackgroundColor(Color.parseColor("#FF7C00"));

            p.setTextColor(Color.parseColor("#ffffff"));
            n.setTextColor(Color.parseColor("#ffffff"));
            ex.setTextColor(Color.parseColor("#000000"));
        }
    }
    public void pageChange(View v)
    {
        String action=getResources().getResourceEntryName(v.getId());
        if(action.equalsIgnoreCase("inc"))
        {
            if(page!=11)
            page++;
        }
        else
        {
            if(page!=1)
                page--;

        }
        button=1;
        update();

    }

    public void update()
    {
        int index=page-1;
        page_no.setText(""+page+"/11");
        role_name_big.setText(""+role[index]);
        role_name.setText("ROLE:"+role[index]);
        part.setText("PARTICIPANTS : "+par[index]);
        kpt.setText("KILL POWER : "+kp[index]);
        ddt.setText("ON DETECT : "+dd[index]);
        jdt.setText("ON JOKER DETECT : "+jd[index]);
        role_action_image.setBackgroundResource(role_action[index]);
        if(page<=4)
            alliance_image.setBackgroundResource(R.drawable.mafia);
        else if(page<=10)
            alliance_image.setBackgroundResource(R.drawable.civilians);
        else
            alliance_image.setBackgroundResource(R.drawable.god_father);

        buttonUpdate();

    }
}
