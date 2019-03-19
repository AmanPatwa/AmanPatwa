package patwa.aman.com.amanpatwa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity
{
    EditText pswd,mail;
    TextView signup,login;
    ProgressDialog mprogressDialog;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String type;


    @Override
    protected void onStart() {
        super.onStart();
//        if(currentUser != null && type.equals("Admin")){
//            Intent loginIntent=new Intent(LoginActivity.this,MainChatActivity.class);
//            loginIntent.putExtra("type",type);
//            startActivity(loginIntent);
//            finish();
//        }
        if(currentUser != null && type.equals("Personel") && currentUser.isEmailVerified()){
            Intent loginIntent = new Intent(LoginActivity.this, MainChatActivity.class);
            loginIntent.putExtra("type",type);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);


        login = (TextView) findViewById(R.id.login_login);
        mail = (EditText) findViewById(R.id.login_user);
        pswd = (EditText) findViewById(R.id.login_pswrd);
        signup = (TextView) findViewById(R.id.login_signup);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mprogressDialog=new ProgressDialog(this);
        /*Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/LatoLight.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        login.setTypeface(custom_font1);
        signup.setTypeface(custom_font);
        user.setTypeface(custom_font);
        pswd.setTypeface(custom_font);*/

        Intent intent=getIntent();
        type=intent.getStringExtra("type");

        System.out.println("Type"+type);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mprogressDialog.setTitle("Loging In");
                mprogressDialog.setMessage("Please wait!!");
                mprogressDialog.setCanceledOnTouchOutside(false);

                signInUser(mail,pswd);
            }
        });

        if(type.equals("Admin")){
            signup.setVisibility(View.INVISIBLE);
        }

        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                i.putExtra("type", type);
                startActivity(i);
            }
        });


    }

    private void signInUser(EditText memail, EditText mpass) {
        String email,password;

        memail.setError(null);
        mpass.setError(null);

        email=memail.getText().toString().trim();
        password=mpass.getText().toString().trim();

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

        if(cancel){
            mprogressDialog.hide();
            focusView.requestFocus();
        }
        else{
            signInFirebaseUser(email,password);
        }


    }

    private void signInFirebaseUser(String email, String password) {

        if(type.equals("Admin") && email.equals("admin@gmail.com") && password.equals("admin")){
            mprogressDialog.dismiss();
            Intent loginIntent=new Intent(LoginActivity.this,MainChatActivity.class);
            loginIntent.putExtra("type",type);
            startActivity(loginIntent);
            finish();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if(currentUser!= null && currentUser.isEmailVerified()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("UpasanaMandir", "signInWithEmail:success");

                                    mprogressDialog.dismiss();

                                    Intent loginIntent = new Intent(LoginActivity.this, MainChatActivity.class);
                                    loginIntent.putExtra("type",type);
                                    startActivity(loginIntent);
                                    finish();
                                }
                                else
                                {
                                    mprogressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Please verify or email", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else {
                                // If sign in fails, display a message to the user.
                                mprogressDialog.dismiss();
                                Log.w("UpasanaMandir", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }


    }

}
