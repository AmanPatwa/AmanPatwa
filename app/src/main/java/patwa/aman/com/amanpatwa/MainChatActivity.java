package patwa.aman.com.amanpatwa;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import patwa.aman.com.amanpatwa.R;


public class MainChatActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDataBaseReference;
    private ChatListAdapter mAdapter;
    private FirebaseDatabase mDatabase;
    private String type;
    private String uid;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        FirebaseApp.initializeApp(this);

        // TODO: Set up the display name and get the Firebase reference
       //String  event_data=getIntent().getStringExtra("event");
        type=getIntent().getStringExtra("type");
        setUpDisplayName();
        mDatabase=FirebaseDatabase.getInstance();
        mDataBaseReference =mDatabase.getReference("chats");


        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);


        mAuth=FirebaseAuth.getInstance();
        currentUser=FirebaseAuth.getInstance().getCurrentUser();


        // TODO: Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                sendMessage();
                return true;
            }
        });


        // TODO: Add an OnClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    // TODO: Retrieve the display name from the Shared Preferences
    private void setUpDisplayName()
    {
        if(type.equals("Admin")){
            mDisplayName = "Admin";
        }
        else if(currentUser != null){
            uid = mAuth.getCurrentUser().getUid();
            mDisplayName = uid;
        }
        if(currentUser == null){
            mDisplayName = "Anonymous";
        }



    }



    private void sendMessage() {

        // TODO: Grab the text the user typed in and push the message to Firebase
        String input = mInputText.getText().toString();
        if(!input.equals("")) {
            Log.d("PubChat","Message sent");
            InstantMessage chat = new InstantMessage(input, mDisplayName);
            mDataBaseReference.child("messages").push().setValue(chat);
            mInputText.setText("");
            Log.d("PubChat","ok");
        }
    }

    // TODO: Override the onStart() lifecycle method. Setup the adapter here.
    @Override
    public void onStart(){
        super.onStart();
        mAdapter = new ChatListAdapter(this, mDataBaseReference, mDisplayName,type);
        mChatListView.setAdapter(mAdapter);

    }


    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanUp();

        // TODO: Remove the Firebase event listener on the adapter.

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.chat_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.chat_logout)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(MainChatActivity.this,StartActivity.class);
            intent.putExtra("type",type);
            startActivity(intent);
            return true;

        }
        return false;
    }
}

