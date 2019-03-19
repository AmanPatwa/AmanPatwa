package patwa.aman.com.amanpatwa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText mail,mophone,pswd,user;
    TextView login,signup;
    DatabaseReference users;
    ProgressDialog mprogressDialog;
    FirebaseAuth mAuth;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signup = (TextView) findViewById(R.id.signup_sup);
        login = (TextView) findViewById(R.id.signup_login);
        user = (EditText) findViewById(R.id.signup_user);
        pswd = (EditText) findViewById(R.id.signup_pswrd);
        mail = (EditText) findViewById(R.id.signup_mail);
        mophone = (EditText) findViewById(R.id.signup_mobphone);
        mprogressDialog=new ProgressDialog(this);
        type=getIntent().getStringExtra("type");
        users= FirebaseDatabase.getInstance().getReference("users");
        mAuth=FirebaseAuth.getInstance();


        /*Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/LatoLight.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        mophone.setTypeface(custom_font);
        signup.setTypeface(custom_font1);
        pswd.setTypeface(custom_font);
        login.setTypeface(custom_font);
        user.setTypeface(custom_font);
        mail.setTypeface(custom_font);*/

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mprogressDialog.setTitle("Creating new User");
                mprogressDialog.setMessage("Please wait!!");
                mprogressDialog.setCanceledOnTouchOutside(false);
                mprogressDialog.show();
                createUser(mail,pswd,user,mophone);
            }
        });



        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(SignUpActivity.this,LoginActivity.class);
                it.putExtra("type",type);
                startActivity(it);
            }
        });
    }


    private void createUser(EditText memail, EditText mpass, EditText user, EditText mobphone) {
        String email,password,muser,mmobphone;
        memail.setError(null);
        mpass.setError(null);
        user.setError(null);
        mobphone.setError(null);

        email=memail.getText().toString().trim();
        password=mpass.getText().toString().trim();
        muser=user.getText().toString();
        mmobphone=mobphone.getText().toString();

        boolean cancel=false;
        View focusView=null;

        if(TextUtils.isEmpty(email) || !(email.contains("@") && email.contains(".com")))
        {
            memail.setError("Enter valid Email address");
            focusView=memail;
            cancel=true;
        }

        else if(TextUtils.isEmpty(password))
        {
            mpass.setError("Enter valid password");
            focusView=mpass;
            cancel=true;
        }

        else if(TextUtils.isEmpty(mmobphone))
        {
            mobphone.setError("Enter valid contact no");
            focusView=mobphone;
            cancel=true;
        }

        else if(TextUtils.isEmpty(muser))
        {
            user.setError("Enter valid username");
            focusView=user;
            cancel=true;
        }

        if(cancel){
            mprogressDialog.hide();
            focusView.requestFocus();
        }
        else{
            createFirebaseUser(email,password);
        }

    }

    private void createFirebaseUser(final String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "Registered Successfully.Please check your email",
                                                Toast.LENGTH_SHORT).show();
                                        // email.setText("");
                                        //password.setText("");

                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("email", mail.getText().toString());
                                        map.put("username", user.getText().toString());
                                        map.put("mobile", mophone.getText().toString());
                                        map.put("password", pswd.getText().toString());
                                        map.put("description","None");
                                        map.put("type", type);

                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        users.child(uid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mprogressDialog.dismiss();
                                                Intent chatIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                chatIntent.putExtra("type",type);
                                                startActivity(chatIntent);
                                                finish();
                                            }
                                        });
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("code", "signInWithEmail:success");

                                    }


                                }
                            });
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            mprogressDialog.hide();
                            Log.w("code", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}


