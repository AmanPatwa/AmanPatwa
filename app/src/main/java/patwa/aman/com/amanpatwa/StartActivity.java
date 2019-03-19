package patwa.aman.com.amanpatwa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {
    RadioGroup rg;
    RadioButton person,org,radioButton,admin;
    TextView goAhead;
//    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        rg=(RadioGroup)findViewById(R.id.start_rg);
        person=(RadioButton)findViewById(R.id.start_per_radiobtn);
        //org=(RadioButton)findViewById(R.id.start_org_radiobtn);
        admin=(RadioButton)findViewById(R.id.start_admin_radiobtn);
        goAhead=(TextView)findViewById(R.id.start_go_ahead);

//        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.nav_actionbar);
//        setSupportActionBar(toolbar);

        goAhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioId = rg.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);

                if (radioButton != null) {

                    System.out.println("akddjk"+radioButton.getText().toString());

                    Intent personIntent = new Intent(StartActivity.this, LoginActivity.class);
                    personIntent.putExtra("type", radioButton.getText().toString());
                    startActivity(personIntent);

                    //if(radioButton.getText().equals("Organization")){
                    //Intent orgIntent=new Intent(StartActivity.this,LoginActivity.class);
                    //personIntent.putExtra("person",radioButton.getText().toString());
                    //}
                }
                else {
                    Toast.makeText(StartActivity.this, "Please select one choice", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
