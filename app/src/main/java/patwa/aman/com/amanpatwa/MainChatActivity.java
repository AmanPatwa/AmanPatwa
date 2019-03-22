package patwa.aman.com.amanpatwa;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import patwa.aman.com.amanpatwa.R;


public class MainChatActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton,mImgAdd;
    private DatabaseReference mDataBaseReference;
    private ChatListAdapter mAdapter;
    private FirebaseDatabase mDatabase;
    private String type;
    private String uid;
    private String user;
    String currentUser;
    private FirebaseAuth mAuth;
    private static final int GALLERY_PICK = 2;
    private String messagetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        FirebaseApp.initializeApp(this);

        // TODO: Set up the display name and get the Firebase reference
       //String  event_data=getIntent().getStringExtra("event");
        type=getIntent().getStringExtra("type");
        currentUser=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase=FirebaseDatabase.getInstance();
        setUpDisplayName();

        mDataBaseReference =mDatabase.getReference("chats");


        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);
        mImgAdd = (ImageButton)findViewById(R.id.chat_plus);
        messagetype="text";

        mAuth=FirebaseAuth.getInstance();

        System.out.println("current user "+currentUser);



        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("images/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
            }
        });



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
                setUpDisplayName();
                sendMessage();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK)
        {
            Uri imageUri = data.getData();
            messagetype="image";

            DatabaseReference imageRef = mDataBaseReference.child("messages").child(currentUser).push();
            String push_id=imageRef.getKey();

            StorageReference filepath = FirebaseStorage.getInstance().getReference("message_images").child(push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){
                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String downloadUrl = task.getResult().toString();
                                InstantMessage chat = new InstantMessage(downloadUrl, mDisplayName,type,messagetype);
                                chat.setMtype(type);
                                chat.setMesstype(messagetype);
                                mDataBaseReference.child("messages").push().setValue(chat);
                            }
                        });


                    }

                }
            });


        }
    }

    // TODO: Retrieve the display name from the Shared Preferences
    private void setUpDisplayName()
    {
        if(type.equals("Admin")){
            mDisplayName = "Admin";
        }
        else if(currentUser != null){
           // uid = mAuth.getCurrentUser().getUid();
            //Log.d("uid","PRINT:"+uid);

            DatabaseReference userData= mDatabase.getReference("users").child(currentUser);

            userData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user =  dataSnapshot.child("username").getValue(String.class);
                    System.out.println("Users:"+user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mDisplayName = user;
        }
        else if(currentUser == null){
            mDisplayName = "Anonymous";
        }



    }



    private void sendMessage() {

        // TODO: Grab the text the user typed in and push the message to Firebase
        String input = mInputText.getText().toString();
        if(!input.equals("")) {
            //Log.d("PubChat","Message sent");
            Log.d("gfhdh","Display"+mDisplayName);
            messagetype="text";
            InstantMessage chat = new InstantMessage(input, mDisplayName,type,messagetype);
            chat.setMtype(type);
            chat.setMesstype(messagetype);
            mDataBaseReference.child("messages").push().setValue(chat);
            mInputText.setText("");
            //Log.d("PubChat","ok");
        }
    }

    // TODO: Override the onStart() lifecycle method. Setup the adapter here.
    @Override
    public void onStart(){
        super.onStart();

        currentUser=FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference userData= mDatabase.getReference("users").child(currentUser);

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user =  dataSnapshot.child("username").getValue(String.class);
                System.out.println("User:"+user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDisplayName = user;
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

